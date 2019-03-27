package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbAddress;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface AddressService {

	/**
	 * 根据用户名查询该用户的列表集合
	 * @param userId
	 * @return
	 */
	List<TbAddress> findListByUserId(String userId);
}
