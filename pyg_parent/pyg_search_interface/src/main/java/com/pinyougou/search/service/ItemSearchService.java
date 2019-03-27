package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    //solr搜索方法，参数是map类型，返回值是map
    public Map search(Map searchMap);

    /**
     * 根据goods的id导入对象的所有tbItem对象
     * @param ids
     */
    public void importItemToSolr(Long[] ids);

    /**
     * 根据goods的id的对象的移除tbItem对象从solr库
     * @param ids
     */
    public void removeItemFromSolr(Long[] ids);
}
