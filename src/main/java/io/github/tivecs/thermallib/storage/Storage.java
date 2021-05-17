package io.github.tivecs.thermallib.storage;

import java.util.HashMap;

public abstract class Storage implements StorageAction {

    private HashMap<String, Object> data;
    private HashMap<String, ChangeHistory> changeHistory;

    public Storage(){
        this.data = new HashMap<>();
        this.changeHistory = new HashMap<>();
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public HashMap<String, ChangeHistory> getChangeHistory() {
        return changeHistory;
    }

}
