package fr.insee.keycloak.http.user.storage.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.http.client.HttpClient;
import org.keycloak.broker.provider.util.SimpleHttp;

public class Utils {

    private Utils() {

    }

    public static SimpleHttp prepareRequest(HttpClient client, String method, String baseUrl, String path,
            Map<String, String> headers, String bodyType, List<String> body, Map<String, String> params) {
        SimpleHttp request = null;
        switch (method) {
            case "GET":
                request = SimpleHttp.doGet(baseUrl + Utils.inject(path, params), client).acceptJson();
                request = addHeaderAndBody(headers, bodyType, body, params, request);
                break;
            case "POST":
                request = SimpleHttp.doPost(baseUrl + Utils.inject(path, params), client).acceptJson();
                request = addHeaderAndBody(headers, bodyType, body, params, request);
                break;
        }
        return request;
    }

    private static SimpleHttp addHeaderAndBody(Map<String, String> headers, String bodyType, List<String> body,
            Map<String, String> params, SimpleHttp request) {
        // Add header to request
        if (headers != null) {
            for (String key : new ArrayList<>(headers.keySet())) {
                request = request.header(key, Utils.inject(headers.get(key), params));
            }
        }
        System.out.println(bodyType);
        switch (bodyType) {
            case "None":
                break;
            case "application/json":
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode json = mapper.readTree(Utils.inject(body.get(0), params));
                    request = request.json(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "application/x-www-form-urlencoded":
                System.out.println(params);
                System.out.println(body);
                Map<String, String> bodyParams = body.stream()
                        .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
                for (String bodyParamsKey : bodyParams.keySet()) {
                    request = request.param(bodyParamsKey, inject(bodyParams.get(bodyParamsKey), params));
                }
                break;
        }

        return request;
    }

    /**
     * Inject the params in the expression by replacing ${var} by the value of the
     * key var contained in the Params Map
     * 
     * @param expr
     * @param params
     * @return
     */
    private static String inject(String expr, Map<String, String> params) {
        return StrSubstitutor.replace(expr, params);
    }
}
