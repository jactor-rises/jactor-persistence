package com.gitlab.jactor.rises.web;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.web.menu.DefaultMenuFacade;
import com.gitlab.jactor.rises.web.menu.Menu;
import com.gitlab.jactor.rises.web.menu.MenuFacade;
import com.gitlab.jactor.rises.web.service.UserRestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.util.UriTemplateHandler;

import static com.gitlab.jactor.rises.web.menu.Menu.aMenu;
import static com.gitlab.jactor.rises.web.menu.MenuItem.aMenuItem;

@Configuration
@PropertySource("classpath:application.properties")
public class JactorWebBeans {

    @Value("${jactor-facade.url.root}") private String rootUrlFacade;
    @Value("${jactor-facade.url.user-endpint}") private String endpointUser;
    @Value("${jactor-web.menu.users}") private String menuNammeUsers;

    @Bean public MenuFacade menuFacade() {
        return new DefaultMenuFacade(usersMenu());
    }

    private Menu usersMenu() {
        return aMenu()
                .withName(new Name(menuNammeUsers))
                .add(aMenuItem().withName("menu.users.default")
                        .add(aMenuItem().withName("jactor").withTarget("user?choose=jactor").withDescription("menu.users.jactor.desc"))
                        .add(aMenuItem().withName("tip").withTarget("user?choose=tip").withDescription("menu.users.tip.desc"))
                ).build();
    }

    @Bean public UserRestService userRestService() {
        return new UserRestService(initUriTemplateHandler(), endpointUser);
    }

    private UriTemplateHandler initUriTemplateHandler() {
        return new RootUriTemplateHandler(rootUrlFacade);
    }
}
