package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private TbPayLogMapper payLogMapper;

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//购物车列表转成订单，一个购物车一个订单
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

		double payMoney = 0.0;
		List orderIds = new ArrayList<>();  //保存订单id的集合

		for (Cart cart : cartList) {
			double totalMoney = 0.0;

			TbOrder dbOrder = new TbOrder();
			dbOrder.setUserId(order.getUserId()); //保存谁下的订单
			dbOrder.setSourceType("2");  //'订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端',
			dbOrder.setReceiverMobile(order.getReceiverMobile()); //收货人电话
			dbOrder.setReceiver(order.getReceiver());    //收货人
			dbOrder.setReceiverAreaName(order.getReceiverAreaName());  //收货地址
			dbOrder.setCreateTime(new Date());
			dbOrder.setPaymentType(order.getPaymentType());  //支付方式
			dbOrder.setUpdateTime(new Date());

			long orderId = idWorker.nextId();
			dbOrder.setOrderId(orderId);     //利用雪花算法完成id生成
			orderIds.add(orderId);          //将订单id放入集合

			//订单明细
			List<TbOrderItem> orderItemList = cart.getOrderItemList();//购物车明细
			for (TbOrderItem orderItem : orderItemList) {
				totalMoney += orderItem.getTotalFee().doubleValue(); //商品金额累计

				//设置id，以及多对一关系即可
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);  //多对一关系
				orderItemMapper.insert(orderItem);
			}
			//循环购物车中所有的明细求总金额
			dbOrder.setPayment(new BigDecimal(totalMoney));   //支付总金额是当前订单的总金额
			dbOrder.setSellerId(cart.getSellerId());  //商家id在购物车上
			dbOrder.setStatus("1");  // '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价',

			payMoney += totalMoney;  //累加每张订单的总金额，就是支付单总金额

			//订单保存到数据库
			orderMapper.insert(dbOrder);
		}

		//清空该用户的购物车
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());


		//生产支付单对象，并且保存数据库
		TbPayLog payLog = new TbPayLog();
		payLog.setOutTradeNo(idWorker.nextId()+""); //支付单号采用雪花算法
		payLog.setCreateTime(new Date());
		payLog.setTotalFee((long)(payMoney * 100));  //这里是分
		payLog.setUserId(order.getUserId());  //哪个用户的支付单
		payLog.setTradeState("0");       //0未支付  1已支付

		//[xxxx ,xxxx ,xxxx, xxxxx] 去掉左右括号，去掉，号中间的空格
		payLog.setOrderList(orderIds.toString().replace("[","" ).replace("]","" ).replaceAll(" ", ""));
		payLog.setPayType("1");     //1，微信支付  2.货到付款

		payLogMapper.insert(payLog);  //将未支付的支付单保存数据库

		//将payLog放入redis
		redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);
	}

	
}
