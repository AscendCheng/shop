package org.cyx.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.cyx.constant.CacheKey;
import org.cyx.enums.BizCodeEnum;
import org.cyx.exception.BizException;
import org.cyx.interceptor.LoginInterceptor;
import org.cyx.model.LoginUser;
import org.cyx.request.CartItemRequest;
import org.cyx.service.CartService;
import org.cyx.service.ProductService;
import org.cyx.util.JsonData;
import org.cyx.vo.CartItemVo;
import org.cyx.vo.CartVo;
import org.cyx.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Description CartServiceImpl
 * @Author cyx
 * @Date 2021/4/13
 **/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductService productService;

    @Override
    public JsonData addToCart(CartItemRequest cartItemRequest) {
        Long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        // 获取购物车
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        Object cacheObj = myCart.get(productId);
        String result = "";
        if (cacheObj != null) {
            result = (String) cacheObj;
        }
        if (StringUtils.isBlank(result)) {
            // 不存在新增购物项
            CartItemVo cartItemVo = new CartItemVo();
            ProductVO productVO = productService.findDetailById(productId.toString());
            if (productVO == null) {
                throw new BizException(BizCodeEnum.CART_FAIL);
            }
            cartItemVo.setAmount(productVO.getAmount());
            cartItemVo.setBuyNum(buyNum);
            cartItemVo.setProductId(productId);
            cartItemVo.setProductImg(productVO.getCoverImg());
            cartItemVo.setProductTitle(productVO.getTitle());
            myCart.put(productId, JSON.toJSONString(cartItemVo));
        } else {
            // 存在商品，修改数量
            CartItemVo cartItemVo = JSON.parseObject(result, CartItemVo.class);
            cartItemVo.setBuyNum(buyNum);
            myCart.put(productId, JSON.toJSONString(cartItemVo));
        }
        return JsonData.buildSuccess();
    }

    @Override
    public JsonData clearCart() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);
        return JsonData.buildSuccess();
    }

    @Override
    public CartVo findMyCart() {
        // 获取全部购物项
        List<CartItemVo> cartItemVoList = buildCartItem(false);
        // 封装成vo
        CartVo cartVo = new CartVo();
        cartVo.setCartItems(cartItemVoList);
        return cartVo;
    }

    @Override
    public void deleteProduct(Long productId) {
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        myCart.delete(productId);
    }

    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        String product = (String) myCart.get(cartItemRequest.getProductId());
        CartItemVo cartItemVo = JSON.parseObject(product, CartItemVo.class);
        cartItemVo.setBuyNum(cartItemRequest.getBuyNum());
        myCart.put(cartItemVo.getProductId(), JSON.toJSONString(cartItemVo));
    }

    @Override
    public List<CartItemVo> confirmOrderCartItems(List<Long> productIds) {
        // 获取购物车全部购物项
        List<CartItemVo> cartItemVoList = buildCartItem(true);
        // 根据需要的商品Id进行过滤，并清空购物项
        List<CartItemVo> resultList = cartItemVoList.stream().filter(obj -> {
            if (productIds.contains(obj.getProductId())) {
                this.deleteProduct(obj.getProductId());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return resultList;
    }

    private List<CartItemVo> buildCartItem(boolean latestPrice) {
        BoundHashOperations<String, Object, Object> myCart = getMyCartOps();
        List<Object> items = myCart.values();
        List<CartItemVo> myCartItems = new ArrayList<>();
        for (Object item : items) {
            CartItemVo itemVo = JSON.parseObject((String) item, CartItemVo.class);
            myCartItems.add(itemVo);
        }
        List<Long> productIdList = myCartItems.stream().map(CartItemVo::getProductId).collect(Collectors.toList());
        if (latestPrice) {
            setProductLatestPrice(myCartItems, productIdList);
        }
        return myCartItems;
    }

    private void setProductLatestPrice(List<CartItemVo> cartItemVoList, List<Long> productIdList) {
        List<ProductVO> productVOList = productService.findDetailByIdBatch(productIdList);

        // 分组
        Map<Long, ProductVO> productVOMap =
                productVOList.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity(), (k1, k2) -> k1));

        cartItemVoList.stream().forEach(cartItemVo -> {
            ProductVO productVO = productVOMap.get(cartItemVo.getProductId());
            cartItemVo.setProductTitle(productVO.getTitle());
            cartItemVo.setProductImg(productVO.getCoverImg());
            cartItemVo.setAmount(productVO.getAmount());
        });
    }

    private BoundHashOperations<String, Object, Object> getMyCartOps() {
        String cartkey = getCartKey();
        return redisTemplate.boundHashOps(cartkey);
    }

    private String getCartKey() {
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String cartKey = String.format(CacheKey.CART_KEY, loginUser.getId());
        return cartKey;
    }
}
