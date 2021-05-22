package io.github.tivecs.thermallib.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StorageYML extends Storage {

    private File file;
    private FileConfiguration config;

    public StorageYML(@Nonnull File file){
        super();
        this.file = file;
        if (!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(getFile());
    }

    @Override
    public void read() {
        getData().clear();

        for (String path : getConfig().getKeys(true)){
            getData().put(path, getConfig().get(path));
        }
    }

    @Override
    public void save() {
        try {
            List<String> keys = new ArrayList<>(getChangeHistory().keySet());
            for (String path : keys){
                ChangeHistory history = getChangeHistory().get(path);
                if (history.equals(ChangeHistory.EDIT)){
                    getConfig().set(path, getData().get(path));
                }else{
                    getConfig().set(path, null);
                }
                getChangeHistory().remove(path);
            }
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void set(String path, Object value) {
        // TODO add system when set "some.path.here" will also add "some.path" on data field
        getData().put(path, value);
        getChangeHistory().put(path, ChangeHistory.EDIT);
    }

    @Override
    public void setIfNotExists(String path, Object value) {
        if (!getData().containsKey(path)){
            set(path, value);
        }
    }

    public void createSection(String path){
        getData().put(path, getConfig().createSection(path));
        getChangeHistory().put(path, ChangeHistory.EDIT);
    }

    public void createSectionIfNotExists(String path){
        if (!getData().containsKey(path)){
            createSection(path);
        }
    }

    @Override
    public void remove(String path) {
        // TODO add system when remove "some.path.here" will also remove "some.path.here.*" on data field
        getData().remove(path);
        getChangeHistory().put(path, ChangeHistory.REMOVE);
    }

    @Override
    public boolean has(String path) {
        return getData().containsKey(path);
    }

    @Override
    public Object get(String path) {
        return getData().get(path);
    }

    @Override
    public Set<String> findChild(String parent, boolean deep) {
        if (deep) {
            return getData().keySet().stream().filter(path -> path.startsWith(parent)).collect(Collectors.toSet());
        }else{
            HashSet<String> sets = new HashSet<>();
            if (get(parent) != null){
                ConfigurationSection section = (ConfigurationSection) get(parent);
                for (String child : section.getKeys(false)){
                    String path = parent + "." + child;
                    sets.add(path);
                }
            }
            return sets;
        }
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
