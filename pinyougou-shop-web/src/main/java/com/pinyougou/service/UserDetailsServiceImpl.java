package com.pinyougou.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 用户详情
 * @description 认证类； 类型
 * @date 2019/8/7 0007
 */

public class UserDetailsServiceImpl  implements UserDetailsService {

    private SellerService sellerService;

    public void  setSellerService(SellerService sellerService){
        this.sellerService=sellerService;
    }

//    @Reference//从注册中心远程引用
//    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过了UserDetailsServiceImpl");
        //构建角色列表
        List<GrantedAuthority> grantAuths = new ArrayList();
        grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //查询是否存在商家
        TbSeller seller = sellerService.findOne(username);
        if(seller!=null&&seller.getStatus().equals("1")){//这是没查到该用户

            return new User(username,seller.getPassword(),grantAuths);
        }else {
            return null;
        }
    }
}
