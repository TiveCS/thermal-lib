package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.menu.events.MenuObjectMetadataUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

/**
 * Replication of MenuTemplate that will be used for each player independently
 */
public class MenuObject {

    private MenuTemplate template;
    private Inventory menuView;
    private HashMap<Integer, MenuPageData> pageData;
    private HashMap<String, Object> metadata;
    private int page;

    public MenuObject(MenuTemplate template) {
        this.template = template;
        this.page = 1;
        this.pageData = new HashMap<>();
        this.metadata = new HashMap<>();
    }

    /**
     * Create menu view and render first page
     */
    public void initialize() {
        setMenuView(Bukkit.createInventory(null, getTemplate().getRows()*9, getTemplate().getTitle()));
        render(1);
    }

    /**
     * render page data on specific page
     *
     * @param page rendered page data
     */
    public void render(int page) {
        MenuPageData pageData = getPageData().get(page);
        if (pageData == null){
            pageData = new MenuPageData(this, page);
            getPageData().put(page, pageData);
        }

        pageData.prepare(getTemplate().filterComponentOnPage(page));
        pageData.render();
    }

    public void setTemplate(MenuTemplate template) {
        this.template = template;
    }

    /**
     * Update menu object's current page and components
     *
     * @param page target update page
     */
    public void setPage(int page) {
        this.page = page;
        render(getPage());
    }

    /**
     *
     * @param key
     * @param value
     */
    public void updateMetadata(String key, Object value){
        MenuObjectMetadataUpdateEvent.UpdateAction updateAction = getMetadata().containsKey(key) ? MenuObjectMetadataUpdateEvent.UpdateAction.UPDATE : MenuObjectMetadataUpdateEvent.UpdateAction.CREATE;

        MenuObjectMetadataUpdateEvent updateEvent = new MenuObjectMetadataUpdateEvent(this, key, getMetadata().get(key), value, updateAction);
        Bukkit.getPluginManager().callEvent(updateEvent);

        if (!updateEvent.isCancelled()){
            getMetadata().put(key, value);
        }
    }

    /**
     *
     * @param key
     */
    public void deleteMetadata(String key){
        MenuObjectMetadataUpdateEvent.UpdateAction updateAction = MenuObjectMetadataUpdateEvent.UpdateAction.DELETE;

        MenuObjectMetadataUpdateEvent updateEvent = new MenuObjectMetadataUpdateEvent(this, key, getMetadata().get(key), null, updateAction);
        Bukkit.getPluginManager().callEvent(updateEvent);

        if (!updateEvent.isCancelled()) {
            getMetadata().remove(key);
        }
    }

    public void setMenuView(Inventory menuView) {
        this.menuView = menuView;
    }

    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    public HashMap<Integer, MenuPageData> getPageData() {
        return pageData;
    }

    public int getPage() {
        return page;
    }

    public Inventory getMenuView() {
        return menuView;
    }

    public MenuTemplate getTemplate() {
        return template;
    }
}
