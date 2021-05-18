package io.github.tivecs.thermallib.storage;

import java.util.Set;

public interface StorageAction {

    void read();
    void save();
    void set(String path, Object value);
    void setIfNotExists(String path, Object value);
    void remove(String path);
    boolean has(String path);
    Object get(String path);
    Set<String> findChild(String parent, boolean deep);

}
