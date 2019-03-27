package com.pinyougou.pay.service;

import com.pinyougou.pojo.TbPayLog;

import java.util.Map;

public interface PayService {

    /**
     * 返回一个封装好的map
     * @param out_trade_no 支付单号
     * @param total_fee   支付金额
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 查询订单状态
     * @param out_trade_no 支付单号
     * @return
     */
    public Map queryPayStatus(String out_trade_no);


    /**
     * 根据key查询redis中的支付单对象
     */
    public TbPayLog searchPayLogFromRedis(String key);

    /**
     * 修改支付单状态，修改订单状态
     * @param out_trade_no  支付单号
     * @param transaction_id  微信返回的业务代码
     */
    public void updateOrderStatus(String out_trade_no,String transaction_id);
}
