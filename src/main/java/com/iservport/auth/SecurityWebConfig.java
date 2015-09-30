/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iservport.auth;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Spring Security Configuration to OAuth Server.
 * 
 * @author Eldevan Nery Junior
 * 
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityWebConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/**").authenticated()
		.and().httpBasic().realmName("OAuth Server");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication()
		.withUser("John").roles("ADMIN").password("password")
		.and()
		.withUser("Mary").roles("BASIC").password("password");
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

//	/**
//	 * Intercepta erros de autenticação.
//	 */
//	@Bean
//	public AuthenticationFailureHandler authenticationFailureHandler() {
//		return new AuthenticationFailureHandler() {
//
//			@Override
//			public void onAuthenticationFailure(HttpServletRequest request,
//					HttpServletResponse response,
//					AuthenticationException exception) throws IOException,
//					ServletException {
//				String exceptionToken = exception.getMessage().trim()
//						.toLowerCase().replace(" ", "");
//				int type = 0;
//				if (exceptionToken.contains("badcredentials")) {
//					type = 1;
//				} else if (exceptionToken.contains("unabletoextractvaliduser")) {
//					type = 2;
//				}
//				response.sendRedirect(request.getContextPath()
//						+ "/login/error?type=" + type);
//			}
//		};
//	}

}
