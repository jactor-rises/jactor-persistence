package com.gitlab.jactor.rises.web;

import com.gitlab.jactor.rises.web.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafView;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
@PropertySource("classpath:application.properties")
public class JactorWebMvcConfiguration implements WebMvcConfigurer {

    private final RequestInterceptor requestInterceptor;
    private final String prefix;
    private final String suffix;

    public JactorWebMvcConfiguration(
            @Autowired RequestInterceptor requestInterceptor,
            @Value("${spring.mvc.view.prefix}") String prefix,
            @Value("${spring.mvc.view.suffix}") String suffix
    ) {
        this.requestInterceptor = requestInterceptor;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public @Bean ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setCacheable(false);
        templateResolver.setCharacterEncoding("UTF-8");

        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(suffix);

        templateResolver.setTemplateMode("HTML");

        return templateResolver;
    }

    public @Bean SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    public @Bean ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
        thymeleafViewResolver.setViewClass(ThymeleafView.class);
        thymeleafViewResolver.setTemplateEngine(templateEngine());

        return thymeleafViewResolver;
    }

    public @Bean LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    public @Bean LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");

        return lci;
    }

    public @Override void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("static/**")
                .addResourceLocations("resources");
    }

    public @Override void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }

    public @Override void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(requestInterceptor);
    }
}
