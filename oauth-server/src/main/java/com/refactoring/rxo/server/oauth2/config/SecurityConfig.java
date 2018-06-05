package com.refactoring.rxo.server.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * spring security配置
 *
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 *          2017年10月16日
 *          在WebSecurityConfigurerAdapter不拦截oauth要开放的资源
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired(required=false)
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**", "/doc.html");
        web.ignoring().antMatchers("/health");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().antMatchers("/user/token", "/client/token").permitAll().antMatchers("/oauth/authorize").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll()
                .anyRequest().authenticated();

        // 基于密码 等模式可以无session,不支持授权码模式
        if (authenticationEntryPoint != null) {
            http.exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint);
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        } else {
            //授权码模式单独处理，需要session的支持，此模式可以支持所有oauth2的认证
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        }


        // http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        // 解决不允许显示在iframe的问题
        http.headers().frameOptions().disable();
        http.headers().cacheControl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}
