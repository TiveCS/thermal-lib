package io.github.tivecs.thermallib.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageYML extends Storage {

    private File file;
    private FileConfiguration config = null;

    public StorageYML(File file){
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

    @Override
    public void remove(String path) {
        // TODO add system when remove "some.path.here" will also remove "some.path.here.*" on data field
        getData().remove(path);
        getChangeHistory().put(path, ChangeHistory.REMOVE);
    }

    @Override
    public boolean has(String path) {
        return getConfig().contains(path);
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
