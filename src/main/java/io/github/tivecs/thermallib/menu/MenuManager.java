package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.menu.events.MenuComponentClickEvent;
import io.github.tivecs.thermallib.menu.events.MenuObjectCreateEvent;
import io.github.tivecs.thermallib.menu.events.MenuViewCloseEvent;
import io.github.tivecs.thermallib.menu.events.MenuViewOpenEvent;
import io.github.tivecs.thermallib.menu.exception.MenuTemplateNotFoundException;
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
    private HashMap<UUID, HashMap<String, MenuObject>> viewersObjectData; // viewer, <template id, menu object>
    private HashMap<UUID, MenuObject> viewers;
    private File menuFolder;

    public MenuManager(@Nonnull JavaPlugin plugin){
        this.plugin = plugin;
        this.menuFolder = new File(plugin.getDataFolder(), "menu");
        this.templates = new HashMap<>();
        this.viewers = new HashMap<>();
        this.viewersObjectData = new HashMap<>();

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
        if (getViewers().containsKey(event.getPlayer().getUniqueId())) {
            MenuObject menuObject = getViewers().get(event.getPlayer().getUniqueId());
            MenuViewCloseEvent viewCloseEvent = new MenuViewCloseEvent(menuObject, event);
            Bukkit.getPluginManager().callEvent(viewCloseEvent);
        }
        getViewers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onViewClose(MenuViewCloseEvent event){
        event.getMenuObject().getTemplate().onViewClose(event.getMenuObject(), event);
    }

    @EventHandler
    public void onViewOpen(MenuViewOpenEvent event){
        event.getMenuObject().getTemplate().onViewOpen(event.getMenuObject(), event);
    }

    @EventHandler
    public void onMenuObjectCreated(MenuObjectCreateEvent event){
        event.getTemplate().onMenuObjectCreated(event.getCreatedObject(), event);
    }

    /**
     * Register created template
     *
     * @param templates template list
     */
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

    /**
     * Open target player's menu object by its template id
     *
     * @param viewer the player who want to open menu
     * @param otherPlayer the target player that will be used to get menu object
     * @param templateId menu object's template id
     * @param page menu object's target page
     * @param inputMetadata input metadata to menu object if object is not exists before
     * @throws MenuTemplateNotFoundException throw when template is not found
     */
    public void open(Player viewer, UUID otherPlayer, String templateId, int page, HashMap<String, Object> inputMetadata) throws MenuTemplateNotFoundException {
        templateId = templateId.toLowerCase();
        MenuTemplate template = findTemplate(templateId);

        if (template != null){
            viewer.closeInventory();
            MenuObject menuObject;
            UUID viewerUniqueId = viewer.getUniqueId();

            HashMap<String, MenuObject> objectData = getPlayerViewerData(otherPlayer);

            MenuViewOpenEvent viewOpenEvent;
            boolean isAlreadyCreated = objectData.containsKey(template.getId());
            menuObject = getPlayerMenuObject(objectData, templateId, inputMetadata);
            viewOpenEvent = new MenuViewOpenEvent(menuObject, viewer, menuObject.getPage(), page, isAlreadyCreated);

            menuObject.setPage(page);
            if (!(getViewers().containsKey(viewerUniqueId) && getViewers().get(viewerUniqueId).getTemplate().getId().equalsIgnoreCase(templateId))){
                getViewers().put(viewerUniqueId, menuObject);
                objectData.put(template.getId(), menuObject);
                viewer.openInventory(menuObject.getMenuView());
            }

            Bukkit.getPluginManager().callEvent(viewOpenEvent);
        }
    }

    /**
     * Open target player's menu object by its template id
     *
     * @param viewer the player who want to open menu
     * @param otherPlayer the target player that will be used to get menu object
     * @param templateId menu object's template id
     * @param page menu object's target page
     * @throws MenuTemplateNotFoundException throw when template is not found
     */
    public void open(Player viewer, UUID otherPlayer, String templateId, int page) throws MenuTemplateNotFoundException {
        open(viewer, otherPlayer, templateId, page, new HashMap<>());
    }

    /**
     * Open player's own menu by its template id
     *
     * @param player the player who want to open menu
     * @param templateId menu object's template id
     * @param page menu object's target page
     * @throws MenuTemplateNotFoundException throw when template is not found
     */
    public void open(Player player, String templateId, int page) throws MenuTemplateNotFoundException {
        open(player, player.getUniqueId(), templateId, page);
    }

    /**
     * Find template by id
     *
     * @param templateId target template id
     * @return menu template
     * @throws MenuTemplateNotFoundException throw when template is not found
     */
    public MenuTemplate findTemplate(String templateId) throws MenuTemplateNotFoundException {
        templateId = templateId.toLowerCase();
        MenuTemplate template = getTemplates().get(templateId);
        if (template != null){
            return template;
        }else{
            throw new MenuTemplateNotFoundException(templateId);
        }
    }

    /**
     * Get menu object from viewer data
     *
     * @param viewerData target viewer data
     * @param templateId menu object's template id
     * @param inputMetadata input metadata to menu object
     * @return menu object
     */
    public MenuObject getPlayerMenuObject(HashMap<String, MenuObject> viewerData, String templateId, HashMap<String, Object> inputMetadata){
        templateId = templateId.toLowerCase();
        MenuObject menuObject = viewerData.get(templateId);
        if (menuObject == null){
            if (getTemplates().containsKey(templateId)){
                menuObject = getTemplates().get(templateId).createObject(inputMetadata);
            }
        }
        return menuObject;
    }

    /**
     * Get menu object from viewer data
     *
     * @param viewerData target viewer data
     * @param templateId menu object's template id
     * @return menu object
     */
    public MenuObject getPlayerMenuObject(HashMap<String, MenuObject> viewerData, String templateId){
        return getPlayerMenuObject(viewerData, templateId, new HashMap<>());
    }

    /**
     * Get target player viewer data
     *
     * @param playerUniqueId target player's uuid
     * @return viewer data
     */
    public HashMap<String, MenuObject> getPlayerViewerData(UUID playerUniqueId){
        if (getViewersObjectData().containsKey(playerUniqueId)){
            return getViewersObjectData().get(playerUniqueId);
        }else{
            HashMap<String, MenuObject> objectData = new HashMap<>();
            getViewersObjectData().put(playerUniqueId, objectData);
            return objectData;
        }
    }

    public HashMap<UUID, HashMap<String, MenuObject>> getViewersObjectData() {
        return viewersObjectData;
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
