package io.github.tivecs.thermallib.menu;

import com.google.common.primitives.Ints;
import io.github.tivecs.thermallib.storage.StorageYML;
import io.github.tivecs.thermallib.utils.ItemBuilder;
import io.github.tivecs.thermallib.utils.StringUtils;
import io.github.tivecs.thermallib.utils.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MenuTemplate {

    private String id;
    private int rows;
    private String title;
    private StorageYML menuStorage = null;

    private MenuManager menuManager = null;
    private HashMap<String, MenuComponent> components;
    private HashMap<Integer, List<MenuComponent>> componentsPerPage;

    public MenuTemplate(String id, String title, int rows){
        this.id = id.toLowerCase();
        this.rows = rows;
        this.title = title;
        this.components = new HashMap<>();
        this.componentsPerPage = new HashMap<>();
    }

    /**
     * Executed when player clicked on menu view
     *
     * @param menuObject menu view's object
     * @param event click event
     */
    public abstract void onClick(MenuObject menuObject, InventoryClickEvent event);


    /**
     * Save existing components into file
     */
    public void saveComponents(){
        if (getMenuStorage() != null){
            getMenuStorage().setIfNotExists("title", getTitle());
            getMenuStorage().setIfNotExists("rows", getRows());

            for (MenuComponent component : getComponents().values()){
                String path = "components." + component.getId();
                getMenuStorage().setIfNotExists(path + ".min-page", component.getMinPage());
                getMenuStorage().setIfNotExists(path + ".max-page", component.getMaxPage());
                getMenuStorage().setIfNotExists(path + ".slots", component.getSlots());

                for (String state : component.getStateItems().keySet()){
                    String statePath = path + ".state-items." + state;
                    ItemStack item = component.getStateItems().get(state);
                    ItemMeta meta = item.getItemMeta();

                    getMenuStorage().setIfNotExists(statePath + ".material", XMaterial.matchXMaterial(item).name());
                    if (meta != null) {
                        if (meta.hasDisplayName()) {
                            getMenuStorage().setIfNotExists(statePath + ".display-name", meta.getDisplayName());
                        }
                        if (meta.hasLore()){
                            getMenuStorage().setIfNotExists(statePath + ".lore", meta.getLore());
                        }
                    }
                }
            }

            getMenuStorage().save();
        }
    }

    /**
     * Load saved data from file
     */
    @SuppressWarnings("unchecked")
    public void loadData(){
        setRows((Integer) getMenuStorage().get("rows"));
        setTitle(StringUtils.colored((String) getMenuStorage().get("title")));

        ConfigurationSection rootComponents = getMenuStorage().has("components") ? (ConfigurationSection) getMenuStorage().get("components") : null;
        if (rootComponents != null){
            for (String componentId : rootComponents.getKeys(false)){
                String path = "components." + componentId;
                MenuComponent component = null;

                ConfigurationSection rootStateItems = getMenuStorage().has(path + ".state-items") ? (ConfigurationSection) getMenuStorage().get(path + ".state-items") : null;
                if (rootStateItems != null){
                    for (String state : rootStateItems.getKeys(false)){
                        String statePath = path + ".state-items." + state;

                        XMaterial material;
                        String materialString = getMenuStorage().has(statePath + ".material") ? (String) getMenuStorage().get(statePath + ".material") : null;
                        if (materialString != null) {
                            material = XMaterial.matchXMaterial(materialString.toUpperCase()).get();

                            String displayName = getMenuStorage().has(statePath + ".display-name") ? StringUtils.colored((String) getMenuStorage().get(statePath + ".display-name")) : null;
                            List<String> lore = getMenuStorage().has(statePath + ".lore") ? StringUtils.colored((List<String>) getMenuStorage().get(statePath + ".lore")) : null;

                            component = addComponent(componentId, state, material, displayName, lore);
                        }
                    }
                }

                if (component != null){
                    component.setSlots(Ints.toArray((List<Integer>) getMenuStorage().get(path + ".slots")));
                }
            }
        }
    }

    /**
     * Create menu object
     *
     * @return created menu object
     */
    public MenuObject createObject(){
        MenuObject menuObject = new MenuObject(this);

        menuObject.initialize();

        return menuObject;
    }

    /**
     * Filter list of used components on selected page.
     *
     * @param page selected page
     * @param update list of used components on selected page will be updated if this parameter value is true
     * @return list of used components on selected page
     */
    public List<MenuComponent> filterComponentOnPage(int page, boolean update){
        List<MenuComponent> components = getComponentsPerPage().get(page);
        if (update || components == null) {
            components = new ArrayList<>();

            for (MenuComponent component : getComponents().values()) {
                if ((component.getMinPage() == -1 || component.getMinPage() <= page) && (component.getMaxPage() == -1 || component.getMaxPage() >= page)) {
                    components.add(component);
                }
            }

            getComponentsPerPage().put(page, components);
        }
        return components;
    }

    /**
     * Filter list of used components on selected page without update it.
     *
     * @param page selected page
     * @return list of used components on selected page
     */
    public List<MenuComponent> filterComponentOnPage(int page){
        return filterComponentOnPage(page, false);
    }

    /**
     * Create component with customized state item. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param state state address of state item
     * @param item component's state item
     * @return created component
     */
    public MenuComponent addComponent(String componentId, String state, ItemStack item) {
        MenuComponent component = getComponents().getOrDefault(componentId, new MenuComponent(componentId));
        if (!getComponents().containsKey(componentId)){
            getComponents().put(componentId, component);
        }

        return component.setStateItem(state, item);
    }

    /**
     * Create component with customized state item. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param state state address of state item
     * @param material material of state item
     * @param displayName display name of state item
     * @param lore lore of state item
     * @return created component
     */
    public MenuComponent addComponent(String componentId, String state, XMaterial material, String displayName, List<String> lore){
        ItemStack item = new ItemBuilder().setMaterial(material).setDisplayName(displayName).setLore(lore).build();
        return addComponent(componentId, state, item);
    }

    /**
     * Create component with customized state item, page and slot configuration. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param state state address of state item
     * @param item component's state item
     * @param minPage component's minimum page
     * @param maxPage component's maximum page
     * @param slots component's slots
     * @return created component
     */
    public MenuComponent addComponent(String componentId, String state, ItemStack item, int minPage, int maxPage, int[] slots) {
        MenuComponent component = getComponents().getOrDefault(componentId, new MenuComponent(componentId));
        if (!getComponents().containsKey(componentId)){
            getComponents().put(componentId, component);
        }

        return component.setMinPage(minPage).setMaxPage(maxPage).setSlots(slots).setStateItem(state, item);
    }

    /**
     * Create component with customized state item, page and slot configuration. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param state state address of state item
     * @param material material of state item
     * @param displayName display name of state item
     * @param lore lore of state item
     * @param minPage component's minimum page
     * @param maxPage component's maximum page
     * @param slots component's slots
     * @return created component
     */
    public MenuComponent addComponent(String componentId, String state, XMaterial material, String displayName, List<String> lore, int minPage, int maxPage, int[] slots){
        ItemStack item = new ItemBuilder().setMaterial(material).setDisplayName(displayName).setLore(lore).build();
        return addComponent(componentId, state, item, minPage, maxPage, slots);
    }

    /**
     * Create component with default state item, page and slot configuration. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param item component's state item
     * @param minPage component's minimum page
     * @param maxPage component's maximum page
     * @param slots component's slots
     * @return created component
     */
    public MenuComponent addComponent(String componentId, ItemStack item, int minPage, int maxPage, int[] slots){
        MenuComponent component = getComponents().getOrDefault(componentId, new MenuComponent(componentId));
        if (!getComponents().containsKey(componentId)){
            getComponents().put(componentId, component);
        }

        return component.setMinPage(minPage).setMaxPage(maxPage).setSlots(slots).setStateItem("default", item);
    }

    /**
     * Create component with default state item, page and slot configuration. <br>
     * If component already exists, the component will be edited instead.
     *
     * @param componentId component id
     * @param material material of state item
     * @param displayName display name of state item
     * @param lore lore of state item
     * @param minPage component's minimum page
     * @param maxPage component's maximum page
     * @param slots component's slots
     * @return created component
     */
    public MenuComponent addComponent(String componentId, XMaterial material, String displayName, List<String> lore, int minPage, int maxPage, int[] slots){
        ItemStack item = new ItemBuilder().setMaterial(material).setDisplayName(displayName).setLore(lore).build();
        return addComponent(componentId, item, minPage, maxPage, slots);
    }

    public void setMenuStorage(StorageYML menuStorage) {
        this.menuStorage = menuStorage;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected void setMenuManager(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    public HashMap<String, MenuComponent> getComponents() {
        return components;
    }

    public HashMap<Integer, List<MenuComponent>> getComponentsPerPage() {
        return componentsPerPage;
    }

    public StorageYML getMenuStorage() {
        return menuStorage;
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public String getId() {
        return id;
    }
}
