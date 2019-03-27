package com.pinyougou.order.service;
import java.util.List;
import com.pinyougou.pojo.TbOrder;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	

}
