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
    private Map<String, String> validateCredentialsHeader;
    private String validateCredentialsBodyType;
    private List<String> validateCredentialsBody;
    private String getByUsernamePath;
    private String getByUsernameMethod;
    private Map<String, String> getByUsernameHeader;
    private String getByUsernameBodyType;
    private List<String> getByUsernameBody;
    private String getByIdPath;
    private String getByIdMethod;
    private Map<String, String> getByIdHeader;
    private String getByIdBodyType;
    private List<String> getByIdBody;
    private String getByEmailPath;
    private String getByEmailMethod;
    private Map<String, String> getByEmailHeader;
    private String getByEmailBodyType;
    private List<String> getByEmailBody;
    private String updateCredentialPath;
    private String updateCredentialMethod;
    private List<String> updateCredentialBody;
    private String updateCredentialBodyType;
    private Map<String, String> updateCredentialHeader;

    public HttpStoreService(KeycloakSession session, MultivaluedHashMap<String, String> multivaluedHashMap,
            ComponentModel userStorageModel) {
        this.session = session;
        this.userStorageModel = userStorageModel;
        this.client = session.getProvider(HttpClientProvider.class).getHttpClient();

        baseUrl = multivaluedHashMap.getFirst(HttpStorageConstants.BASE_URL);
        mappers = multivaluedHashMap.getList(HttpStorageConstants.MAPPERS_MAP).stream()
                .collect(Collectors.toMap(s -> s.split(";")[0], s -> s.split(";")[1]));
        idAttribute = multivaluedHashMap.getFirst(HttpStorageConstants.ID_ATTRIBUTE);

        validateCredentialsPath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_VALIDATE_CREDENTIALS);
        validateCredentialsMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_VALIDATE_CREDENTIALS);
        validateCredentialsHeader = multivaluedHashMap.getList(HttpStorageConstants.HEADERS_VALIDATE_CREDENTIALS)
                .stream().collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        validateCredentialsBodyType = multivaluedHashMap.getFirst(HttpStorageConstants.BODY_TYPE_VALIDATE_CREDENTIALS);
        validateCredentialsBody = multivaluedHashMap.getList(HttpStorageConstants.BODY_VALIDATE_CREDENTIALS);

        getByUsernamePath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_GET_BY_USERNAME);
        getByUsernameMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_GET_BY_USERNAME);
        getByUsernameHeader = multivaluedHashMap.getList(HttpStorageConstants.HEADERS_GET_BY_USERNAME).stream()
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        getByUsernameBodyType = multivaluedHashMap.getFirst(HttpStorageConstants.BODY_TYPE_GET_BY_USERNAME);
        getByUsernameBody = multivaluedHashMap.getList(HttpStorageConstants.BODY_GET_BY_USERNAME);

        getByIdPath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_GET_BY_ID);
        getByIdMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_GET_BY_ID);
        getByIdHeader = multivaluedHashMap.getList(HttpStorageConstants.HEADERS_GET_BY_ID).stream()
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        getByIdBodyType = multivaluedHashMap.getFirst(HttpStorageConstants.BODY_TYPE_GET_BY_ID);
        getByIdBody = multivaluedHashMap.getList(HttpStorageConstants.BODY_GET_BY_ID);

        getByEmailPath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_GET_BY_EMAIL);
        getByEmailMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_GET_BY_EMAIL);
        getByEmailHeader = multivaluedHashMap.getList(HttpStorageConstants.HEADERS_GET_BY_EMAIL).stream()
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        getByEmailBodyType = multivaluedHashMap.getFirst(HttpStorageConstants.BODY_TYPE_GET_BY_EMAIL);
        getByEmailBody = multivaluedHashMap.getList(HttpStorageConstants.BODY_GET_BY_EMAIL);

        updateCredentialPath = multivaluedHashMap.getFirst(HttpStorageConstants.PATH_UPDATE_CREDENTIAL);
        updateCredentialMethod = multivaluedHashMap.getFirst(HttpStorageConstants.METHOD_UPDATE_CREDENTIAL);
        updateCredentialHeader = multivaluedHashMap.getList(HttpStorageConstants.HEADERS_UPDATE_CREDENTIAL).stream()
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
        updateCredentialBodyType = multivaluedHashMap.getFirst(HttpStorageConstants.BODY_TYPE_UPDATE_CREDENTIAL);
        updateCredentialBody = multivaluedHashMap.getList(HttpStorageConstants.BODY_UPDATE_CREDENTIAL);

    }

    public boolean validateCredential(String username, String password) {
        try {

            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("username", username);
            params.put("password", password);

            Response response = Utils
                    .prepareRequest(client, validateCredentialsMethod, baseUrl, validateCredentialsPath,
                            validateCredentialsHeader, validateCredentialsBodyType, validateCredentialsBody, params)
                    .asResponse();

            return response.getStatus() == 200;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public UserModel getByUsername(String username) {
        try {
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("username", username);

            JsonNode json = Utils.prepareRequest(client, getByUsernameMethod, baseUrl, getByUsernamePath,
                    getByUsernameHeader, getByUsernameBodyType, getByUsernameBody, params).asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UserModel getById(String externalId) {
        try {
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("id", externalId);
            JsonNode json = Utils.prepareRequest(client, getByIdMethod, baseUrl, getByIdPath, getByIdHeader,
                    getByIdBodyType, getByIdBody, params).asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UserModel getByEmail(String email) {
        try {
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("email", email);
            JsonNode json = Utils.prepareRequest(client, getByEmailMethod, baseUrl, getByEmailPath, getByEmailHeader,
                    getByEmailBodyType, getByEmailBody, params).asJson();
            return getUserFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCredential(UserModel user, CredentialInput input) {
        String id = StorageId.externalId(user.getId());
        try {
            Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            params.put("id", id);
            params.put("password", input.getChallengeResponse());
            Response response = Utils
                    .prepareRequest(client, updateCredentialMethod, baseUrl, updateCredentialPath,
                            updateCredentialHeader, updateCredentialBodyType, updateCredentialBody, params)
                    .asResponse();
            return response.getStatus() == 200;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
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

}
