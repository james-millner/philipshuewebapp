package com.jm.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * com.jm.service.AbstractHueService Class
 * <p>
 * Created by James Millner on 25/09/2017 at 23:12.
 */
@Service
public class AbstractHueService {

    private Log logger = LogFactory.getLog(this.getClass());

    private RestTemplate restTemplate = new RestTemplate();

    @PostConstruct
    public String getPhilipsBridgeIP() throws JSONException {
        //https://www.meethue.com/api/nupnp
        final String URL = "https://www.meethue.com/api/nupnp";
        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, request, String.class);
        JSONObject object = new JSONArray(response.getBody()).getJSONObject(0);

        logger.info(object.get("internalipaddress").toString());
        return object.get("internalipaddress").toString();
    }
    
}
