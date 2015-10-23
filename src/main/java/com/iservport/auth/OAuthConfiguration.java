package com.iservport.auth;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Authorization server configuration.
 * 
 * @author mauriciofernandesdecastro
 * @author Eldevan Nery Junior
 */
@Configuration
@EnableAuthorizationServer
public class OAuthConfiguration  extends AuthorizationServerConfigurerAdapter{

	private static final String HELIANTO_RESOURCE_ID = "helianto";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private UserApprovalHandler userApprovalHandler;
	
	@Inject
	private ApprovalStore approvalStore;	
	
	@Autowired
	private ClientDetailsService clientDetailsService;
	

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Value("${helianto.trusted.client.secret:helianto}")
	private String heliantoTrustedClientSecret;

	/**
	 * Client configuration.
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("helianto-client")
		.resourceIds(HELIANTO_RESOURCE_ID)
		.authorizedGrantTypes("authorization_code", "implicit")
		.authorities("ROLE_USER")
		.scopes("read", "write")
		.secret("secret")
		.and()
		.withClient("helianto-trusted-client")
		.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
		.authorities("ROLE_USER")
		.scopes("read", "write", "trust")
		.secret("secret")
		.accessTokenValiditySeconds(60)
		.and()
		.withClient("helianto-trusted-client-with-secret")
		.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
		.authorities("ROLE_USER")
		.scopes("read", "write", "trust")
		.secret(heliantoTrustedClientSecret);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore)
		.userApprovalHandler(userApprovalHandler)
		.authenticationManager(authenticationManager);
	}
	
	@Bean
	public DefaultTokenServices tokenServices() throws Exception {
	    DefaultTokenServices tokenServices = new DefaultTokenServices();
	    tokenServices.setAccessTokenValiditySeconds(6000);
	    tokenServices.setClientDetailsService(clientDetailsService);
	    tokenServices.setSupportRefreshToken(true);
	    tokenServices.setTokenStore(tokenStore());
	    return tokenServices;
	}
	
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }


	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.realm("helianto/client");
	}

	@Bean
	@Lazy
	@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
	public ApprovalStoreUserApprovalHandler userApprovalHandler() throws Exception {
		ApprovalStoreUserApprovalHandler handler = new ApprovalStoreUserApprovalHandler();
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		handler.setApprovalStore(approvalStore);
		return handler;
	}
}
