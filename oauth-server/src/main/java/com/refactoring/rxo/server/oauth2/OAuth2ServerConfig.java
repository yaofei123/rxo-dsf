
package com.refactoring.rxo.server.oauth2;

import com.refactoring.rxo.server.oauth2.store.RedisTemplateTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 */
@Configuration
public class OAuth2ServerConfig {

    private Logger logger = LoggerFactory.getLogger(OAuth2ServerConfig.class);

    @Resource
    private DataSource dataSource;

    @Bean // 声明 ClientDetails实现
    @ConditionalOnProperty(prefix = "security.oauth2.token.store", name = "type", havingValue = "redis", matchIfMissing = true)
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * @author owen 624191343@qq.com
     * @version 创建时间：2017年11月12日 上午22:57:51
     *          默认token存储在内存中
     *          DefaultTokenServices默认处理
     */
    @Component
    @Configuration
    @EnableAuthorizationServer
    public class UnieapAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
        /**
         * 注入authenticationManager 来支持 password grant type
         */
        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private UserDetailsService userDetailsService;

        @Autowired(required = false)
        private RedisTemplateTokenStore redisTokenStore;

        @Autowired(required = false)
        private JwtTokenStore jwtTokenStore;
        @Autowired(required = false)
        private JwtAccessTokenConverter jwtAccessTokenConverter;

        @Autowired
        private WebResponseExceptionTranslator webResponseExceptionTranslator;

        @Autowired
        private ClientDetailsService clientDetailsService;


        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


            logger.info("redisTokenStore===================" + redisTokenStore);

            if (jwtTokenStore != null) {
                endpoints.tokenStore(jwtTokenStore).authenticationManager(authenticationManager)
                        .userDetailsService(userDetailsService); // 支持
                // password
                // grant
                // type;
            } else if (redisTokenStore != null) {
                endpoints.tokenStore(redisTokenStore).authenticationManager(authenticationManager)
                        .userDetailsService(userDetailsService); // 支持
                // password
                // grant
                // type;
            }

            if (jwtAccessTokenConverter != null) {
                endpoints.accessTokenConverter(jwtAccessTokenConverter);
            }
            endpoints.exceptionTranslator(webResponseExceptionTranslator);

        }

        // 配置应用名称 应用id
        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetailsService);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

            // url:/oauth/token_key,exposes
            security.tokenKeyAccess("permitAll()")
                    /// public key for token
                    /// verification if using
                    /// JWT tokens
                    // url:/oauth/check_token
                    .checkTokenAccess("isAuthenticated()")
                    // allow check token
                    .allowFormAuthenticationForClients();

            // security.allowFormAuthenticationForClients();
            //// security.tokenKeyAccess("permitAll()");
            // security.tokenKeyAccess("isAuthenticated()");
        }

    }
}
