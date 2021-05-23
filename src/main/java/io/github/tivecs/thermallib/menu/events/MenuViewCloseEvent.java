package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuObject;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuViewCloseEvent extends Event {

    private static HandlerList HANDLER_LIST = new HandlerList();

    private MenuObject menuObject;
    private InventoryCloseEvent closeEvent;

    public MenuViewCloseEvent(MenuObject menuObject, InventoryCloseEvent closeEvent) {
        this.menuObject = menuObject;
        this.closeEvent = closeEvent;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public InventoryCloseEvent getCloseEvent() {
        return closeEvent;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
