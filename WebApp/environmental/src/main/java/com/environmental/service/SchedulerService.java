package com.environmental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;

@Service
public class SchedulerService {

    private final RestTemplate restTemplate;

    @Value("${esp32.url}")
    private String espUrl;

    public SchedulerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void requestMeasurements(){
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(espUrl, String.class);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
