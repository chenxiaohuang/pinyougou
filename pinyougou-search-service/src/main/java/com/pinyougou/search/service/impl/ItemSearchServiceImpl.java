package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 用户名
 * @description 类型
 * @date 2019/8/21 0021
 */
@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {


        @Autowired
        private SolrTemplate solrTemplate;

        @Autowired
        private RedisTemplate redisTemplate;

        @Override
        public Map<String, Object> search(Map searchMap) {
            Map<String,Object> map=new HashMap<>();

            //1.查询列表   封装的功能方法searchCategoryList在下面
            map.putAll( searchList(searchMap));
            //2.分组查询商品分类列表
            List<String> categoryList = searchCategoryList(searchMap);
            map.put("categoryList",categoryList);
            //3.查询品牌和规格列表
            String categoryName=(String)searchMap.get("category");
            if(!"".equals(categoryName)){//如果有分类名称
                map.putAll(searchBrandAndSpecList(categoryName));
            }else{//如果没有分类名称，按照第一个查询
                if(categoryList.size()>0){
                    map.putAll(searchBrandAndSpecList(categoryList.get(0)));
                }
            }

            return  map;

        }
    /**
     * 根据关键字搜索列表
     * @param searchMap
     * @return map
     */
        private  Map searchList(Map searchMap){
            Map map=new HashMap();

            //高亮选项显示初始化
            HighlightQuery query=new SimpleHighlightQuery();
            //构建高亮选项对象
            HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域；这里可继续拼接addField添加高亮域
            highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
            highlightOptions.setSimplePostfix("</em>");//高亮后缀
            query.setHighlightOptions(highlightOptions);//设置高亮选项

            //1.1按照关键字查询        fieldname：参数是在solrhome配置文件中对应 《域》 的 《name》字符串
            Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
            query.addCriteria(criteria);

            //1.2按分类筛选
            if(!"".equals(searchMap.get("category"))){//如果用户选择了分类才进行筛选
                Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            //1.3按品牌筛选
            if(!"".equals(searchMap.get("brand"))){
                Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

            //1.4过滤规格
            if(searchMap.get("spec")!=null){
                Map<String,String> specMap= (Map) searchMap.get("spec");
                for(String key:specMap.keySet() ){//"item_spec_"+key：对应配置文件中的"item_spec_*"
                    Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                    FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
            }

            //1.5按价格筛选.....
            if(!"".equals(searchMap.get("price"))){
                String[] price = ((String) searchMap.get("price")).split("-");//split分裂；切开
                if(!price[0].equals("0")){//如果区间起点不等于0
                    Criteria filterCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                    FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
                if(!price[1].equals("*")){//如果区间终点不等于*
                    Criteria filterCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
                    FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                    query.addFilterQuery(filterQuery);
                }
            }


            //**************     获取高亮结果集      *****************************
            //高亮页对象
            HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

            //高亮入口集合（每条记录的高亮入口）
            List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

            for(HighlightEntry<TbItem> h: entryList){//循环高亮入口集合
                TbItem item = h.getEntity();//获取原实体类
                if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
                    item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
                }
            }

            map.put("rows",page.getContent());
            return map;
        }


    /**
     * 查询分类列表
     * @param searchMap
     * @return
     *  select 'category' from tb_item where ... group by category
     */
    private  List searchCategoryList(Map searchMap){//最初的参数：例如传入一个手机
        List<String> list=new ArrayList();
        Query query=new SimpleQuery();
        //按照关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");//相当于group by
        query.setGroupOptions(groupOptions);
        //得到分组页 包含很多个分组结果
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for(GroupEntry<TbItem> entry:content){
            list.add(entry.getGroupValue());//将分组结果的名称封装到返回值中
        }
        return list;
    }

    /**
     * 根据商品分类名称，查询品牌和规格列表
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category){
        HashMap map = new HashMap();
        //1.根据商品分类名称得到模板id
        Long templateId = (Long)redisTemplate.boundHashOps("itemCat").get(category);
        if (templateId!=null){
            //2.根据模板Id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList",brandList);
            System.out.println("品牌列表条数："+brandList.size());

            //3.根据模板Id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList",specList);
            System.out.println("规格列表条数："+specList.size());
        }

        return map;
    }

}

