package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/findSeckillGoods")
    public List<TbSeckillGoods> findSeckillGoods(){
        return seckillGoodsService.findAll();
    }

    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long id){
        return seckillGoodsService.findOne(id);
    }

    @RequestMapping("/saveSeckillOrder")
    public Result saveSeckillOrder(Long goodsId){

        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();

            if("anonymousUser".equals(userId)){
                return new Result(false, "秒杀商品必须登录");
            }

            seckillOrderService.saveSeckillOrder(goodsId, userId);

            return new Result(true, "秒杀成功！！！");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "秒杀失败！！！");
        }
    }

}
