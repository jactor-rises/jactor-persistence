package com.gitlab.jactor.rises.web.controller;

import com.gitlab.jactor.rises.commons.datatype.Name;
import com.gitlab.jactor.rises.commons.datatype.Username;
import com.gitlab.jactor.rises.web.dto.UserModel;
import com.gitlab.jactor.rises.web.menu.MenuFacade;
import com.gitlab.jactor.rises.web.service.UserRestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.gitlab.jactor.rises.web.menu.MenuItem.aMenuItem;
import static java.util.Collections.singletonList;

@Controller
public class UserController {

    private final UserRestService userRestService;
    private final MenuFacade menuFacade;
    private final String menuNameUsers;

    @Autowired public UserController(UserRestService userRestService, MenuFacade menuFacade, @Value("${jactor-web.menu.users}") String menuNameUsers) {
        this.userRestService = userRestService;
        this.menuFacade = menuFacade;
        this.menuNameUsers = menuNameUsers;
    }

    @GetMapping(value = "/user") public ModelAndView get(@RequestParam(name = "choose", required = false) String username) {
        ModelAndView modelAndView = new ModelAndView("user");

        if (StringUtils.isNoneBlank(username)) {
            populateUser(username, modelAndView);
        }

        populateUserMenu(modelAndView);
        populateDefaultUsers(modelAndView);

        return modelAndView;
    }

    private void populateUser(String username, ModelAndView modelAndView) {
        var user = userRestService.find(new Username(username));
        Map<String, Object> modelMap = modelAndView.getModel();

        if (user.isPresent()) {
            modelMap.put("user", new UserModel(user.get()));
        } else {
            modelMap.put("unknownUser", username);
        }
    }

    private void populateUserMenu(ModelAndView modelAndView) {
        var usernames = userRestService.findAllUsernames();
        modelAndView.addObject("usersMenu", singletonList(
                aMenuItem()
                        .withName("menu.users.choose")
                        .addAsChildren(usernames)
                        .build()
                )
        );
    }

    private void populateDefaultUsers(ModelAndView modelAndView) {
        var menuItems = menuFacade.fetchMenuItems(Name.of(menuNameUsers));
        modelAndView.addObject("defaultUsers", menuItems);
    }
}
