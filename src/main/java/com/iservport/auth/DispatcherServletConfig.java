package com.iservport.auth;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.helianto.seed.AbstractDispatcherServletConfig;
import org.helianto.seed.AbstractServletContextConfig;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class DispatcherServletConfig 
	extends AbstractDispatcherServletConfig{

	@Override
	protected Class<? extends AbstractServletContextConfig> getServletContextConfigClass() {
		return ServletContextConfig.class;
	}

	@Override 
	public void onStartup(ServletContext servletContext) throws ServletException {
		
		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("characterEncoding", characterEncodingFilter);
		characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
		
		//REST
		AnnotationConfigWebApplicationContext restContext = new AnnotationConfigWebApplicationContext();
		
		restContext.register(getServletContextConfigClass());
		
		ServletRegistration.Dynamic rest = servletContext.addServlet("/", new DispatcherServlet(restContext));
		rest.setLoadOnStartup(2);
		rest.addMapping("/");

		servletContext.addListener(new ContextLoaderListener(restContext));
		servletContext.addListener(new SessionListener());
	}
	
	

}
