/*
 * Copyright (C) 2017 北京好药师大药房连锁有限公司(www.ehaoyao.com) , All Rights Reserved.
 */
package com.refactoring.rxo.hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * @Description API熔断看板
 * @Author
 * @Date 2017-12-23 11:03
 * @Modified by:
 **/
@EnableHystrixDashboard
@SpringBootApplication
public class HystrixDashboardServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardServerApplication.class, args);
	}
}
