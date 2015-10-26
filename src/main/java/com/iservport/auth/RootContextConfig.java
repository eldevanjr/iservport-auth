
package com.iservport.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.helianto.core.config.HeliantoServiceConfig;
import org.helianto.security.resolver.CurrentUserHandlerMethodArgumentResolver;
import org.helianto.sendgrid.config.SendGridConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import freemarker.template.TemplateException;

/**
 * Basic Java configuration to Spring-boot.
 * 
 * @author Eldevan Nery Junior
 *
 */
@Configuration
@EnableWebMvc
@Import({HeliantoServiceConfig.class, SendGridConfig.class})
@ComponentScan(
		basePackages = {
				"org.helianto.*.sender"
				, "com.iservport.*.repository"
				, "com.iservport.*.service"
				, "com.iservport.*.controller"
				, "org.helianto.*.controller"
				, "org.helianto.*.repository"
				, "com.iservport.*.sender"
		})
@EnableJpaRepositories(
		basePackages={"org.helianto.*.repository", "com.iservport.*.repository"})
@PropertySource(value = { "classpath:META-INF/app.properties" })
public abstract class RootContextConfig  extends WebMvcConfigurerAdapter{

	@Inject
	protected Environment env;

	@Bean 
	public CurrentUserHandlerMethodArgumentResolver currentUserHandlerMethodArgumentResolver() {
		return new CurrentUserHandlerMethodArgumentResolver();
	}

	/**
	 * Registro de resolução de argumentos.
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.addAll(
			Arrays.asList(
					currentUserHandlerMethodArgumentResolver()
			)
		);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Para direcionamento de recursos estáticos.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/webjars/**")) {
			registry.addResourceHandler("/webjars/**").addResourceLocations(
					"classpath:/META-INF/resources/webjars/");
		}
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/META-INF/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/META-INF/fonts/").setCachePeriod(31556926);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/META-INF/images/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/META-INF/js/").setCachePeriod(31556926);
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/").setCachePeriod(31556926);
        registry.addResourceHandler("/views/**").addResourceLocations("classpath:/views/").setCachePeriod(31556926);
	}	                        
	
	@Bean
	public ViewResolver viewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setCache(true);
		resolver.setPrefix("");
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html;charset=UTF-8");
		return resolver;
	}

	@Bean
	public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setPreferFileSystemAccess(false);
		factory.setTemplateLoaderPaths(
				new String[] {"/WEB-INF/classes/freemarker/"
						,"/WEB-INF/freemarker/"
						,"classpath:/freemarker/"} );
		Properties props = new Properties();
		props.put("default_encoding", "utf-8");
		props.put("number_format", "computer");
		props.put("whitespace_stripping", "true");
		factory.setFreemarkerSettings(props);
		factory.setDefaultEncoding("UTF-8");
		FreeMarkerConfigurer result = new FreeMarkerConfigurer();
		result.setConfiguration(factory.createConfiguration());
		return result;
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
