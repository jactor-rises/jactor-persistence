package nu.hjemme.business.service;

import nu.hjemme.business.domain.menu.MenuImpl;
import nu.hjemme.client.datatype.MenuTarget;
import nu.hjemme.client.datatype.Name;
import nu.hjemme.client.domain.menu.ChosenMenuItem;
import nu.hjemme.client.dto.MenuDto;
import nu.hjemme.client.service.MenuFacade;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The {@link MenuFacade} */
public class MenuFacadeImpl implements MenuFacade {
    private Map<Name, MenuImpl> menusByName = new HashMap<>();

    public MenuFacadeImpl(List<MenuDto> menus) {
        Validate.notEmpty(menus, "Menus must be provided");

        for (MenuDto menuDto : menus) {
            Name menuName = new Name(menuDto.getName());
            menusByName.put(menuName, new MenuImpl(menuDto));
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<ChosenMenuItem> retrieveChosenMenuItemBy(MenuTarget menuTarget) {
        Name name = menuTarget.getMenuName();
        Validate.notNull(name, "The name of a menu must be provided!");
        Validate.isTrue(menusByName.containsKey(name), name + " is an unknown menu. Known:" + menusByName.keySet());

        return menusByName.get(name).retrieveChosenMenuItemsBy(menuTarget.getMenuItemTarget());
    }
}
