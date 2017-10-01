package com.jm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jm.domain.PhilipsHueBridge;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * com.jm.service.AbstractHueService Class
 * <p>
 * Created by James Millner on 25/09/2017 at 23:12.
 */
@Service
public class AbstractHueService {

    @Autowired
    private RegisterService registerService;

    private enum ENDPOINTS {
        API_NEWDEVELOPER("/api/newdeveloper"),
        API("/api");

        private final String endpoint;

        ENDPOINTS(final String text) {
            this.endpoint = text;
        }

        public String getEndpoint() {
            return this.endpoint;
        }
    }

    private Log logger = LogFactory.getLog(this.getClass());

    private RestTemplate restTemplate = new RestTemplate();

    public String getPhilipsBridgeIP() throws JSONException {
        //https://www.meethue.com/api/nupnp
        final String URL = "https://www.meethue.com/api/nupnp";
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
        JSONObject object = new JSONArray(response.getBody()).getJSONObject(0);

        return object.get("internalipaddress").toString();
    }


    public void initConnectionToBridge() {
        try {
            String url = String.format("http://%s%s", getPhilipsBridgeIP(), ENDPOINTS.API_NEWDEVELOPER.getEndpoint());

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", ContentType.APPLICATION_JSON.toString());
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            logger.info(response.getBody());
        } catch (JSONException exc) {
            logger.fatal(exc);
        }
    }

    @PostConstruct
    public void checkConnectionToBridge() throws InterruptedException, IOException, JSONException {

        initConnectionToBridge();

        TimeUnit.SECONDS.sleep(3);

        String url = String.format("http://%s%s", getPhilipsBridgeIP(), ENDPOINTS.API.getEndpoint());

        //Replace hardcoded app name with username later.
        JSONObject body = new JSONObject().put("devicetype", String.format("%s#%s", "my_hue_app", "web_site"));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", ContentType.APPLICATION_JSON.toString());
        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.getBody()).get(0);

        logger.info(node.toString());

        if(!node.has("error")) {
            PhilipsHueBridge bridge = registerService.saveConnection(new PhilipsHueBridge(getPhilipsBridgeIP(), getUsernameFromBridge(node)));
            outputBridgeDetailsToLog(bridge);
        }

    }

    public void outputBridgeDetailsToLog(PhilipsHueBridge bridge) {
        logger.info("New bridge connection established...");
        logger.info(bridge.getId());
        logger.info(bridge.getIpAddress());
        logger.info(bridge.getUsername());
    }

    /**
     * Method to extract the username from the response returned after successfully pressing the
     * hue bridge.
     *
     * The username will then be used throughout each interaction with the bridge.
     *
     * @param response - The successful response from the bridge.
     * @return return the username supplied from the bridge.
     */
    public String getUsernameFromBridge(JsonNode response) {
        return response.path("success").path("username").asText();
    }



}
