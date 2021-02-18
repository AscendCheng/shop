package org.cyx.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import lombok.Getter;
import lombok.Setter;
import org.cyx.entity.OssBucketDto;
import org.cyx.entity.OssFileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description OssClient
 * @Author cyx
 * @Date 2021/2/16
 **/
@Getter
@Setter
@Component
public class OssClient {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Autowired
    private OssBuilder ossBuilder;

    @Value("${oss.secretId}")
    private String secretId;

    @Value("${oss.secretKey}")
    private String secretKey;

    @Value("${oss.region}")
    private String region;

    /**
     * 创建存储桶
     *
     * @param bucketName
     */
    public Bucket creatBucket(String bucketName) {
        COSClient cosClient = initCosClient();
        if (cosClient == null || bucketName == null) return null;
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
        createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
        try {

            Bucket bucketResult = cosClient.createBucket(createBucketRequest);
            close(cosClient);
            return bucketResult;
        } catch (CosServiceException serverException) {
            serverException.printStackTrace();
        } catch (CosClientException clientException) {
            clientException.printStackTrace();
        }
        return null;
    }

    /**
     * 查询存储桶列表
     */
    public List<OssBucketDto> listBucket() {
        COSClient cosClient = initCosClient();

        if (cosClient == null) {
            return new ArrayList<>();
        }
        List<OssBucketDto> result = new ArrayList<>();
        for (Bucket item : cosClient.listBuckets()) {
            result.add(new OssBucketDto(item.getName(), item.getLocation()));
        }
        close(cosClient);
        return result;
    }

    /**
     * 上传文件
     *
     * @param multipartFile
     * @param bucketName
     * @param keyPrefix
     */
    public PutObjectResult uploadFile(MultipartFile multipartFile, String bucketName, String keyPrefix) {
        COSClient cosClient = initCosClient();
        try (InputStream ins = multipartFile.getInputStream()) {
            String originalFileName = multipartFile.getOriginalFilename();
            File toFile = multipartFileToFile(multipartFile);
            String key = keyPrefix + dtf.format(LocalDateTime.now()) + "/" + originalFileName;
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, toFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            close(cosClient);
            return putObjectResult;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询对象列表
     */
    public List<OssFileDto> listOssFile(String bucketName, String prefix, String delimiter) {
        COSClient cosClient = initCosClient();
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置bucket名称
        listObjectsRequest.setBucketName(bucketName);
        // prefix表示列出的object的key以prefix开始
        listObjectsRequest.setPrefix(prefix);
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter(delimiter);
        // 设置最大遍历出多少个对象, 一次listobject最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;
        List<OssFileDto> result = new ArrayList<>();
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return result;
            } catch (CosClientException e) {
                e.printStackTrace();
                return result;
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
                result.add(new OssFileDto(fileSize, key, etag, storageClasses));
            }
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
        close(cosClient);
        return result;
    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param key
     * @param outputFilePath
     */
    public ObjectMetadata download(String bucketName, String key, String outputFilePath) {
        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式;
        // 方法1 获取下载输入流
        COSClient cosClient = initCosClient();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        COSObject cosObject = cosClient.getObject(getObjectRequest);
        ObjectMetadata downObjectMeta = null;
        try (COSObjectInputStream cosObjectInput = cosObject.getObjectContent()) {
            // 下载对象的 CRC64
            String crc64Ecma = cosObject.getObjectMetadata().getCrc64Ecma();
            // 方法2 下载文件到本地
            File downFile = new File(outputFilePath);
            getObjectRequest = new GetObjectRequest(bucketName, key);
            downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close(cosClient);
        return downObjectMeta;
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param key
     */
    public boolean delete(String bucketName, String key) {
        COSClient cosClient = initCosClient();
        cosClient.deleteObject(bucketName, key);
        return true;
    }

    private void close(COSClient cosClient) {
        cosClient.shutdown();
    }

    private COSClient initCosClient() {
        return ossBuilder.build(this.secretId, this.secretKey, region);
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        if (!multipartFile.equals("") && multipartFile.getSize() > 0) {
            String fileName = multipartFile.getOriginalFilename();
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            File file = File.createTempFile(prefix, suffix);
            multipartFile.transferTo(file);
            return file;
        }
        return null;
    }
}
