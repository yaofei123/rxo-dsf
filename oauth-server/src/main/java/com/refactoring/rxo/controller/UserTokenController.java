package com.refactoring.rxo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.refactoring.rxo.utils.SpringContextUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** 
* @author 作者 owen E-mail: 624191343@qq.com
* @version 创建时间：2018年4月28日 下午2:18:54 
* 类说明 
*/
@Api(tags = "Token处理")
@RestController
public class UserTokenController {

	private Logger LOGGER = LoggerFactory.getLogger(UserTokenController.class);

	@Autowired
	private ClientDetailsService jdbcClientDetailsService;

	@Resource
	private ObjectMapper objectMapper; // springmvc启动时自动装配json处理类
	
	@ApiOperation(value = "用户名密码获取token")
	@PostMapping("/user/token")
	public void onAuthenticationSuccess(
			@ApiParam(required = true, name = "username", value = "账号") @RequestParam( value = "username") String username ,
			@ApiParam(required = true, name = "password", value = "密码") @RequestParam( value = "password") String password ,
			HttpServletRequest request, HttpServletResponse response){
		String clientId = request.getHeader("client_id");
		String clientSecret = request.getHeader("client_secret");

		try {

			if (clientId == null) {
				throw new UnapprovedClientAuthenticationException("请求头中无client_id信息");
			}

			if (clientSecret == null) {
				throw new UnapprovedClientAuthenticationException("请求头中无client_secret信息");
			}

			ClientDetails clientDetails = jdbcClientDetailsService.loadClientByClientId(clientId);

			if (clientDetails == null) {
				throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
			} else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
				throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
			}

			TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId,
					clientDetails.getScope(), "customer");

			OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

			
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			
			AuthenticationManager authenticationManager = SpringContextUtils.getBean(AuthenticationManager.class);
			
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
			
			OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request,
					authentication);

			AuthorizationServerTokenServices authorizationServerTokenServices = SpringContextUtils.getBean("defaultAuthorizationServerTokenServices", AuthorizationServerTokenServices.class);
			
			OAuth2AccessToken oAuth2AccessToken = authorizationServerTokenServices
					.createAccessToken(oAuth2Authentication);

			oAuth2Authentication.setAuthenticated(true);

			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(oAuth2AccessToken));
			response.getWriter().flush();
			response.getWriter().close();

		} catch (Exception e) {
			LOGGER.error("",e);

			response.setStatus(HttpStatus.UNAUTHORIZED.value());

			response.setContentType("application/json;charset=UTF-8");

			Map<String, String> rsp = new HashMap<>();
			rsp.put("resp_code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("rsp_msg", e.getMessage());

			try {
				response.getWriter().write(objectMapper.writeValueAsString(rsp));
				response.getWriter().flush();
				response.getWriter().close();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		

		}
	}
	
	@ApiOperation(value = "clientId获取token")
	@PostMapping("/client/token")
	public void client(
			@ApiParam(required = true, name = "clientId", value = "应用ID") @RequestParam( value = "clientId") String clientId ,
			@ApiParam(required = true, name = "clientSecret", value = "应用密钥") @RequestParam( value = "clientSecret") String clientSecret ,
			HttpServletRequest request, HttpServletResponse response){
 

		try {

			if (clientId == null) {
				throw new UnapprovedClientAuthenticationException("请求参数中无clientId信息");
			}

			if (clientSecret == null) {
				throw new UnapprovedClientAuthenticationException("请求参数中无clientSecret信息");
			}

			ClientDetailsService clientDetailsService = SpringContextUtils.getBean(ClientDetailsService.class);
			
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

			if (clientDetails == null) {
				throw new UnapprovedClientAuthenticationException("clientId对应的信息不存在");
			} else if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
				throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
			}
			
			Map<String,String> map = new HashMap<>() ;
			map.put("client_secret", clientSecret);
			map.put("client_id" ,clientId);
			map.put("grant_type" , "client_credentials") ;
			TokenRequest tokenRequest = new TokenRequest(map, clientId,
					clientDetails.getScope(), "client_credentials");

			OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

			
			AuthorizationServerTokenServices authorizationServerTokenServices = SpringContextUtils.getBean("defaultAuthorizationServerTokenServices", AuthorizationServerTokenServices.class);
			OAuth2RequestFactory requestFactory =  new DefaultOAuth2RequestFactory(clientDetailsService) ;
			ClientCredentialsTokenGranter clientCredentialsTokenGranter = new ClientCredentialsTokenGranter(
					authorizationServerTokenServices,
					clientDetailsService,  requestFactory 
					);
			 
			clientCredentialsTokenGranter.setAllowRefresh(true);
			OAuth2AccessToken oAuth2AccessToken = clientCredentialsTokenGranter.grant("client_credentials", tokenRequest);

			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(oAuth2AccessToken));
			response.getWriter().flush();
			response.getWriter().close();

		} catch (Exception e) {

			response.setStatus(HttpStatus.UNAUTHORIZED.value());

			response.setContentType("application/json;charset=UTF-8");

			Map<String, String> rsp = new HashMap<>();
			rsp.put("resp_code", HttpStatus.UNAUTHORIZED.value() + "");
			rsp.put("rsp_msg", e.getMessage());

			try {
				response.getWriter().write(objectMapper.writeValueAsString(rsp));
				response.getWriter().flush();
				response.getWriter().close();
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		

		}
	}
	
}
