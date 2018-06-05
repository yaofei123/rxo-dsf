package com.refactoring.rxo.server.oauth2.service;

import com.refactoring.rxo.member.entity.Account;
import com.refactoring.rxo.member.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


/**
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 *          类说明
 */
@Component
public class UnieapUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountService.getAccountInfoByUserName(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户" + username + "不存在!");
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(account.getUserName(), account.getPassword(), grantedAuthorities);
    }
}
