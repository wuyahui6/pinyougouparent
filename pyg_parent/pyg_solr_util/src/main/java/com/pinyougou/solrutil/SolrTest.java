package com.pinyougou.solrutil;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.*;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext*.xml")
public class SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;


    @Test
    public void testQuery(){

        //拼接query查询条件
        SimpleQuery simpleQuery = new SimpleQuery("*:*");

        Criteria criteria = new Criteria("item_title");//查询条件设置域的名字
        criteria = criteria.contains("三星"); //api需要重新赋值

        simpleQuery.addCriteria(criteria);

        //发送查询
        ScoredPage<TbItem> items = solrTemplate.queryForPage(simpleQuery, TbItem.class);

        List<TbItem> itemList = items.getContent();

        //循环显示
        for (TbItem item : itemList) {
            System.out.println(item.getTitle());
        }

    }

    @Test
    public void testDelete(){

        //全部删除
        SimpleQuery simpleQuery = new SimpleQuery("*:*");
        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();
    }
}
