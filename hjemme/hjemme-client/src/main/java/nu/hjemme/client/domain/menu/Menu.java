package nu.hjemme.client.domain.menu;

import nu.hjemme.client.datatype.MenuItemTarget;
import nu.hjemme.client.datatype.Name;

import java.util.List;

/**
 * @author Tor Egil Jacobsen
 */
public interface Menu {

    /**
     * @return the name of the menu
     */
    Name getName();

    /**
     * @return the menu items of this menu
     */
    List<? extends MenuItem> getMenuItems();

    /**
     * @param menuItemTarget som er ønskelig
     * @return en liste av {@link nu.hjemme.client.domain.menu.ChosenMenuItem}s basert på ønsket {@link nu.hjemme.client.datatype.MenuItemTarget}
     */
    List<ChosenMenuItem> retrieveChosenMenuItemsBy(MenuItemTarget menuItemTarget);
}