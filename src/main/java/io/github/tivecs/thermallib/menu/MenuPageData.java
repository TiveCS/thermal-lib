package io.github.tivecs.thermallib.menu;

import java.util.Arrays;
import java.util.Collection;

public class MenuPageData {

    private final MenuObject menuObject;
    private final int page;
    private final MenuPageSlotData pageSlotData;
    private boolean isPrepared;

    public MenuPageData(MenuObject menuObject, int page) {
        this.menuObject = menuObject;
        this.page = page;
        this.isPrepared = false;
        this.pageSlotData = new MenuPageSlotData(this);
    }

    /**
     * Prepare configuration of used components on this page
     * @param components used components
     */
    public void prepare(Collection<MenuComponent> components){
        getPageSlotData().clear();

        this.isPrepared = false;
        for (MenuComponent component : components){
            int[] slots = component.getSlots();
            for (int slot : slots){
                getPageSlotData().getSlotStates().put(slot, "default");
                getPageSlotData().getSlotComponents().put(slot, component.getId());
                getPageSlotData().getSlotItems().put(slot, component.getStateItems().get("default").clone());
            }
        }

        this.isPrepared = true;
    }

    /**
     * Render prepared components and display into menu view
     */
    public void render(){
        getMenuObject().getMenuView().clear();
        for (int slot : getPageSlotData().getSlotItems().keySet()){
            getMenuObject().getMenuView().setItem(slot, getPageSlotData().getSlotItems().get(slot));
        }
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public int getPage() {
        return page;
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public MenuPageSlotData getPageSlotData() {
        return pageSlotData;
    }
}
