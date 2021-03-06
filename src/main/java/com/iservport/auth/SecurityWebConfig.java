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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
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

	private int REMEMBER_ME_DEFAULT_DURATION = 14*24*60*60; // duas semanas

	@Inject
	private Environment env;

	@Inject
	private DataSource dataSource;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private UserDetailsService userDetailsService;

	@Inject
	private AuthenticationFailureHandler authenticationFailureHandler;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web
		//Spring Security ignores request to static resources such as CSS or JS files.
		.ignoring()
		.antMatchers("/webjars/**","/css/**","/fonts/**","/images/**","/js/**")
		.antMatchers("/ng/**")
		.antMatchers("/redactor/**")
		.antMatchers("/locales/**")
		.antMatchers("/favicon.png")
		.antMatchers("/app/status");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.formLogin()
		.loginPage("/login")
		.failureUrl("/login?error=bad_credentials")
		.failureHandler(authenticationFailureHandler)
		.permitAll()
		.and()
		//habilita csrf e remember-me
		.csrf().disable()
		//	        .csrfTokenRepository(csrfTokenRepository())
		//		    .and()
		.rememberMe().tokenRepository(persistentTokenRepository())
		.tokenValiditySeconds(REMEMBER_ME_DEFAULT_DURATION)
		//Configures the logout function
		.and()
		.logout()
		.deleteCookies("JSESSIONID")
		.deleteCookies("remember-me")
		.deleteCookies("X-XSRF-TOKEN")
		.logoutUrl("/logout") 
		.logoutSuccessUrl("/login")
		.permitAll()
		//Configures url based authorization
		.and()
		.authorizeRequests()
		.antMatchers("/**").permitAll();
	}

	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler 
	savedRequestAwareAuthenticationSuccessHandler() {

		SavedRequestAwareAuthenticationSuccessHandler auth 
		= new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}	;

	/**
	 * Intercepta erros de autenticação.
	 */
	@Bean
	public AuthenticationFailureHandler authenticationFailureHandler() {
		return new AuthenticationFailureHandler() {

			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) 
					throws IOException, ServletException {
				String exceptionToken = exception.getMessage().trim().toLowerCase().replace(" ", "");
				int type = 0;
				if (exceptionToken.contains("badcredentials")) {
					type = 1;
				}
				else if (exceptionToken.contains("unabletoextractvaliduser")) {
					type = 2;
				}
				response.sendRedirect(request.getContextPath() + "/login/error?type="+type);
			}
		};
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder);
	}


	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	/**
	 * Criptografia.
	 */
	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.queryableText(env.getProperty("security.encryptPassword", "password"), env.getProperty("security.encryptSalt", "00"));
	}


}
