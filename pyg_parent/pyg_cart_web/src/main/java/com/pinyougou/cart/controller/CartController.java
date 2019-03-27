package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import entity.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    //从cookie中获取唯一的key值
    public String getUuid(){
        String uuid = CookieUtil.getCookieValue(request, "uuid", "utf-8");
        if(uuid == null || uuid.equals("")){ //cookie中没有任何内容
            uuid = UUID.randomUUID().toString();
            //存cookie中
            CookieUtil.setCookie(request, response, "uuid", uuid, 48*60*60, "utf-8");
        }
        return uuid;
    }

    /**
     * 将商品添加购物车
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addItemToCartList")
    //参数一，远程访问的地址,参数二，当需要操作cookie需要设置
    @CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
    public Result addItemToCartList(Long itemId,Integer num){

       /* //解决方法一：http://localhost:9109也可设置为*，但是*号不能用cookie,允许访问的域
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
        //----如果用了cookie信息-----，必须加后面这句话，如果不用cookie可以不加这句话
        response.setHeader("Access-Control-Allow-Credentials", "true");*/

        try {
            String uuid = getUuid();


            String loginName = SecurityContextHolder.getContext().getAuthentication().getName();

            if(!"anonymousUser".equals(loginName)){  //登录后的逻辑
                uuid = loginName; //uuid变成登录后的用户名id
            }

            //从redis中获取购物车
            List<Cart> cartList = cartService.getCartListByUserId(uuid);
            //商品添加购物车
            cartList = cartService.addTbItemToCartList(cartList, itemId, num);

            //将添加好商品的购物车存到redis中
            cartService.setCartListByUserId(uuid, cartList);

            return new Result(true, "添加购物车成功!!!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加购物车失败!!!");
        }
    }

    @RequestMapping("/findCartListFromRedis")
    public List<Cart> findCartListFromRedis(){
        String uuid = getUuid(); //获取唯一的key值

        //登录后uuid是从springSecurity获取唯一的key值
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(loginName);

        //从redis取购物车取未登录的购物车
        List<Cart> cartList = cartService.getCartListByUserId(uuid);

        if(!"anonymousUser".equals(loginName)){  //登录后的逻辑

            //在第一次登录的时候合并购物车,登录后的购物车
            List<Cart> cartListLogin = cartService.getCartListByUserId(loginName);

            //当未登录的购物车的size>0的时候，才合并
            if(cartList.size() > 0){
                //1.合并购物车
                cartList = cartService.mergeCartList(cartList, cartListLogin);
                //2.将合并的购物车保存redis
                cartService.setCartListByUserId(loginName, cartList);
                //3.清除未登录的购物车
                cartService.deleCartListByUserId(uuid);
            }else{
                cartList = cartListLogin; //如果未登录购物车已经空了，直接将登录购物车返回
            }
        }
        return cartList;
    }

}
