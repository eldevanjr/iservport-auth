package com.iservport.auth;

import java.util.Locale;

import org.helianto.seed.AbstractServletContextConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/**
 * Configuracao Java.
 * 
 * @author mauriciofernandesdecastro
 */
@Configuration
@EnableTransactionManagement
@Import({ RootContextConfig.class})
public class ServletContextConfig extends AbstractServletContextConfig {

	/**
	 * Força locale para pt_BR.
	 */
	@Bean(name = "localeResolver")
	 public LocaleResolver sessionLocaleResolver(){
	     SessionLocaleResolver localeResolver=new SessionLocaleResolver();
	     localeResolver.setDefaultLocale(new Locale("pt_BR"));
	     return localeResolver;
	 }  

}
