
package com.iservport.auth;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.helianto.core.config.HeliantoServiceConfig;
import org.helianto.sendgrid.config.SendGridConfig;
import org.hibernate.ejb.HibernatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Basic Java configuration.
 * 
 * @author mauriciofernandesdecastro
 * @author Eldevan Nery Junior
 * 
 */
@Configuration
@EnableWebMvc
@Import({SecurityWebConfig.class, OAuthConfiguration.class, HeliantoServiceConfig.class, SendGridConfig.class})
@ComponentScan(
	basePackages = {
		"com.iservport.*.controller"
})
@EnableJpaRepositories(
    basePackages={"org.helianto.*.repository"})
public abstract class RootContextConfig extends WebMvcConfigurerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(RootContextConfig.class);
	
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
		vendor.setDatabasePlatform(env.getProperty("helianto.jdbc.dialect", "org.hibernate.dialect.HSQLDialect"));
		DataSource dataSource = dataSource();
		logger.info("Creating entity manager from {}", dataSource);
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource);
		bean.setPackagesToScan(getPacakgesToScan());
		bean.setJpaVendorAdapter(vendor);
		bean.setPersistenceProvider(new HibernatePersistence());
		bean.afterPropertiesSet();
        return bean.getObject();
	}
	
	/**
	 * Data source.
	 */
	@Bean
	public DataSource dataSource() {
		try {
			ComboPooledDataSource ds = new ComboPooledDataSource();
			ds.setDriverClass(env.getProperty("helianto.jdbc.driverClassName", "org.hsqldb.jdbcDriver"));
			ds.setJdbcUrl(env.getProperty("helianto.jdbc.url", "jdbc:hsqldb:file:target/testdb/db2;shutdown=true"));
			ds.setUser(env.getProperty("helianto.jdbc.username", "sa"));
			ds.setPassword(env.getProperty("helianto.jdbc.password", ""));
			ds.setAcquireIncrement(5);
			ds.setIdleConnectionTestPeriod(60);
			ds.setMaxPoolSize(100);
			ds.setMaxStatements(50);
			ds.setMinPoolSize(10);
			return ds;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
