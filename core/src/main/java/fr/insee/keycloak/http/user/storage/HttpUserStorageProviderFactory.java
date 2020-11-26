package fr.insee.keycloak.http.user.storage;

import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

public class HttpUserStorageProviderFactory implements UserStorageProviderFactory<HttpUserStorageProvider> {
    public static final String PROVIDER_ID = "http-user-storage";

    protected static final List<ProviderConfigProperty> configMetadata;

    static {
        configMetadata = ProviderConfigurationBuilder.create()

                .property().name(HttpStorageConstants.BASE_URL).type(ProviderConfigProperty.STRING_TYPE).label("Url")
                .defaultValue("http://localhost:8080").helpText("Url for the user http api").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE).name(HttpStorageConstants.MAPPERS_MAP)
                .label("attribute mappers").add().property().type(ProviderConfigProperty.STRING_TYPE)
                .name(HttpStorageConstants.ID_ATTRIBUTE).label("id attribute").add()

                .property().type(ProviderConfigProperty.LIST_TYPE)
                .name(HttpStorageConstants.METHOD_VALIDATE_CREDENTIALS).label("method validateCredentials")
                .options(List.of("GET", "POST", "DELETE", "UPDATE"))
                .helpText("http method to use to validate users credentials").add()

                .property().type(ProviderConfigProperty.STRING_TYPE)
                .name(HttpStorageConstants.PATH_VALIDATE_CREDENTIALS).label("path validateCredentials")
                .helpText("http endpoint users credentials").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.HEADERS_VALIDATE_CREDENTIALS).label("header validateCredentials")
                .helpText("http headers to add to request to validate users credentials").add()

                .property().type(ProviderConfigProperty.LIST_TYPE)
                .name(HttpStorageConstants.BODY_TYPE_VALIDATE_CREDENTIALS).label("body type validateCredentials")
                .options(List.of("None", "application/json", "application/x-www-form-urlencoded")).defaultValue("None")
                .helpText("Type of the body part").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.BODY_VALIDATE_CREDENTIALS).label("body validateCredentials")
                .helpText("if body is type application/json use only first one").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.METHOD_GET_BY_USERNAME)
                .label("method getByUsername").options(List.of("GET", "POST", "DELETE", "UPDATE"))
                .helpText("http method to use").add()

                .property().type(ProviderConfigProperty.STRING_TYPE).name(HttpStorageConstants.PATH_GET_BY_USERNAME)
                .label("path getByUsername").helpText("http endpoint").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.HEADERS_GET_BY_USERNAME).label("header getByUsername")
                .helpText("http headers to add to request").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.BODY_TYPE_GET_BY_USERNAME)
                .label("body type getByUsername")
                .options(List.of("None", "application/json", "application/x-www-form-urlencoded")).defaultValue("None")
                .helpText("Type of the body part").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.BODY_GET_BY_USERNAME).label("body getByUsername")
                .helpText("if body is type application/json use only first one").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.METHOD_GET_BY_ID)
                .label("method getById").options(List.of("GET", "POST", "DELETE", "UPDATE"))
                .helpText("http method to use").add()

                .property().type(ProviderConfigProperty.STRING_TYPE).name(HttpStorageConstants.PATH_GET_BY_ID)
                .label("path getById").helpText("http endpoint").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.HEADERS_GET_BY_ID).label("header getById")
                .helpText("http headers to add to request").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.BODY_TYPE_GET_BY_ID)
                .label("body type getById")
                .options(List.of("None", "application/json", "application/x-www-form-urlencoded")).defaultValue("None")
                .helpText("Type of the body part").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.BODY_GET_BY_ID).label("body getById")
                .helpText("if body is type application/json use only first one").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.METHOD_GET_BY_EMAIL)
                .label("method getByEmail").options(List.of("GET", "POST", "DELETE", "UPDATE"))
                .helpText("http method to use").add()

                .property().type(ProviderConfigProperty.STRING_TYPE).name(HttpStorageConstants.PATH_GET_BY_EMAIL)
                .label("path getByEmail").helpText("http endpoint").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.HEADERS_GET_BY_EMAIL).label("header getByEmail")
                .helpText("http headers to add to request").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.BODY_TYPE_GET_BY_EMAIL)
                .label("body type getByEmail")
                .options(List.of("None", "application/json", "application/x-www-form-urlencoded")).defaultValue("None")
                .helpText("Type of the body part").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.BODY_GET_BY_EMAIL).label("body getByEmail")
                .helpText("if body is type application/json use only first one").add()

                .property().type(ProviderConfigProperty.LIST_TYPE).name(HttpStorageConstants.METHOD_UPDATE_CREDENTIAL)
                .label("method updateCredential").options(List.of("GET", "POST", "DELETE", "UPDATE"))
                .helpText("http method to use").add()

                .property().type(ProviderConfigProperty.STRING_TYPE).name(HttpStorageConstants.PATH_UPDATE_CREDENTIAL)
                .label("path updateCredential").helpText("http endpoint").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.HEADERS_GET_BY_EMAIL).label("header updateCredential")
                .helpText("http headers to add to request").add()

                .property().type(ProviderConfigProperty.LIST_TYPE)
                .name(HttpStorageConstants.BODY_TYPE_UPDATE_CREDENTIAL).label("body type updateCredential")
                .options(List.of("None", "application/json", "application/x-www-form-urlencoded")).defaultValue("None")
                .helpText("Type of the body part").add()

                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.BODY_UPDATE_CREDENTIAL).label("body updateCredential")
                .helpText("if body is type application/json use only first one").add()

                .build();

    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public HttpUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new HttpUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}
