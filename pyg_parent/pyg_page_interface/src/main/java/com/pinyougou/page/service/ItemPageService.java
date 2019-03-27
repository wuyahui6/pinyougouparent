package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 通过商品id，创建对应的商品静态页面
     * @param goodsId
     */
    public void createHtml(Long goodsId);


    /**
     * 通过商品id，删除对应的商品静态页面
     * @param ids
     */
    public void removeHtml(Long[] ids);

}
