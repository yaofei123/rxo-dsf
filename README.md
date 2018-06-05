服务规范
=====
# 1 微服务基础服务服务规范

## 1.1 服务器
	x.x.x.xxx pro-microserver-xxx.rxo.com
	x.x.x.xxx pro-microserver-xxx.rxo.com
	x.x.x.xxx pro-microserver-xxx.rxo.com
## 1.2 Spring cloud 
    端口:400xx ~ 402xx
### 1.2.1 Eureka
    端口40000
---
	pro-microserver-xxx.rxo.com:40000
	pro-microserver-xxx.rxo.com:40000
	pro-microserver-xxx.rxo.com:40000
	
### 1.2.2 zipkin 链路追踪
    端口:40010
    	
### 1.2.3 zuul 服务网关
    端口:40020
    	
### 1.2.4 hystrix 断路熔断
    端口:40030

## 1.3 Apollo 
    端口:403xx
### 1.3.1 apollo-portal
    端口:40300
    超级管理员账号/密码：apollo/admin
---
	pro-microserver-xxx.rxo.com:40300
### 1.3.2 apollo-config
    端口:40310
    
### 1.3.3 apollo-admin
    端口:40320
	
## 1.4 RabbitMQ
    端口:404xx
---    
    管理员 账号/密码：admin/admin
---

---	
	集群 SLB 地址 
	
## 1.5 xxl-job-admin 
    端口:405xx
---
    端口:40500
    管理员 账号/密码：admin / admin@xxljob!
---    
    pro-microserver-xxx.rxo.com:40500
    pro-microserver-xxx.rxo.com:40500
    pro-microserver-xxx.rxo.com:40500
