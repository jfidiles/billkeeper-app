package com.work.jfidiles.BillKeeper;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;

public class Rest {
    private static Rest _instance = null;
    public String error;
    public HttpStatus code;
    public static RestTemplate restTemplate;

    private Rest() {
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                error = StreamUtils.copyToString(response.getBody(),
                        Charset.defaultCharset());

                code = response.getStatusCode();
            }
        });
    }

    public static Rest getInstance() {
        if (_instance == null) {
            _instance = new Rest();
        }
        return _instance;
    }
}
