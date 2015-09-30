package com.iservport.auth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Resource server configuration.
 * 
 * @author mauriciofernandesdecastro
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	private static final String HELIANTO_RESOURCE_ID = "helianto";
	
	@Autowired
	private DataSource dataSource;
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(HELIANTO_RESOURCE_ID)
		.tokenStore(tokenStore());
	}

//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		http
//			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//		.and()
//			.requestMatchers().antMatchers("/rest/**", "/oauth/users/**", "/oauth/clients/**")
//		.and().csrf().disable()	
//			.authorizeRequests()
//				.antMatchers("/rest").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")                                        
//				.antMatchers("/rest/**").access("#oauth2.hasScope('read') or (!#oauth2.isOAuth() and hasRole('ROLE_USER'))")
//				.regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
//					.access("#oauth2.clientHasRole('ROLE_USER') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
//				.regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
//					.access("#oauth2.clientHasRole('ROLE_USER') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
//				.regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
//					.access("#oauth2.clientHasRole('ROLE_USER') and #oauth2.isClient() and #oauth2.hasScope('read')");
//	}
	
	
	public TokenStore tokenStore() {
		return new JdbcTokenStore(dataSource);
	}

}