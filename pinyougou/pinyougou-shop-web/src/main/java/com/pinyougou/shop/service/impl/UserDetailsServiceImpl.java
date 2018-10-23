package com.pinyougou.shop.service.impl;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {
    private SellerService sellerService;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
       /* //静态登陆
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        return new User(userName,"123456",authorityList);*/
        //动态登陆
        //到数据可根据userName(主键)查询
        TbSeller seller = sellerService.findOne(userName);
        if(seller!=null&&"1".equals(seller.getStatus())){
            List<GrantedAuthority> authorityList = new ArrayList<>();
            authorityList.add(new SimpleGrantedAuthority("ROLE_SELLER"));
            return new User(userName,seller.getPassword(),authorityList);
        }
        return null;

    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
