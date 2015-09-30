
package com.iservport.auth;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Basic Java configuration.
 * 
 * @author mauriciofernandesdecastro
 */
@Configuration
@EnableWebMvc
@Import({SecurityWebConfig.class, OAuthConfiguration.class})
//@ComponentScan(
//	basePackages = {
//		"org.helianto.*.controller"
//})
@EnableJpaRepositories(
    basePackages={"org.helianto.*.repository"})
public abstract class RootContextConfig extends WebMvcConfigurerAdapter {
	
	@Inject
	protected Environment env;
	
	/**
	 * Override to set packages to scan.
	 */
	protected String[] getPacakgesToScan() {
		return new String[] {"org.helianto.*.domain", "com.iservport.*.domain"};
	}
	
	/**
	 * Entity manager factory.
	 */
	@Bean 
	public EntityManagerFactory entityManagerFactory() {
		HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
		vendor.setGenerateDdl(env.getProperty("helianto.sql.generateDdl", Boolean.class, Boolean.TRUE));

		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPackagesToScan(getPacakgesToScan());
		bean.setJpaVendorAdapter(vendor);
		bean.setPersistenceProvider(new HibernatePersistence());
		bean.afterPropertiesSet();
        return bean.getObject();
	}
	
	/**
	 * Simple data source.
	 * 
	 * @throws NamingException 
	 * @throws IllegalArgumentException 
	 */
	@Bean
	public DataSource dataSource() throws IllegalArgumentException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(env.getRequiredProperty("helianto.db.driver"));
        dataSource.setUrl(env.getRequiredProperty("helianto.db.url"));
        dataSource.setUsername(env.getRequiredProperty("helianto.db.username"));
        dataSource.setPassword(env.getRequiredProperty("helianto.db.password"));
         
        return dataSource;
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Jackson mapper.
	 * 
	 * Configured to allow comments on JSON files and to disable fail if class 
	 * doens't contains a property.
	 */
	@Bean
	public ObjectMapper mapper(){
		JsonFactory factory = new JsonFactory().configure(Feature.ALLOW_COMMENTS, true);
		ObjectMapper mapper =  new ObjectMapper(factory);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	
	/**
	 * Password encoder.
	 */
	@Bean
	public Md5PasswordEncoder notificationEncoder() {
		return new Md5PasswordEncoder();
	}
	
	/**
	 * Add Locale change interceptor.
	 */
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeInterceptor = new LocaleChangeInterceptor();
		localeInterceptor.setParamName("siteLocale");
		registry.addInterceptor(localeInterceptor);
	}
	
	/**
	 * Cookie locale resolver.
	 */
	@Bean
	public LocaleResolver localeResolver() {
		return new CookieLocaleResolver();
	}
	
}
