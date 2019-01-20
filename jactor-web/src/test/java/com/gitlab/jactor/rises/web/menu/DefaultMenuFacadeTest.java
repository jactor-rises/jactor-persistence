package com.gitlab.jactor.rises.web.menu;

import com.gitlab.jactor.rises.commons.datatype.Name;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.gitlab.jactor.rises.web.menu.Menu.aMenu;
import static com.gitlab.jactor.rises.web.menu.MenuItem.aMenuItem;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

@DisplayName("A DefaultMenuFacade")
class DefaultMenuFacadeTest {

    @DisplayName("should fail if the menus provided are null")
    @Test void willThrowExceptionIfProvidedMenusAreNull() {
        assertThatNullPointerException().isThrownBy(() -> new DefaultMenuFacade((Menu[]) null))
                .withMessage("Menus must be provided");
    }

    @DisplayName("should fail if there are no provided menus")
    @Test void willThrowExceptionIfProvidedMenusAreEmpty() {
        Menu[] menus = new Menu[]{};
        assertThatIllegalArgumentException().isThrownBy(() -> new DefaultMenuFacade(menus))
                .withMessage("Menus must be provided");
    }

    @DisplayName("should fail if the menu asked for is unknown")
    @Test void willFailWhenMenuIsUnknown() {
        DefaultMenuFacade defaultMenuFacadeToTest = new DefaultMenuFacade(aMenu().withName("known.menu").add(aMenuItem()).build());

        assertThatIllegalArgumentException().isThrownBy(() -> defaultMenuFacadeToTest.fetchMenuItems(new Name("unknown.menu")))
                .withMessageContaining("unknown menu")
                .withMessageContaining("known.menu");
    }

    @DisplayName("should find menu items for known Menu")
    @Test void willFindKnownMenuItems() {
        MenuItem menuItem = aMenuItem().build();
        DefaultMenuFacade defaultMenuFacadeToTest = new DefaultMenuFacade(aMenu().withName("known.menu").add(menuItem).build());

        List<MenuItem> menuItems = defaultMenuFacadeToTest.fetchMenuItems(new Name("known.menu"));

        Assertions.assertThat(menuItems).contains(menuItem);
    }
}
