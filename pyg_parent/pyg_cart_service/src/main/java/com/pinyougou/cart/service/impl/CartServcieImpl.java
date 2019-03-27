package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServcieImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public List<Cart> addTbItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //将itemId获取item对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);

        //判断是否已经存在当前购物车，根据sellerId是否已经在cartList中
        Cart cart = findCartFromCartList(cartList, item.getSellerId());

        if(cart == null){ //没找到购物车
            //将item对象编程OrderItem
            TbOrderItem orderItem = setValue(item, num);

            //将orderItem存到cart.orderItemList
            List<TbOrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem); //商品放入商品明细列表

            //创建购物车
            cart = new Cart();
            cart.setSellerId(item.getSellerId()); //购物车需要设置商家id
            cart.setSellerName(item.getSeller());  //设置商家名称
            cart.setOrderItemList(orderItems);
            cartList.add(cart);  //购物车放到购物车列表

        }else{ //找到购物车
            //再判断该商品是否已经存在商品列表中
            TbOrderItem orderItem = findOrderItemFromList(cart.getOrderItemList(), itemId);
            if(orderItem == null){ //第一次加该商品
                orderItem = setValue(item, num);  //将item变成orderItem
                cart.getOrderItemList().add(orderItem); //将orderItem加入到购物车的明细列表
            }else{ //找到该商品了
                orderItem.setNum(orderItem.getNum()+num); //购买数量累加
                //重新设置总金额
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));

                if(orderItem.getNum() < 1){ //商品不想买
                    cart.getOrderItemList().remove(orderItem); //从购物车明细列表中移除，明细对象
                }
                if(cart.getOrderItemList().size() < 1){
                    cartList.remove(cart); //如果该商家下的购物车，什么都没有，直接将购物车从购物车列表里移除
                }
            }
        }
        return cartList;
    }


    public TbOrderItem setValue(TbItem item,Integer num){
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setPrice(item.getPrice());
        orderItem.setTotalFee(new BigDecimal(num * item.getPrice().doubleValue())); //总金额 数量*单价
        orderItem.setNum(num); //数量
        orderItem.setTitle(item.getTitle());
        orderItem.setPicPath(item.getImage()); //唯一的一张图片
        orderItem.setSellerId(item.getSellerId()); //商家id
        orderItem.setItemId(item.getId());  //这个id很重要是明细对应的sku的id
        orderItem.setGoodsId(item.getGoodsId());  //商品id
        return orderItem;
    }

    /**
     * 购物车列表，商家id，根据商家id查询是否该id已经存在购物车列表中
     * @param cartList
     * @param sellerId
     * @return
     */
    public Cart findCartFromCartList(List<Cart> cartList,String sellerId){
        //循环一遍购物车
        for (Cart cart : cartList) {
            if(sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }

    public TbOrderItem findOrderItemFromList(List<TbOrderItem> orderItems,Long itemId){
        for (TbOrderItem orderItem : orderItems) {
            if(orderItem.getItemId().equals(itemId)){ //找到了该orderItem的（itemId）
                return orderItem;
            }
        }
        return null;
    }

    @Override
    public List<Cart> getCartListByUserId(String key) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(key);
        if(cartList == null){ //空购物车
            cartList = new ArrayList<Cart>(); //防止是null值添加商品崩溃
        }
        return cartList;
    }

    @Override
    public void setCartListByUserId(String key,List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(key, cartList);
    }

    @Override
    public void deleCartListByUserId(String key) {
        redisTemplate.boundHashOps("cartList").delete(key);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //先循环购物车
        for (Cart cart : cartList1) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //每一个购物车的商品
            for (TbOrderItem orderItem : orderItemList) {
                //将cartList1中所有商品合并到cartList2中了
                cartList2 = addTbItemToCartList(cartList2,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList2;
    }
}
