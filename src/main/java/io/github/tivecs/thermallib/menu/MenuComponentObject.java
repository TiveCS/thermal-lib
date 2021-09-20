package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.utils.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;

public class MenuComponentObject {

    private final MenuObject menuObject;
    private final MenuComponent component;
    private final int slot;

    private final HashMap<String, Object> state;
    private boolean isUpdated;

    private ItemStack objectItem;

    public MenuComponentObject(@Nonnull MenuObject menuObject, @Nonnull MenuComponent component, int slot){
        this.menuObject = menuObject;
        this.component = component;
        this.slot = slot;
        this.state = new HashMap<>(component.getState());
        this.isUpdated = false;
        this.objectItem = null;
    }

    public ItemStack createObjectItem(){
        ItemStack item = getComponent().getItem().clone();

        // TODO add state to item data (lore, displayName)
        item = useState(item);

        setObjectItem(item);
        this.isUpdated = true;
        return item;
    }

    public void updateObjectItem(){
        if (getObjectItem() != null){
            setObjectItem(createObjectItem());
            getMenuObject().getInventory().setItem(getSlot(), getObjectItem());
        }
    }

    public ItemStack useState(@Nonnull ItemStack item){

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = meta.hasDisplayName() ? meta.getDisplayName() : null;
            List<String> lore = meta.hasLore() ? meta.getLore() : null;

            if (displayName != null){
                displayName = useStateAsString(displayName);
                meta.setDisplayName(StringUtils.colored(displayName));
            }

            if (lore != null){
                for (int i = 0; i < lore.size(); i++){
                    String l = lore.get(i);
                    l = useStateAsString(l);
                    lore.set(i, l);
                }
                meta.setLore(StringUtils.colored(lore));
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public String useStateAsString(@Nonnull String stringUsingState){
        for (String stateKey : getState().keySet()){
            Object stateValue = getState().get(stateKey);
            stringUsingState = stringUsingState.replace(new StringBuilder("%").append(stateKey).append("%"), stateValue.toString());
        }
        return stringUsingState;
    }

    public void setState(String key, Object value){
        getState().put(key, value);
        this.isUpdated = false;
        updateObjectItem();
        getComponent().componentDidUpdate(this);
    }

    public void setState(HashMap<String, Object> newState){
        for (String key : newState.keySet()){
            getState().put(key, newState.get(key));
        }
        this.isUpdated = false;
        updateObjectItem();
        getComponent().componentDidUpdate(this);
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setObjectItem(ItemStack objectItem) {
        this.objectItem = objectItem;
    }

    public ItemStack getObjectItem() {
        return objectItem;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public HashMap<String, Object> getState() {
        return state;
    }

    public int getSlot() {
        return slot;
    }

    public MenuComponent getComponent() {
        return component;
    }
}
