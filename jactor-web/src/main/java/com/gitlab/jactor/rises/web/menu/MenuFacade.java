package com.gitlab.jactor.rises.web.menu;

import com.gitlab.jactor.rises.commons.datatype.Name;

import java.util.List;

public interface MenuFacade {

    /**
     * @param menuName of the menu items to fetch.
     * @return a list of {@link MenuItem} by {@link Name}.
     */
    List<MenuItem> fetchMenuItems(Name menuName);
}
