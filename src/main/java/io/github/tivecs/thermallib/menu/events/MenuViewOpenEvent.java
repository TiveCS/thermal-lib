package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MenuViewOpenEvent extends Event {

    private static HandlerList HANDLER_LIST = new HandlerList();

    private MenuObject menuObject;
    private Player viewer;
    private int previousPage, targetPage;
    private boolean isAlreadyCreated;

    public MenuViewOpenEvent(MenuObject menuObject, Player viewer, int previousPage, int targetPage, boolean isAlreadyCreated) {
        this.menuObject = menuObject;
        this.viewer = viewer;
        this.previousPage = previousPage;
        this.targetPage = targetPage;
        this.isAlreadyCreated = isAlreadyCreated;
    }

    public boolean isAlreadyCreated() {
        return isAlreadyCreated;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getTargetPage() {
        return targetPage;
    }

    public Player getViewer() {
        return viewer;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
