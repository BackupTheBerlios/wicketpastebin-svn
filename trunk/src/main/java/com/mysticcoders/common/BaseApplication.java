package com.mysticcoders.common;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import wicket.protocol.http.WebApplication;

import javax.servlet.ServletContext;

/**
 * BaseApplication
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2004 Mystic Coders, LLC
 */
public abstract class BaseApplication extends WebApplication {

    /**
     * Determine operations mode: deployment or development
     */
    protected void init() {        
        ServletContext servletContext = getServletContext();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

        String names[] = applicationContext.getBeanDefinitionNames();

        for (String name : names) {
            System.out.println("name:" + name);
        }
    }

    private ApplicationContext applicationContext;


    public Object getBean(String name) {
        if (name == null) return null;

        return applicationContext.getBean(name);
    }
}
