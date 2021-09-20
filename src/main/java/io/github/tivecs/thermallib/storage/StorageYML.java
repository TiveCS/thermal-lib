package io.github.tivecs.thermallib.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class StorageYML extends Storage {

    private final File file;
    private final HashSet<String> updatePaths;

    private FileConfiguration config = null;

    public StorageYML(@Nonnull File file){
        super();
        this.file = file;
        this.updatePaths = new HashSet<>();

        createIfNotExists();
        retrieveData();
    }

    private void createIfNotExists(){
        if (!getFile().exists()){
            try {
                getFile().createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void setConfig(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public void retrieveData() {
        setConfig(YamlConfiguration.loadConfiguration(getFile()));

        getData().clear();
        getUpdatePaths().clear();
        for (String path : getConfig().getRoot().getKeys(true)){
            getData().put(path, getConfig().get(path));
        }
    }

    @Override
    public void saveData() {
        for (String updatedPath : getUpdatePaths()){
            getConfig().set(updatedPath, getData().get(updatedPath));
        }
        getUpdatePaths().clear();

        try {
            getConfig().save(getFile());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Object get(String path) {
        return getData().get(path);
    }

    @Override
    public Object getOrDefault(String path, Object otherValue) {
        return getData().getOrDefault(path, otherValue);
    }

    @Override
    public void set(String path, Object value) {
        if (value == null){
            // if value is null, is same as delete path
            delete(path);
        }else{
            getData().put(path, value);
            getUpdatePaths().add(path);

            // if parent change value, its children will no longer exists
            HashSet<String> children = getChildrenPath(path);
            for (String childPath : children){
                getData().remove(childPath);
                getUpdatePaths().add(childPath);
            }
        }
    }

    @Override
    public void delete(String path) {
        if (getData().containsKey(path)){
            // remove parent
            getData().remove(path);
            getUpdatePaths().add(path);

            HashSet<String> children = getChildrenPath(path);
            for (String childPath : children){
                getData().remove(childPath);
                getUpdatePaths().add(childPath);
            }
        }
    }

    // TODO untested, so it's unsafe
    public void createSection(String path){
        if (!getData().containsKey(path)){
            ConfigurationSection createdSection = getConfig().createSection(path);
            getData().put(path, createdSection);
            getUpdatePaths().add(path);
        }
    }

    public void setIfNotExists(String path, Object value){
        if (!getData().containsKey(path)){
            getData().put(path, value);
            getUpdatePaths().add(path);
        }
    }

    public HashSet<String> getChildrenPath(String parentPath){
        HashSet<String> childrens = new HashSet<>();
        Object parent = getData().get(parentPath);

        if (parent instanceof ConfigurationSection){
            ConfigurationSection parentSection = (ConfigurationSection) parent;

            for (String children : parentSection.getKeys(true)){
                childrens.add(parentPath + "." + children);
            }
        }

        return childrens;
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public HashSet<String> getUpdatePaths() {
        return updatePaths;
    }
}
