package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper goodsMapper;

    /**
     * springTask定时任务
     * cron表达式（时间表达式）
     * 秒  分   时   日   月   周   年（Quartz）
     * 日和周必须有一个放弃（放弃标识是？）
     *
     * *   0  15   ?   *   2
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void seckillGoodsToRedis(){
        /*SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        System.out.println(format.format(new Date()));*/
        //javaMailSender

        //查询数据库，将满足条件的秒杀商品添加到redis中
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        //开始时间<=当前时间
        example.createCriteria().andStartTimeLessThanOrEqualTo(new Date())
                //结束时间>=当前时间
                .andEndTimeGreaterThanOrEqualTo(new Date())
                //库存数要大于0
                .andStockCountGreaterThan(0)
                //已审核的秒杀商品
                .andStatusEqualTo("1");
        List<TbSeckillGoods> seckillGoods = goodsMapper.selectByExample(example);

        for (TbSeckillGoods seckillGood : seckillGoods) {
            //将秒杀商品保存到redis中
            redisTemplate.boundHashOps("seckill_goods").put(seckillGood.getId(), seckillGood);

            //再次循环秒杀商品中的数量
            for (int i = 0; i < seckillGood.getStockCount(); i++) {
                //按照该商品的数量，创建一个list，一个商品一个list，list中的对象随意，但是按照该商品的库存数，循环创建的
                redisTemplate.boundListOps("seckill_goods_queue_" + seckillGood.getId()).leftPush(seckillGood.getId());
            }
        }

        System.out.println("导入了==="+seckillGoods.size());
    }
}
