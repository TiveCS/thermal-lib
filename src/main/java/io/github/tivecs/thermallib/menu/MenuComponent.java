package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.storage.StorageYML;
import io.github.tivecs.thermallib.utils.StringUtils;
import io.github.tivecs.thermallib.utils.XMaterial;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class MenuComponent {

    private String componentId;
    private XMaterial material;
    private final Menu menu;

    private final HashMap<String, Object> state;

    private MenuPropsSlotMap propsSlotMap;
    private String key;
    private boolean isUnique;

    private ItemStack item = null;
    private String name = null;
    private int amount = 1;
    private List<String> lore = null;

    public MenuComponent(@Nonnull String componentId, @Nonnull String key, @Nonnull XMaterial material, @Nonnull Menu menu){
        this.menu = menu;
        this.key = key;
        this.state = new HashMap<>();
        this.isUnique = true;
        this.propsSlotMap = new MenuPropsSlotMap(menu, this);

        setComponentId(componentId);
        setMaterial(material);
    }
    public MenuComponent(@Nonnull String componentId, @Nonnull XMaterial material, @Nonnull Menu menu){
        this(componentId, "default", material, menu);
    }

    public void addPreState(String key, Object value){
        getState().put(key, value);
    }

    public void componentWillUpdate(@Nonnull MenuComponentObject componentObject){ }
    public void componentDidUpdate(@Nonnull MenuComponentObject componentObject){ }

    public MenuComponentObject createObject(@Nonnull MenuObject menuObject, int slot){
        return new MenuComponentObject(menuObject, this, slot);
    }

    public void writeSlotMapValues(@Nonnull Menu menu){
        String path = "map." + getComponentId() + "." + getKey();
        StorageYML storage = menu.getStorage();

        storage.setIfNotExists(path + ".min-page", getPropsSlotMap().getMinPage());
        storage.setIfNotExists(path + ".max-page", getPropsSlotMap().getMaxPage());
        storage.setIfNotExists(path + ".slots", getPropsSlotMap().getSlots());
    }

    @SuppressWarnings("unchecked")
    public void retrieveSlotMapValues(@Nonnull Menu menu){
        String path = "map." + getComponentId() + "." + getKey();
        StorageYML storage = menu.getStorage();

        getPropsSlotMap().setMinPage((Integer) storage.get(path + ".min-page"));
        getPropsSlotMap().setMaxPage((Integer) storage.get(path + ".max-page"));
        if (storage.getData().containsKey(path + ".slots")) {
            List<Integer> slotsList = new ArrayList<>((List<Integer>) storage.get(path + ".slots"));
            getPropsSlotMap().setSlots(slotsList);
        }
    }

    public void writeDefaultValues(@Nonnull Menu menu){
        String path = "props." + getComponentId();
        StorageYML storage = menu.getStorage();

        storage.setIfNotExists(path + ".material", getMaterial().name());
        if (getName() != null) {
            storage.setIfNotExists(path + ".name", getName());
        }
        storage.setIfNotExists(path + ".amount", getAmount());
        if (getLore() != null) {
            storage.setIfNotExists(path + ".lore", getLore());
        }

    }

    @SuppressWarnings("unchecked")
    public void retrieveValues(@Nonnull Menu menu){
        String path = "props." + getComponentId();
        StorageYML storage = menu.getStorage();

        Optional<XMaterial> mat = XMaterial.matchXMaterial((String) storage.get(path + ".material"));
        setMaterial(mat.orElse(XMaterial.STONE));

        if (storage.getData().containsKey(path + ".amount")) {
            setAmount((Integer) storage.get(path + ".amount"));
        }
        if (storage.getData().containsKey(path + ".name")) {
            setName((String) storage.get(path + ".name"));
        }
        if (storage.getData().containsKey(path + ".lore")) {
            setLore((List<String>) storage.get(path + ".lore"));
        }

    }

    public void createItem(){
        ItemStack item = getMaterial().parseItem();

        if (item != null) {
            item.setAmount(getAmount());
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                if (getName() != null) {
                    meta.setDisplayName(StringUtils.colored(getName()));
                }
                if (getLore() != null) {
                    meta.setLore(StringUtils.colored(getLore()));
                }
            }
            item.setItemMeta(meta);
        }

        this.item = item;
    }

    private void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public MenuComponent setKey(String key) {
        this.key = key;
        return this;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMaterial(XMaterial material) {
        this.material = material;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean hasPropsSlotMap(){
        return getPropsSlotMap().getSlots().isEmpty();
    }

    public boolean isUnique() {
        return isUnique;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getKey() {
        return key;
    }

    public MenuPropsSlotMap getPropsSlotMap() {
        return propsSlotMap;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> getState() {
        return state;
    }

    public Menu getMenu() {
        return menu;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public String getComponentId() {
        return componentId;
    }

    @Override
    public String toString() {
        return "MenuComponent{" +
                "componentId='" + componentId + '\'' +
                ", material=" + material +
                ", menu=" + menu +
                ", state=" + state +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", lore=" + lore +
                '}';
    }


}
