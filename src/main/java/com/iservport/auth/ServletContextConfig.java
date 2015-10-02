package com.iservport.auth;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;

import org.helianto.security.resolver.CurrentUserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import freemarker.template.TemplateException;

/**
 * Configuração Java.
 * 
 * @author Eldevan Nery Junior
 * @author mauriciofernandesdecastro
 * 
 */
@Configuration
@EnableTransactionManagement
@Import({ RootContextConfig.class})
public class ServletContextConfig  extends WebMvcConfigurerAdapter{


	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Inject
	private ObjectMapper mapper;

	@Bean 
	public CurrentUserHandlerMethodArgumentResolver currentUserHandlerMethodArgumentResolver() {
		return new CurrentUserHandlerMethodArgumentResolver();
	}
	
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
	 * Argument resolvers.
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.addAll(
				Arrays.asList(
						currentUserHandlerMethodArgumentResolver()
						)
				);
	}
	
	/**
	 * Static resources.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/META-INF/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/fonts/**").addResourceLocations("classpath:/META-INF/fonts/").setCachePeriod(31556926);
        registry.addResourceHandler("/images/**").addResourceLocations("classpath:/META-INF/images/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/META-INF/js/").setCachePeriod(31556926);
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/").setCachePeriod(31556926);
        registry.addResourceHandler("/views/**").addResourceLocations("classpath:/views/").setCachePeriod(31556926);
	}	       

	/**
	 * Freemarker configurer.
	 * 
	 * @throws TemplateException 
	 * @throws IOException 
	 */
	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setPreferFileSystemAccess(false);
		configurer.setTemplateLoaderPaths(
				new String[] {"/WEB-INF/classes/freemarker/"
						,"/WEB-INF/freemarker/"
						,"classpath:/freemarker/"} );
		Properties props = new Properties();
		props.put("default_encoding", "utf-8");
		props.put("number_format", "computer");
		props.put("whitespace_stripping", "true");
		configurer.setFreemarkerSettings(props);
		return configurer;
	}

	/**
	 * Freemarker view resolver.
	 */
	public ViewResolver freeMarkerViewResolver() {
		FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
		resolver.setExposeSpringMacroHelpers(true);
		resolver.setCache(true);
		resolver.setPrefix("");
		resolver.setSuffix(".ftl");
		resolver.setContentType("text/html;charset=iso-8859-1");
		return resolver;
	}

	@Bean
	public ViewResolver viewResolver() {
		ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
		resolver.setViewResolvers(Arrays.asList(freeMarkerViewResolver()));
		return resolver;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * Formatters.
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addFormatter(new StringFormatter());
		super.addFormatters(registry);

	}

	public class StringFormatter implements Formatter<String>{
		@Override
		public String print(String object, Locale locale) {
			return object;
		}

		@Override
		public String parse(String text, Locale locale) throws ParseException {
			return new String(text);
		}
	}

	/**
	 * Jackson json converter.
	 */
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(mapper);
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON,APPLICATION_JSON_UTF8));
		return converter;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//para converter String direto pra json
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(
				Charset.forName("UTF-8"));
		stringConverter.setSupportedMediaTypes(Arrays.asList( //
				MediaType.TEXT_PLAIN, //
				MediaType.TEXT_HTML, //
				MediaType.APPLICATION_JSON));
		//Converter byte[] para imagem
		ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		arrayHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.IMAGE_JPEG
				, MediaType.IMAGE_PNG
				, MediaType.IMAGE_GIF));

		converters.add(arrayHttpMessageConverter);
		converters.add(stringConverter);
		converters.add(mappingJackson2HttpMessageConverter());
		super.configureMessageConverters(converters);
	}

	/**
	 * Commons multipart resolver.
	 */
	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver();
		resolver.setMaxUploadSize(10000000);
		return resolver;
	}
}
