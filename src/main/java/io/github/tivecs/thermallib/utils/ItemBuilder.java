package io.github.tivecs.thermallib.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemBuilder {

    private XMaterial material;
    private String displayName = null;
    private List<String> lore = null;
    private int amount = 1;

    public ItemBuilder(){

    }

    public ItemStack build(){
        ItemStack item = getMaterial().parseItem();
        item.setAmount(getAmount());
        ItemMeta meta = item.getItemMeta();

        if (getDisplayName() != null){
            meta.setDisplayName(StringUtils.colored(getDisplayName()));
        }
        if (getLore() != null){
            meta.setLore(StringUtils.colored(getLore()));
        }

        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder setMaterial(XMaterial material) {
        this.material = material;
        return this;
    }

    public int getAmount() {
        return amount;
    }

    public XMaterial getMaterial() {
        return material;
    }

    public List<String> getLore() {
        return lore;
    }

    public String getDisplayName() {
        return displayName;
    }
}
