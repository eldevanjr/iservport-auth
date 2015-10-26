package com.iservport.auth;

import java.util.Locale;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Configuracao Java para Spring boot.
 * 
 * @author Eldevan Nery Junior
 *
 */
@Configuration
@SpringBootApplication 
@EnableAutoConfiguration
@EnableTransactionManagement
@Import({ RootContextConfig.class, SecurityWebConfig.class, OAuthConfiguration.class
//		, CassandraConfig.class
	})
@EntityScan(basePackages={"org.helianto.*.domain", "com.iservport.*.domain"})
public class ServletContextConfig extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(ServletContextConfig.class, args);
	}
	
	/**
	 * Override to set packages to scan.
	 */
	protected String[] getPacakgesToScan() {
		return new String[] {"org.helianto.*.domain", "com.iservport.*.domain"};
	}
	
	@Inject
	private Environment env;
	
	/**
	 * Força locale para pt_BR.
	 */
	@Bean(name = "localeResolver")
	public LocaleResolver sessionLocaleResolver(){
		SessionLocaleResolver localeResolver=new SessionLocaleResolver();
		localeResolver.setDefaultLocale(new Locale("pt_BR"));
		return localeResolver;
	}  
	
	/**
	 * Entity manager factory.
	 */
	@SuppressWarnings("deprecation")
	@Bean 
	public  LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
		vendor.setGenerateDdl(env.getProperty("helianto.sql.generateDdl", Boolean.class, Boolean.TRUE));
		vendor.setDatabasePlatform(env.getProperty("helianto.jdbc.dialect", "org.hibernate.dialect.HSQLDialect"));
		LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPackagesToScan(getPacakgesToScan());
		bean.setJpaVendorAdapter(vendor);
		bean.setPersistenceProvider(new HibernatePersistence());
		bean.afterPropertiesSet();
        return bean;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
	  JpaTransactionManager txManager = new JpaTransactionManager();
	  txManager.setEntityManagerFactory(entityManagerFactory().getObject());
	  return txManager;
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


}
