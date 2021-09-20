package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuObject {

    private MenuManager manager;
    private Menu menu;

    private Inventory inventory;

    private final HashMap<String, MenuPropsSlotMap> propsSlotMap;
    private final HashMap<Integer, HashMap<Integer, MenuComponent>> renderedSlotMap; // TODO for performance efficient. save page previous used component
    private final HashMap<Integer, MenuComponentObject> propsObject;

    private final HashMap<String, Object> state;
    private boolean isUpdated;

    public MenuObject(@Nonnull MenuManager manager, @Nonnull Menu menu){
        this.manager = manager;
        this.menu = menu;
        this.propsSlotMap = new HashMap<>(menu.getPropsSlotMap());
        this.renderedSlotMap = new HashMap<>();
        this.propsObject = new HashMap<>();
        this.state = new HashMap<>(menu.getState());

        this.isUpdated = false;

        initBaseInventory();
    }

    public void initBaseInventory(){
        if (menu.getTitle() == null){
            this.inventory = Bukkit.createInventory(null, menu.getRows()*9);
        }else{
            this.inventory = Bukkit.createInventory(null, menu.getRows()*9, StringUtils.colored(useStateAsString(menu.getTitle())));
        }
    }

    public void render(int page){
        getPropsObject().clear();

        for (MenuPropsSlotMap propsSlot : getPropsSlotMap().values()){
            boolean isMinPageCorrect = propsSlot.getMinPage() == 0 || (propsSlot.getMinPage() != -1 && propsSlot.getMinPage() <= page);
            boolean isMaxPageCorrect = propsSlot.getMaxPage() == 0 || (propsSlot.getMaxPage() != -1 && propsSlot.getMaxPage() >= page);
            boolean isDisabled = propsSlot.getMinPage() == -1 && propsSlot.getMaxPage() == -1;
            boolean isCorrectPage = isMaxPageCorrect && isMinPageCorrect && !isDisabled;

            if (isCorrectPage){
                List<Integer> slots = propsSlot.getSlots();
                for (int slot : slots){
                    getPropsObject().put(slot, propsSlot.getComponent().createObject(this, slot));
                }
            }
        }

        showComponent();
        this.isUpdated = true;
    }

    public void showComponent(){
        for (int slot : getPropsObject().keySet()){
            MenuComponentObject object = getPropsObject().get(slot);
            object.createObjectItem();
            getInventory().setItem(slot, object.getObjectItem());
        }
    }

    public void setPage(int page){
        setState("page", page, true);
    }

    public void useState(){

    }

    public String useStateAsString(@Nonnull String stringUsingState){

        for (String stateKey : getState().keySet()){
            Object stateValue = getState().get(stateKey);
            stringUsingState = stringUsingState.replace(new StringBuilder("%").append(stateKey).append("%"), stateValue.toString());
        }

        return stringUsingState;
    }

    public void setState(String key, Object value, boolean refreshInventory){
        getState().put(key, value);
        this.isUpdated = false;

        if (refreshInventory){
            List<HumanEntity> viewers = new ArrayList<>(getInventory().getViewers());
            initBaseInventory();
            for (HumanEntity viewer : viewers){
                viewer.openInventory(getInventory());
                getManager().getPlayerMenu().put(viewer.getUniqueId(), this);
            }
        }
        render((int) getState().get("page"));

        getMenu().menuDidUpdate(this);
    }

    public void setState(HashMap<String, Object> newState, boolean refreshInventory){
        for (String key : newState.keySet()){
            getState().put(key, newState.get(key));
        }
        this.isUpdated = false;

        if (refreshInventory){
            List<HumanEntity> viewers = new ArrayList<>(getInventory().getViewers());
            initBaseInventory();
            for (HumanEntity viewer : viewers){
                viewer.openInventory(getInventory());
                getManager().getPlayerMenu().put(viewer.getUniqueId(), this);
            }
        }
        render((int) getState().get("page"));

        getMenu().menuDidUpdate(this);
    }

    public void setState(String key, Object value){
        setState(key, value, false);
    }

    public void setState(HashMap<String, Object> newState){
        setState(newState, false);
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public int getPage(){
        int page;

        getState().putIfAbsent("page", 1);
        page = (int) getState().get("page");

        return page;
    }

    public HashMap<Integer, MenuComponentObject> getPropsObject() {
        return propsObject;
    }

    public HashMap<String, MenuPropsSlotMap> getPropsSlotMap() {
        return propsSlotMap;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Menu getMenu() {
        return menu;
    }

    public HashMap<String, Object> getState() {
        return state;
    }

    public MenuManager getManager() {
        return manager;
    }
}
