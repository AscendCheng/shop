#登录腾讯云镜像仓
docker login --username=100009559800  ccr.ccs.tencentyun.com/cyx-dev/shop --password=qwerty123

#构建common项目
cd ../common-service
mvn install

#构建网关
cd ../gateway-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag cyx-cloud/gateway-service:latest ccr.ccs.tencentyun.com/cyx-dev/shop-gateway-service:v1.2
docker push ccr.ccs.tencentyun.com/cyx-dev/shop-gateway-service:v1.2
echo "网关构建成功"

#构建优惠券服务
cd ../coupon-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag cyx-cloud/coupon-service:latest ccr.ccs.tencentyun.com/cyx-dev/shop-coupon-service:v1.2
docker push ccr.ccs.tencentyun.com/cyx-dev/shop-coupon-service:v1.2
echo "优惠券服务构建成功"


#构建用户服务
cd ../user-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag cyx-cloud/user-service:latest ccr.ccs.tencentyun.com/cyx-dev/shop-user-service:v1.2
docker push ccr.ccs.tencentyun.com/cyx-dev/shop-user-service:v1.2
echo "用户服务构建成功"


#构建商品服务
cd ../product-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag cyx-cloud/product-service:latest ccr.ccs.tencentyun.com/cyx-dev/shop-product-service:v1.2
docker push ccr.ccs.tencentyun.com/cyx-dev/shop-product-service:v1.2
echo "商品服务构建成功"


#构建订单服务
cd ../order-service
mvn install -Dmaven.test.skip=true dockerfile:build
docker tag cyx-cloud/order-service:latest ccr.ccs.tencentyun.com/cyx-dev/shop-order-service:v1.2
docker push ccr.ccs.tencentyun.com/cyx-dev/shop-order-service:v1.2
echo "订单服务构建成功"
