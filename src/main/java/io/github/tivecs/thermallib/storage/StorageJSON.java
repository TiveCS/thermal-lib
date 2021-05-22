package io.github.tivecs.thermallib.storage;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Set;

public class StorageJSON extends Storage {

    private File file;

    public StorageJSON(@Nonnull File file){
        super();
        this.file = file;
    }

    @Override
    public void read() {

    }

    @Override
    public void save() {

    }

    @Override
    public void set(String path, Object value) {

    }

    @Override
    public void setIfNotExists(String path, Object value) {

    }

    @Override
    public void remove(String path) {

    }

    @Override
    public boolean has(String path) {
        return false;
    }

    @Override
    public Object get(String path) {
        return null;
    }

    @Override
    public Set<String> findChild(String parent, boolean deep) {
        return null;
    }

    public File getFile() {
        return file;
    }
}
