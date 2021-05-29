package io.github.tivecs.thermallib.menu.exception;

public class MenuTemplateNotFoundException extends Exception {

    public MenuTemplateNotFoundException(String templateId){
        super("Menu template not found: " + templateId);
    }

}
