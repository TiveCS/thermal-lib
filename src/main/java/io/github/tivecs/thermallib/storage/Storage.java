package io.github.tivecs.thermallib.storage;

import java.util.HashMap;

public abstract class Storage {

    private final HashMap<String, Object> data;

    public Storage(){
        this.data = new HashMap<>();
    }

    public abstract void retrieveData();
    public abstract void saveData();
    public abstract Object get(String path);
    public abstract Object getOrDefault(String path, Object otherValue);
    public abstract void set(String path, Object value);
    public abstract void delete(String path);

    public HashMap<String, Object> getData() {
        return data;
    }

}
