/**
 *
 */
package com.refactoring.rxo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author owen 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 *          类说明
 */

@EnableDiscoveryClient
@SpringBootApplication
public class OAuthServerApp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.sources(OAuthServerApp.class);
        return application;
    }


    public static void main(String[] args) {
        SpringApplication.run(OAuthServerApp.class, args);
    }

}
