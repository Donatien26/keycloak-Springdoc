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
                .property().name(HttpStorageConstants.BASE_URL)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Url")
                .defaultValue("http://localhost:8080")
                .helpText("Url for the user http api")
                .add()
                .property().type(ProviderConfigProperty.MULTIVALUED_STRING_TYPE)
                .name(HttpStorageConstants.MAPPERS_MAP)
                .label("attribute mappers")
                .add()
                .property().type(ProviderConfigProperty.STRING_TYPE)
                .name(HttpStorageConstants.ID_ATTRIBUTE)
                .label("id attribute")
                .add()
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
