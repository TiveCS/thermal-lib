package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.storage.StorageYML;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public abstract class Menu {

    // Core value
    private StorageYML storage;
    private File folder, file;

    // Menu value
    private final String menuId;
    private MenuComponent[] props;
    private HashMap<String, Object> state;
    private HashMap<String, MenuPropsSlotMap> propsSlotMap;

    // Inventory default values
    private String title;
    private int rows;

    public Menu(@Nonnull File folder, @Nonnull String menuId, String title, int rows){
        this.menuId = menuId;
        this.props = null;
        this.state = new HashMap<>();
        this.propsSlotMap = new HashMap<>();

        setTitle(title);
        setRows(rows);

        setFolder(folder);
        if (!folder.exists()) {
            folder.mkdir();
        }
        handleStorage();
        defaultState();

        initValues();
    }

    public Menu(@Nonnull File folder, @Nonnull String menuId, int rows){
        this(folder, menuId, null, rows);
    }

    public Menu(@Nonnull File folder, @Nonnull String menuId, String title){
        this(folder, menuId, title, 3);
    }

    public Menu(@Nonnull File folder, @Nonnull String menuId){
        this(folder, menuId, null, 3);
    }

    public abstract MenuComponent[] props();

    public void onClick(InventoryClickEvent event, MenuObject menuObject){}
    public void menuWillUpdate(@Nonnull MenuObject menuObject){}
    public void menuDidUpdate(@Nonnull MenuObject menuObject){}

    public MenuComponent findProps(@Nonnull String componentId){
        Optional<MenuComponent> found = Arrays.stream(getProps()).filter((component) -> component.getComponentId().equals(componentId)).findFirst();
        return found.orElse(null);
    }

    public void addPreState(String key, Object value){
        getState().put(key, value);
    }

    private void defaultState(){
        getState().put("page", 1);
    }

    private void handleStorage(){
        setFile(new File(getFolder(), getClass().getSimpleName() + ".yml"));
        setStorage(new StorageYML(getFile()));
        setProps(props());

        // default value for title and rows
        getStorage().setIfNotExists("options.title", getTitle());
        getStorage().setIfNotExists("options.rows", getRows());

        // handle props component's default value (material, name, amount, lore)
        if (isPropsExists()){
            for (MenuComponent component : getProps()){
                component.writeDefaultValues(this);
                component.writeSlotMapValues(this);
            }
        }

        getStorage().saveData();
    }

    private void initValues() {
        setTitle((String) getStorage().getData().getOrDefault("options.title", null));
        setRows((Integer) getStorage().getData().getOrDefault("options.rows", 3));

        if (isPropsExists()){
            for (MenuComponent component : getProps()){
                component.retrieveValues(this);
                component.createItem();

                component.retrieveSlotMapValues(this);
                getPropsSlotMap().put(component.getComponentId(), component.getPropsSlotMap());
            }
        }
    }

    private void setProps(MenuComponent[] props){
        this.props = props;
    }

    private void setStorage(StorageYML storage) {
        this.storage = storage;
    }

    private void setFile(File file){
        this.file = file;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void setFolder(File folder) {
        this.folder = folder;
    }

    public boolean isPropsExists(){
        return this.props != null && this.props.length > 0;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public MenuComponent[] getProps() {
        return props;
    }

    public File getFile() {
        return file;
    }

    public String getMenuId() {
        return menuId;
    }

    public HashMap<String, MenuPropsSlotMap> getPropsSlotMap() {
        return propsSlotMap;
    }

    public StorageYML getStorage() {
        return storage;
    }

    public File getFolder() {
        return folder;
    }

    public HashMap<String, Object> getState() {
        return state;
    }


}
