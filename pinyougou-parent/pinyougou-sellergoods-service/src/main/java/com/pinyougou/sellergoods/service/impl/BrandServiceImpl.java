package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;

import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
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
}
