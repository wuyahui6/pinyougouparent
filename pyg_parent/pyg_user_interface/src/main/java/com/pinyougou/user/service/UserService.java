package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	/**
	 * 增加
	*/
	public void add(TbUser user);

	/**
	 * 发送短信服务
	 * @param phone
	 */
	public void sendSms(String phone);

	/**
	 * 验证手机短信验证码
	 */
	public boolean checkSmsCode(String phone,String inputCode);

}
