package io.github.tivecs.thermallib.menu;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MenuPropsSlotMap {

    private Menu menu;
    private MenuComponent component;

    private int minPage, maxPage;
    private List<Integer> slots;

    public MenuPropsSlotMap(@Nonnull Menu menu, @Nonnull MenuComponent component){
        this.menu = menu;
        this.component = component;
        this.minPage = 1;
        this.maxPage = 1;
        this.slots = new ArrayList<>();
    }

    public MenuPropsSlotMap setMap(int minPage, int maxPage, int[] slots){
        this.minPage = minPage;
        this.maxPage = maxPage;
        return setSlots(slots);
    }

    public MenuPropsSlotMap setPage(int minPage, int maxPage){
        this.minPage = minPage;
        this.maxPage = maxPage;
        return this;
    }

    public MenuPropsSlotMap setMinPage(int minPage){
        this.minPage = minPage;
        return this;
    }

    public MenuPropsSlotMap setMaxPage(int maxPage){
        this.maxPage = maxPage;
        return this;
    }

    public MenuPropsSlotMap setSlots(int[] slots){
        getSlots().clear();

        for (int slot : slots){
            getSlots().add(slot);
        }

        return this;
    }

    public MenuPropsSlotMap setSlots(List<Integer> slotsList){
        getSlots().clear();
        getSlots().addAll(slotsList);
        return this;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public int getMinPage() {
        return minPage;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public Menu getMenu() {
        return menu;
    }

    public MenuComponent getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return "MenuPropsSlotMap{" +
                "menu=" + menu +
                ", component=" + component.getComponentId() +
                ", minPage=" + minPage +
                ", maxPage=" + maxPage +
                ", slots=" + slots +
                '}';
    }
}
