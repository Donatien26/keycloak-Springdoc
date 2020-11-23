package fr.insee.keycloak.http.user.storage.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.http.client.HttpClient;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.provider.util.SimpleHttp.Response;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.connections.httpclient.HttpClientProvider;
import org.keycloak.credential.CredentialInput;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;

import fr.insee.keycloak.http.user.storage.HttpStorageConstants;
import fr.insee.keycloak.http.user.storage.model.HttpUser;
import fr.insee.keycloak.http.user.storage.model.HttpUserModelAdapter;
import fr.insee.keycloak.http.user.storage.utils.Utils;

public class HttpStoreService {

    private HttpClient client;
    private String baseUrl;
    private String idAttribute;
    private Map<String, String> mappers;
    private KeycloakSession session;
    private ComponentModel userStorageModel;
    private String validateCredentialsPath;
    private String validateCredentialsMethod;

    public HttpStoreService(KeycloakSession session, MultivaluedHashMap<String, String> multivaluedHashMap,
            ComponentModel userStorageModel) {
        this.session = session;
        this.userStorageModel = userStorageModel;
        this.client = session.getProvider(HttpClientProvider.class).getHttpClient();
        baseUrl = multivaluedHashMap.getFirst(HttpStorageConstants.BASE_URL);
        mappers = multivaluedHashMap.getList(HttpStorageConstants.MAPPERS_MAP).stream()
                .collect(Collectors.toMap(s -> s.split(";")[0], s -> s.split(";")[1]));
        validateCredentialsPath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_VALIDATE_CREDENTIALS);
        validateCredentialsMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_VALIDATE_CREDENTIALS);
        idAttribute = multivaluedHashMap.getFirst(HttpStorageConstants.ID_ATTRIBUTE);
    }

    public boolean validateCredential(String username, String password) {
        try {
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("username", username);
            params.put("password", password);
            System.out.println(Utils.inject(validateCredentialsPath, params));
            //Maybe a Switch ?
            if (validateCredentialsMethod.equals("POST")) {
                Response status = SimpleHttp.doPost(baseUrl + Utils.inject(validateCredentialsPath, params), client)
                        .acceptJson().asResponse();
                return status.getStatus() == 200;
            }
            else{
                Response status = SimpleHttp.doGet(baseUrl + Utils.inject(validateCredentialsPath, params), client)
                        .acceptJson().asResponse();
                return status.getStatus() == 200;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserModel getByUsername(String username) {
        try {
            JsonNode json = SimpleHttp.doGet(baseUrl + "/users?username=" + username, client).acceptJson().asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UserModel getUserFromJson(JsonNode json) {
        HttpUser user = new HttpUser();
        System.out.println(json);
        System.out.println(idAttribute);
        if (json.isArray()) {
            json = json.get(0);
        }
        user.setId(json.get(idAttribute).asText());

        Map<String, List<String>> attributes = new HashMap<>();

        for (Map.Entry<String, String> entry : mappers.entrySet()) {
            attributes.put(entry.getKey(), Arrays.asList(json.get(entry.getValue()).asText()));
        }
        user.setAttributes(attributes);
        return new HttpUserModelAdapter(session, session.getContext().getRealm(), userStorageModel, user);
    }

    public UserModel getById(String externalId) {
        try {
            JsonNode json = SimpleHttp.doGet(baseUrl + "/users/" + externalId, client).acceptJson().asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UserModel getByEmail(String email) {
        try {
            JsonNode json = SimpleHttp.doGet(baseUrl + "/users?email=" + email, client).acceptJson().asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCredential(UserModel user, CredentialInput input) {
        String id = StorageId.externalId(user.getId());
        try {
            return SimpleHttp.doPost(baseUrl + "/users/" + id + "/update-credential", client)
                    .param("newPassword", input.getChallengeResponse()).asStatus() == 200;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
