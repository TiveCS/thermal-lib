package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuObject;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuComponentStateUpdateEvent extends Event implements Cancellable {

    private static HandlerList HANDLER_LIST = new HandlerList();
    private boolean isCancelled = false;

    private MenuObject menuObject;
    private int targeSlot;
    private String previousState, nextState;

    public MenuComponentStateUpdateEvent(MenuObject menuObject, int targetSlot, String previousState, String nextState) {
        this.menuObject = menuObject;
        this.targeSlot = targetSlot;
        this.previousState = previousState;
        this.nextState = nextState;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public int getTargeSlot() {
        return targeSlot;
    }

    public String getNextState() {
        return nextState;
    }

    public String getPreviousState() {
        return previousState;
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
