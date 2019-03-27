package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;

import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

	@Autowired
	private TbSeckillOrderMapper seckillOrderMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private ThreadPoolTaskExecutor executor;

	@Autowired
	private CreateOrder createOrder;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillOrder> findAll() {
		return seckillOrderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillOrder> page=   (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillOrder seckillOrder) {
		seckillOrderMapper.insert(seckillOrder);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillOrder seckillOrder){
		seckillOrderMapper.updateByPrimaryKey(seckillOrder);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillOrder findOne(Long id){
		return seckillOrderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillOrderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillOrderExample example=new TbSeckillOrderExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillOrder!=null){			
						if(seckillOrder.getUserId()!=null && seckillOrder.getUserId().length()>0){
				criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(seckillOrder.getSellerId()!=null && seckillOrder.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(seckillOrder.getStatus()!=null && seckillOrder.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(seckillOrder.getReceiverAddress()!=null && seckillOrder.getReceiverAddress().length()>0){
				criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(seckillOrder.getReceiverMobile()!=null && seckillOrder.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(seckillOrder.getReceiver()!=null && seckillOrder.getReceiver().length()>0){
				criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(seckillOrder.getTransactionId()!=null && seckillOrder.getTransactionId().length()>0){
				criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
		
		Page<TbSeckillOrder> page= (Page<TbSeckillOrder>)seckillOrderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void saveSeckillOrder(Long goodsId, String userId) {

		Boolean member = redisTemplate.boundSetOps("seckill_log_" + goodsId).isMember(userId);
		if(member){
			throw new RuntimeException("已经购买商品，请勿重复购买！！！");
		}

		//通过redis的list类型解决超卖问题
		Object object = redisTemplate.boundListOps("seckill_goods_queue_" + goodsId).rightPop();
		if(object==null){
			throw new RuntimeException("商品卖光!秒杀活动结束!!!");
		}

		//通过商品的id获取秒杀商品对象
		TbSeckillGoods goods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(goodsId);
		System.out.println("goods:"+goods.getTitle());
		if(goods == null || goods.getStockCount() < 1){
			throw new RuntimeException("秒杀活动结束!!!");
		}

		goods.setStockCount(goods.getStockCount() - 1);

		//通过redis的set类型解决重复购物问题,一种秒杀商品一个set集合
		redisTemplate.boundSetOps("seckill_log_"+goodsId).add(userId);

		//保存redis中,库存剩余数以redis中的库存数为准
		redisTemplate.boundHashOps("seckill_goods").put(goodsId, goods);

		if(goods.getStockCount() < 1){
			redisTemplate.boundHashOps("seckill_goods").delete(goodsId); //从redis中移除
		}


		//完成秒杀订单的生成
		TbSeckillOrder order = new TbSeckillOrder();
		order.setSellerId(goods.getSellerId()); //商家id
		order.setCreateTime(new Date());  //创建秒杀单时间
		order.setMoney(goods.getCostPrice());  //秒杀价格
		order.setSeckillId(goods.getId());
		order.setStatus("0");    //0未付款  1已付款
		order.setUserId(userId);   //哪个用户下的单
		order.setId(idWorker.nextId());   //采用雪花算法

		redisTemplate.boundListOps("seckill_order").leftPush(order);

		//seckillOrderMapper.insert(order);  //保存数据库
		//通过线程解决，主线程卡顿问题
		executor.execute(createOrder); //执行线程类
	}

}
