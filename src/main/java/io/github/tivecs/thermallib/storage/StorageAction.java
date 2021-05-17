package io.github.tivecs.thermallib.storage;

public interface StorageAction {

    void read();
    void save();
    void set(String path, Object value);
    void setIfNotExists(String path, Object value);
    void remove(String path);
    boolean has(String path);

}
