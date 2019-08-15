package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 品牌接口
 *
 */
public interface BrandService {
    //查询TbBrand表所有
    public List<TbBrand> findAll();

    /**
     * 品牌分页
     * 参数1：pageNum  当前页面
     * 参数2：pageSize  每页记录数
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 增加
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 根据ID查询实体类
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 删除
     * @param ids //id数组
     */
    public void delete(long[] ids);

    /**
     * 品牌分页
     * 参数1：pageNum  当前页面
     * 参数2：pageSize  每页记录数
     * @return
     */
    public PageResult findPage(TbBrand brand, int pageNum,int pageSize);

    /**
     * 品牌下拉框数据
     */
    public List<Map> selectOptionList();
}
