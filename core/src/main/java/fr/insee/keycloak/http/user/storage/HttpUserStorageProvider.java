package fr.insee.keycloak.http.user.storage;

import java.util.Collections;
import java.util.Set;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import fr.insee.keycloak.http.user.storage.service.HttpStoreService;

public class HttpUserStorageProvider
        implements UserStorageProvider, UserLookupProvider, CredentialInputValidator, CredentialInputUpdater {

    HttpStoreService httpStore;

    public HttpUserStorageProvider(KeycloakSession session, ComponentModel model) {
        httpStore = new HttpStoreService(session, model.getConfig(), model);
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        System.out.println("Validating " + user.getUsername() + ":" + credentialInput.getChallengeResponse());
        return httpStore.validateCredential(user.getUsername(), credentialInput.getChallengeResponse());
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        return httpStore.getById(StorageId.externalId(id));
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        return httpStore.getByUsername(username);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return httpStore.getByEmail(email);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        return httpStore.updateCredential(user, input);
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        //No-op
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }
    
}
