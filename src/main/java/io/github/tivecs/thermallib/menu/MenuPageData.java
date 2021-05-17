package io.github.tivecs.thermallib.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class MenuPageData {

    private MenuObject menuObject;
    private int page;
    private HashMap<Integer, ItemStack> slotItems;
    private HashMap<Integer, String> slotComponents;
    private HashMap<String, String> componentsState;
    private boolean isPrepared;

    public MenuPageData(MenuObject menuObject, int page) {
        this.menuObject = menuObject;
        this.page = page;
        this.isPrepared = false;
        this.slotItems = new HashMap<>();
        this.slotComponents = new HashMap<>();
        this.componentsState = new HashMap<>();
    }

    /**
     * Prepare configuration of used components on this page
     * @param components used components
     */
    public void prepare(Collection<MenuComponent> components){
        getSlotItems().clear();
        for (MenuComponent component : components){
            int[] slots = component.getSlots();
            for (int slot : slots){
                getSlotItems().put(slot, component.getStateItems().get("default").clone());
                getSlotComponents().put(slot, component.getId());
                getComponentsState().put(component.getId(), "default");
            }
        }

        this.isPrepared = true;
    }

    /**
     * Render prepared components and display into menu view
     */
    public void render(){
        getMenuObject().getMenuView().clear();
        for (int slot : getSlotItems().keySet()){
            getMenuObject().getMenuView().setItem(slot, getSlotItems().get(slot));
        }
    }

    /**
     * Update component's state
     *
     * @param componentId component's id
     * @param state component's next state
     */
    public void updateState(String componentId, String state){
        getComponentsState().put(componentId.toLowerCase(), state);
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public int getPage() {
        return page;
    }

    public HashMap<Integer, ItemStack> getSlotItems() {
        return slotItems;
    }

    public HashMap<String, String> getComponentsState() {
        return componentsState;
    }

    public HashMap<Integer, String> getSlotComponents() {
        return slotComponents;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }
}
