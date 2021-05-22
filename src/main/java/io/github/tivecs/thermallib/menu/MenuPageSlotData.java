package io.github.tivecs.thermallib.menu;

import io.github.tivecs.thermallib.menu.events.MenuComponentStateUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MenuPageSlotData {

    private final MenuPageData pageData;
    private final HashMap<Integer, ItemStack> slotItems;
    private final HashMap<Integer, String> slotComponents;
    private final HashMap<Integer, String> slotStates;

    public MenuPageSlotData(MenuPageData pageData) {
        this.pageData = pageData;
        this.slotComponents = new HashMap<>();
        this.slotStates = new HashMap<>();
        this.slotItems = new HashMap<>();
    }

    public void clear() {
        getSlotComponents().clear();
        getSlotItems().clear();
        getSlotStates().clear();
    }

    /**
     * Update slot's state
     *
     * @param slot  target slot
     * @param state target slot's next state
     */
    public void updateState(int slot, String state) {
        String componentId = getSlotComponents().get(slot);
        MenuComponent component = getPageData().getMenuObject().getTemplate().getComponents().get(componentId);
        MenuObject menuObject = getPageData().getMenuObject();
        state = state.toLowerCase();

        MenuComponentStateUpdateEvent stateUpdateEvent = new MenuComponentStateUpdateEvent(menuObject, slot, getSlotStates().get(slot).toLowerCase(), state.toLowerCase());
        Bukkit.getPluginManager().callEvent(stateUpdateEvent);
        if (stateUpdateEvent.isCancelled()) {
            return;
        }

        getSlotStates().put(slot, state);
        getSlotItems().put(slot, component.getStateItems().get(state).clone());
        getPageData().render();
        component.getAction().stateUpdateAction(menuObject, stateUpdateEvent);
    }

    /**
     * Check if selected slot is same state as parameter state
     *
     * @param slot  selected slot
     * @param state checked state
     * @return true if state is same as parameter state
     */
    public boolean isOnState(int slot, String state) {
        return getSlotStates().get(slot).equalsIgnoreCase(state);
    }

    public MenuPageData getPageData() {
        return pageData;
    }

    public HashMap<Integer, String> getSlotComponents() {
        return slotComponents;
    }

    public HashMap<Integer, ItemStack> getSlotItems() {
        return slotItems;
    }

    public HashMap<Integer, String> getSlotStates() {
        return slotStates;
    }
}
