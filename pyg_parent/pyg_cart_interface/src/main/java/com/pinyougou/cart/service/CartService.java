package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {

    /**
     * 将itemId和数量，传入该方法，再传入购物车列表，返回添加好商品的购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addTbItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 根据传入的key获取购物车列表从redis中
     * @param key
     * @return
     */
    public List<Cart> getCartListByUserId(String key);

    /**
     * 根据key保存购物车列表
     * @param key
     */
    public void setCartListByUserId(String key,List<Cart> cartList);


    /**
     * 根据key删除购物车列表
     */
    public void deleCartListByUserId(String key);

    /**
     * 合并两个购物车返回合并后的购物车
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
}
