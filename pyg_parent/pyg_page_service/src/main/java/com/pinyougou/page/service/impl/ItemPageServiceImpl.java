package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer configurer;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pagedir}")
    private String pageDir;

    @Override
    public void createHtml(Long goodsId) {
         //根据商品id获取商品对象
        try {
            Configuration configuration = configurer.getConfiguration();
            //获取模版
            Template template = configuration.getTemplate("item.ftl");
            FileWriter writer = new FileWriter(new File(("d:/item/" + goodsId + ".html")));

            //将商品及商品详情对象赋值给dataModel
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);

            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

            //获取三级分类的名称
            String category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();
            //封装map生成对象
            Map map = new HashMap();
            map.put("goods", goods);
            map.put("goodsDesc", goodsDesc);
            map.put("category1", category1);
            map.put("category2", category2);
            map.put("category3", category3);

            //封装itemList
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc"); //排序，参数是字段名 ,这样list的第一个是默认选中的规格
            List<TbItem> items = itemMapper.selectByExample(example);

            //将sku列表封装到itemList中
            map.put("itemList", items);


            //写出文件
            template.process(map, writer);
            //关闭资源
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeHtml(Long[] ids) {
        for (Long goodsId : ids) {
            //删除指定路径下的文件
            new File("d:/item/" + goodsId + ".html").delete();
        }
    }
}
