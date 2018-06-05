/*
 * Copyright (C) 2017 北京好药师大药房连锁有限公司(www.ehaoyao.com) , All Rights Reserved.
 */
package com.refactoring.rxo.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;

/**
 * @Description 服务跟踪中心
 * @Author cuiyaosheng
 * @Date 2017-12-23 11:03
 * @Modified by:
 **/
@EnableZipkinStreamServer
@SpringBootApplication
public class ZipkinServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZipkinServerApplication.class, args);
	}
}
