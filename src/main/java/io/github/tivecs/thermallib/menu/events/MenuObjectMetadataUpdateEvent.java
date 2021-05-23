package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuObject;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuObjectMetadataUpdateEvent extends Event implements Cancellable {


    public enum UpdateAction{
        CREATE, UPDATE, DELETE
    }

    private static HandlerList HANDLER_LIST = new HandlerList();

    private boolean isCancelled = false;
    private MenuObject menuObject;
    private String metadataKey;
    private Object previousMetadataValue, nextMetadataValue;
    private UpdateAction updateAction;

    public MenuObjectMetadataUpdateEvent(MenuObject menuObject, String metadataKey, Object previousMetadataValue, Object nextMetadataValue, UpdateAction updateAction) {
        this.menuObject = menuObject;
        this.metadataKey = metadataKey;
        this.previousMetadataValue = previousMetadataValue;
        this.nextMetadataValue = nextMetadataValue;
        this.updateAction = updateAction;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public Object getNextMetadataValue() {
        return nextMetadataValue;
    }

    public Object getPreviousMetadataValue() {
        return previousMetadataValue;
    }

    public String getMetadataKey() {
        return metadataKey;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
