package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuComponent;
import io.github.tivecs.thermallib.menu.MenuObject;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuComponentClickEvent extends Event implements Cancellable {

    private static HandlerList HANDLER_LIST = new HandlerList();

    private boolean isCancelled = false;
    private MenuObject menuObject;
    private InventoryClickEvent clickEvent;
    private MenuComponent clickedComponent;

    public MenuComponentClickEvent(MenuObject menuObject, InventoryClickEvent clickEvent, MenuComponent clickedComponent){
        this.menuObject = menuObject;
        this.clickEvent = clickEvent;
        this.clickedComponent = clickedComponent;
    }

    public MenuComponent getClickedComponent() {
        return clickedComponent;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
