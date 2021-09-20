package io.github.tivecs.thermallib.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class MenuManager implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<String, Menu> registeredMenu;

    private final HashMap<UUID, MenuObject> playerMenu;

    public MenuManager(@Nonnull JavaPlugin plugin){
        this.plugin = plugin;
        this.registeredMenu = new HashMap<>();
        this.playerMenu = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onMenuClick(InventoryClickEvent event){
        MenuObject menuObject = getPlayerMenu().get(event.getWhoClicked().getUniqueId());
        if (menuObject != null){
            menuObject.getMenu().onClick(event, menuObject);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event){
        MenuObject menuObject = getPlayerMenu().get(event.getPlayer().getUniqueId());
        if (menuObject != null){
            close((Player) event.getPlayer());
        }
    }

    public void registerMenu(@Nonnull Menu... menus){
        for (Menu menu : menus){
            getRegisteredMenu().put(menu.getMenuId(), menu);
        }
    }

    public void close(@Nonnull Player target){
        getPlayerMenu().remove(target.getUniqueId());
    }

    public void open(@Nonnull Player target, @Nonnull MenuObject menuObject, int page){
        menuObject.render(page);
        target.openInventory(menuObject.getInventory());
        getPlayerMenu().put(target.getUniqueId(), menuObject);
    }

    public void open(@Nonnull Player target, @Nonnull MenuObject menuObject){
        open(target, menuObject, 1);
    }

    public void open(@Nonnull Player target, @Nonnull Menu menu, int page){
        MenuObject object = new MenuObject(this, menu);
        open(target, object, page);
    }

    public void open(@Nonnull Player target, @Nonnull String menuId, int page){
        open(target, getRegisteredMenu().get(menuId), page);
    }

    public void open(@Nonnull Player target, @Nonnull Menu menu){
        open(target, menu, 1);
    }

    public void open(@Nonnull Player target, @Nonnull String menuId){
        open(target, menuId, 1);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public HashMap<String, Menu> getRegisteredMenu() {
        return registeredMenu;
    }

    public HashMap<UUID, MenuObject> getPlayerMenu() {
        return playerMenu;
    }
}
