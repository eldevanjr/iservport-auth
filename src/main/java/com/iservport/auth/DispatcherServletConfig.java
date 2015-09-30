package com.iservport.auth;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class DispatcherServletConfig implements WebApplicationInitializer {

	public static int SESSION_MAX_INACTIVE_INTERVAL = 30*60;

	@Override 
	public void onStartup(ServletContext servletContext) throws ServletException {

		AnnotationConfigWebApplicationContext restContext = new AnnotationConfigWebApplicationContext();

		restContext.register(ServletContextConfig.class);

		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("characterEncoding", characterEncodingFilter);
		characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

		ServletRegistration.Dynamic rest = servletContext.addServlet("/", new DispatcherServlet(restContext));
		rest.setLoadOnStartup(1);
		rest.addMapping("/");

		servletContext.addListener(new ContextLoaderListener(restContext));
		servletContext.addListener(new SessionListener());
	}

	/**
	 * Listener de tempo de expiração de sessão.
	 */
	public class SessionListener implements HttpSessionListener {

		@Override
		public void sessionCreated(HttpSessionEvent event) {
			event.getSession().setMaxInactiveInterval(SESSION_MAX_INACTIVE_INTERVAL);
		}

		@Override
		public void sessionDestroyed(HttpSessionEvent event) { }
	}


}
