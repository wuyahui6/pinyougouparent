package com.pinyougou.seckill.service.impl;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CreateOrder implements Runnable{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillOrderMapper orderMapper;

    @Autowired
    private TbSeckillGoodsMapper goodsMapper;

    @Override
    public void run() {
        //从redis中获取秒杀订单
        TbSeckillOrder order = (TbSeckillOrder) redisTemplate.boundListOps("seckill_order").rightPop();
        //由线程类保存秒杀订单insert
        orderMapper.insert(order); //保存数据库

        //修改秒杀商品数量update
        TbSeckillGoods goods = goodsMapper.selectByPrimaryKey(order.getSeckillId());
        goods.setStockCount(goods.getStockCount() - 1);
        goodsMapper.updateByPrimaryKey(goods);

    }
}
