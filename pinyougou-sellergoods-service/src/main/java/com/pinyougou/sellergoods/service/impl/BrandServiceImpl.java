package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 用户名
 * @description 类型
 * @date 2019/7/30 0030
 */
@Service//这里写dubbo的service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override//分页插件使用实现分页
    public PageResult findPage(int pageNum, int pageSize) {
        //分页插件PageHelper
        PageHelper.startPage(pageNum,pageSize);//分页

        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult() );
    }

    @Override//增加
    public void add(TbBrand brand) {

        brandMapper.insert(brand);
    }

    @Override
    public TbBrand findOne(Long id) {

        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(long[] ids) {
        for (long id : ids) {//遍历ids数组得到数组中的id
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {

        //分页插件PageHelper
        PageHelper.startPage(pageNum,pageSize);//分页
        //创建实例
        TbBrandExample example = new TbBrandExample();
        //创建标准
        TbBrandExample.Criteria criteria = example.createCriteria();
        //判断非空
        if (brand!=null){
            if (brand.getName()!=null && brand.getName().length()>0){
                //执行模糊查询（根据名字）
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null && brand.getFirstChar().length()>0){
                //执行模糊查询（根据首字母）
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }

        Page<TbBrand> page = (Page<TbBrand>)brandMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult() );
    }


}
