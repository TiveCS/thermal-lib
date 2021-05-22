package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.menu.events.MenuComponentClickEvent;
import io.github.tivecs.thermallib.storage.StorageYML;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class MenuManager implements Listener {

    private JavaPlugin plugin;
    private HashMap<String, MenuTemplate> templates;
    private HashMap<UUID, MenuObject> viewers;
    private File menuFolder;

    public MenuManager(@Nonnull JavaPlugin plugin){
        this.plugin = plugin;
        this.menuFolder = new File(plugin.getDataFolder(), "menu");
        this.templates = new HashMap<>();
        this.viewers = new HashMap<>();

        if (!getMenuFolder().exists()){
            if (!plugin.getDataFolder().exists()){
                plugin.getDataFolder().mkdir();
            }
            getMenuFolder().mkdir();
        }

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        UUID uuid = event.getWhoClicked().getUniqueId();
        if (getViewers().containsKey(uuid)){
            MenuObject menuObject = getViewers().get(uuid);
            if (event.getInventory().equals(menuObject.getMenuView())){
                menuObject.getTemplate().onClick(menuObject, event);

                MenuPageData pageData = menuObject.getPageData().get(menuObject.getPage());
                String componentId = pageData.getPageSlotData().getSlotComponents().get(event.getSlot());
                if (componentId != null){
                    MenuComponent component = menuObject.getTemplate().getComponents().get(componentId);
                    MenuComponentClickEvent componentClickEvent = new MenuComponentClickEvent(menuObject, event, component);
                    Bukkit.getPluginManager().callEvent(componentClickEvent);
                    if (componentClickEvent.isCancelled()){
                        return;
                    }

                    component.getAction().clickAction(menuObject, event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        getViewers().remove(event.getPlayer().getUniqueId());
    }

    public void registerTemplates(MenuTemplate... templates){
        if (templates.length > 0){
            for (MenuTemplate template : templates){
                template.setMenuManager(this);
                template.setMenuStorage(new StorageYML(new File(getMenuFolder(), template.getId() + ".yml")));
                template.getMenuStorage().read();
                template.saveComponents();
                template.loadData();

                getTemplates().put(template.getId(), template);
            }
        }
    }

    public void open(Player player, String templateId, int page){
        MenuTemplate template = getTemplates().get(templateId);

        if (template != null){
            player.closeInventory();
            MenuObject menuObject;
            UUID uuid = player.getUniqueId();

            if (getViewers().containsKey(uuid) && getViewers().get(uuid).getTemplate().getId().equalsIgnoreCase(templateId)){
                menuObject = getViewers().get(uuid);
                menuObject.setPage(page);
            }else{
                menuObject = template.createObject();
                getViewers().put(uuid, menuObject);
                player.openInventory(menuObject.getMenuView());
            }
        }
    }

    public HashMap<UUID, MenuObject> getViewers() {
        return viewers;
    }

    public HashMap<String, MenuTemplate> getTemplates() {
        return templates;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public File getMenuFolder() {
        return menuFolder;
    }
}
