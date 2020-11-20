package fr.insee.keycloak.http.user.storage.model;

import java.util.List;
import java.util.Map;

public class HttpUser {
    private String id;
    private Map<String, List<String>> attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String key, List<String> value){
        this.attributes.put(key, value);
    }
}
