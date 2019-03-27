package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.PhoneFormatCheckUtils;

import java.util.List;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference(timeout = 100000)
	private UserService userService;
	

	@RequestMapping("/showName")
	public String showName(){
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}


	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){

		//需要在注册用户之前验证短信验证码
		/*boolean smsCode = userService.checkSmsCode(user.getPhone(), code);
		if(!smsCode){//返回的是false
			return new Result(false, "短信验证码输入错误！！！");
		}*/

		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	@RequestMapping("/sendSms")
	public Result sendSms(String phone){
		boolean legal = PhoneFormatCheckUtils.isPhoneLegal(phone);
		if(!legal){
			return new Result(false, "手机号格式不正确！！！");
		}

		try {
			userService.sendSms(phone);
			return new Result(true, "发送短信成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "发送短信失败");
		}
	}
	
}
