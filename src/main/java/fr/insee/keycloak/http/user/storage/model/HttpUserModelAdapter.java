package fr.insee.keycloak.http.user.storage.model;

import java.util.List;
import java.util.Map;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class HttpUserModelAdapter extends AbstractUserAdapterFederatedStorage {

    private HttpUser httpUser;

    public HttpUserModelAdapter(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel,
            HttpUser httpUser) {
        super(session, realm, storageProviderModel);
        this.httpUser = httpUser;
    }

    @Override
    public String getUsername() {
        return httpUser.getAttributes().get(USERNAME).get(0);
    }

    @Override
    public void setUsername(String username) {
        getFederatedStorage().setSingleAttribute(realm, httpUser.getId(), "username", username);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((httpUser == null) ? 0 : httpUser.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        HttpUserModelAdapter other = (HttpUserModelAdapter) obj;
        if (httpUser == null) {
            if (other.httpUser != null)
                return false;
        } else if (!httpUser.equals(other.httpUser))
            return false;
        return true;
    }

    @Override
    public List<String> getAttribute(String name) {
        return httpUser.getAttributes().get(name);
    }

    @Override
    public Map<String, List<String>> getAttributes() {

        return httpUser.getAttributes();
    }

    @Override
    public String getEmail() {
        return httpUser.getAttributes().get(EMAIL).get(0);
    }

    @Override
    public String getFirstAttribute(String name) {
        System.out.println("getFirstAttribute(" + name + ")");
        return httpUser.getAttributes().get(name).get(0);
    }

    @Override
    public String getFirstName() {
        return httpUser.getAttributes().get(FIRST_NAME).get(0);
    }

    @Override
    public String getLastName() {
        return httpUser.getAttributes().get(LAST_NAME).get(0);
    }

    @Override
    public Long getCreatedTimestamp() {
        return null;
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        // NOOP
    }

    @Override
    public boolean isEmailVerified() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
   
    
    
}
