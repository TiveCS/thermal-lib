package io.github.tivecs.thermallib.menu;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class MenuComponent {

    private String id;
    private HashMap<String, ItemStack> stateItems;
    private int minPage = -1, maxPage = -1;
    private int[] slots = {};
    private MenuComponentAction action;

    public MenuComponent(String id){
        this.action = new MenuComponentAction() {};
        this.id = id.toLowerCase();
        this.stateItems = new HashMap<>();
    }

    /**
     * Set used item for defined state
     *
     * @param state defined state
     * @param item item for defined state
     * @return component
     */
    public MenuComponent setStateItem(String state, ItemStack item){
        getStateItems().put(state, item);
        return this;
    }

    /**
     * Set action for this component on specific event (e.g. on click, on state update)
     *
     * @param action component action
     */
    public MenuComponent setAction(MenuComponentAction action) {
        this.action = action;
        return this;
    }

    /**
     * Set component's maximum page location
     *
     * @param maxPage maximum page location
     * @return component
     */
    public MenuComponent setMaxPage(int maxPage) {
        this.maxPage = maxPage;
        return this;
    }

    /**
     * Set component's minimum page location
     *
     * @param minPage minimum page location
     * @return component
     */
    public MenuComponent setMinPage(int minPage) {
        this.minPage = minPage;
        return this;
    }

    /**
     * Set component's location slot
     *
     * @param slots location slot
     * @return component
     */
    public MenuComponent setSlots(int[] slots) {
        this.slots = slots;
        return this;
    }

    public MenuComponent setStateItems(HashMap<String, ItemStack> stateItems) {
        this.stateItems = stateItems;
        return this;
    }

    public MenuComponentAction getAction() {
        return action;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public int getMinPage() {
        return minPage;
    }

    public int[] getSlots() {
        return slots;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, ItemStack> getStateItems() {
        return stateItems;
    }

    @Override
    public String toString() {
        return "MenuComponent{" +
                "id='" + id + '\'' +
                ", stateItems=" + stateItems +
                ", minPage=" + minPage +
                ", maxPage=" + maxPage +
                ", slots=" + Arrays.toString(slots) +
                ", action=" + action +
                '}';
    }
}
