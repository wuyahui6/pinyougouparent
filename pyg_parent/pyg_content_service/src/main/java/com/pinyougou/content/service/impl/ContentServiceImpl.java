package com.pinyougou.content.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);

		//删除了当前分类下的缓存数据
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){


		//查询数据库的广告数据
		TbContent dbContent = contentMapper.selectByPrimaryKey(content.getId());


		if (dbContent.getCategoryId() != content.getCategoryId()){ //切换了广告分类,不等的时候是切换了分支
			redisTemplate.boundHashOps("content").delete(dbContent.getCategoryId());
			System.out.println("==清除了缓存==");
		}


		contentMapper.updateByPrimaryKey(content);

		//删除当前分类广告数据
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());

		System.out.println("==清除了缓存1111==");
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//注意顺序，先清缓存
			TbContent content = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());

			//再删除数据库
			contentMapper.deleteByPrimaryKey(id);
		}

		/*//清除缓存
		redisTemplate.delete("content");*/
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategoryId(Long id) {
		//采用redis缓存
		List<TbContent> contents = (List<TbContent>) redisTemplate.boundHashOps("content").get(id);
		if(contents == null){
			//根据分类id查询tbContent
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();

			criteria.andCategoryIdEqualTo(id);

			//修改状态
			criteria.andStatusEqualTo("1"); //1为正常，其他为不正常

			//查询条件中加入排序
			example.setOrderByClause("sort_order desc"); //排序中的条件是字段名

			contents = contentMapper.selectByExample(example);


			//存入redis缓存
			redisTemplate.boundHashOps("content").put(id, contents);

			System.out.println("==从MySQL中获取===");
		}else{
			System.out.println("==从Redis中获取===");
		}
		return contents;
	}

}
