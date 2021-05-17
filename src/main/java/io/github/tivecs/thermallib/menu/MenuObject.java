package io.github.tivecs.thermallib.menu;

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
    private int page;

    public MenuObject(MenuTemplate template) {
        this.template = template;
        this.page = 1;
        this.pageData = new HashMap<>();
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

    public void setMenuView(Inventory menuView) {
        this.menuView = menuView;
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
