package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);		
		//调用封装的缓存处理方法
		saveToRedis();

		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private RedisTemplate redisTemplate;

	//封装的缓存处理方法
	private void  saveToRedis(){
		//获取模板数据
		List<TbTypeTemplate> typeTemplateList = findAll();
		//循环模板
		for(TbTypeTemplate typeTemplate :typeTemplateList){
			//存储品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);//这里是从表中获取集合的字符串需要转换
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
			//存储规格列表
			List<Map> specList = findSpecList(typeTemplate.getId());//根据模板ID查询规格列表
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
		System.out.println("缓存品牌列表");

	}


	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	/**
	 * 查询type_template模板表查询规格集合
	 * @param id
	 * @return
	 */
	@Override
	public List<Map> findSpecList(Long id) {
		//查询模板
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);//得到一条模板表数据对象
		//[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();//获取一条表数据对象的一个对应字段对应内容
        //specIds = " [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}] "转成list=[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);//将表中获得的字符串对象转为list集合对象
        //因为下边需要遍历 需要获得id的值  根据id获取option   遍历拿出list集合数组中的一个map={"id":27,"text":"网络"}specification表对象
		for (Map map:list){//为了展开list集合往里面的map属性添加新的属性键值对options→27：[3g,4g...]
			//规格选项列表
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			com.pinyougou.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));//这个是spec_id在下一步根据这个id查询option表，添加下一步的标准sql语句条件
			//查询数据库
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);//是null就添加所有；不是null就根据上一步的条件查询

			map.put("options",options);//起个名字"options"，然后把查出来的options的集合存到map中
		}
		return list;
	}

}
