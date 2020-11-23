package fr.insee.keycloak.http.user.storage.utils;

import java.util.Map;

import org.apache.commons.lang.text.StrSubstitutor;

public class Utils {

    private Utils() {

    }

    /**
     * Inject the params in the expression by replacing ${var} by the value of the
     * key var contained in the Params Map
     * 
     * @param expr
     * @param params
     * @return
     */
    public static String inject(String expr, Map<String, String> params) {
        return StrSubstitutor.replace(expr, params);
    }
}
