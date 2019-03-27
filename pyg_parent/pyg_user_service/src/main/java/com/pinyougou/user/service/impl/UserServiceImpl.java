package com.pinyougou.user.service.impl;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.pinyougou.user.service.UserService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.pojo.TbUserExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import util.HttpClientUtil;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private TbUserMapper userMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private ActiveMQQueue smsQueue;

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());
		user.setUpdated(new Date());

		//先将密码进行加密处理
		String password = DigestUtils.md5Hex(user.getPassword());//对密码加密，apache框架的
		user.setPassword(password);
		userMapper.insert(user);
	}

	@Override
	public void sendSms(String phone) {

		try {
			//通过RandomStringUtils工具类生成指定个数的随机数
			String code = RandomStringUtils.randomNumeric(6);
			//将验证码存入redis中,小key是手机号，内容是验证码
			redisTemplate.boundHashOps("phoneCode").put(phone, code);

			System.out.println("短信验证码是1===="+code);

			String phoneCode = (String) redisTemplate.boundHashOps("phoneCode").get(phone);
			System.out.println("==redis存的code="+phoneCode);

			//redisTemplate.boundHashOps("test").expire(5000, TimeUnit.MILLISECONDS);
			//发jms消息的方式
			jmsTemplate.send(smsQueue, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					//手机号和验证码
					MapMessage message = session.createMapMessage();
					message.setString("phone", phone);
					message.setString("code", code);
					return message;
				}
			});


			//准备httpclient发送  放弃
			/*HttpClientUtil util = new HttpClientUtil("http://localhost:9002/sms.do?phone=" + phone + "&code=" + code);
			//发送get请求
			util.get();
			//获取请求后的返回值
			String content = util.getContent();
			System.out.println(content);*/
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean checkSmsCode(String phone, String inputCode) {

		//根据手机号获取验证码
		String code = (String) redisTemplate.boundHashOps("phoneCode").get(phone);
		//如果redis中有验证码，并且输入的验证码和redis的验证码一致
		if(code!=null && inputCode.equals(code)){
			return true;
		}

		return false;
	}


}
