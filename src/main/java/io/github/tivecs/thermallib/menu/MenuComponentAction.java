package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.menu.events.MenuComponentStateUpdateEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface MenuComponentAction {
    default void clickAction(MenuObject menuObject, InventoryClickEvent event){
        event.setCancelled(true);
    }
    default void stateUpdateAction(MenuObject menuObject, MenuComponentStateUpdateEvent event){}
}
