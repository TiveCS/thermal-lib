package io.github.tivecs.thermallib.menu.events;

import io.github.tivecs.thermallib.menu.MenuObject;
import io.github.tivecs.thermallib.menu.MenuTemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuObjectCreateEvent extends Event {

    private static HandlerList HANDLER_LIST = new HandlerList();

    private MenuTemplate template;
    private MenuObject createdObject;

    public MenuObjectCreateEvent(MenuTemplate template, MenuObject createdObject){
        this.template = template;
        this.createdObject = createdObject;
    }

    public MenuTemplate getTemplate() {
        return template;
    }

    public MenuObject getCreatedObject() {
        return createdObject;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
