package com.kotelking.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApiInitializer implements WebApplicationInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(ApiInitializer.class);

    public void onStartup(ServletContext container) throws ServletException {

        // Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);

        // Manage the lifecycle of the root application context
        container.addListener(new ContextLoaderListener(rootContext));

        // Create the dispatcher servlet's Spring application context
        {
            final AnnotationConfigWebApplicationContext webContext =
                    new AnnotationConfigWebApplicationContext();

            webContext.setParent(rootContext);
            webContext.register(ApiConfig.class);

            ServletRegistration.Dynamic dispatcher =
                    container.addServlet("dispatcher", new DispatcherServlet(webContext));

            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");
            dispatcher.setMultipartConfig(new MultipartConfigElement(null, 52428800, 52428800, 0));
            dispatcher.setInitParameter("dispatchOptionsRequest", "true");
        }


    }
}