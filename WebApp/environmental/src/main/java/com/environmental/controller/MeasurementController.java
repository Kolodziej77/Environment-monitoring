package com.environmental.controller;

import com.environmental.model.Measurement;
import com.environmental.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Controller
public class MeasurementController {

    private final MeasurementRepository repository;

    public MeasurementController(MeasurementRepository repository) {
        this.repository = repository;
    }

    @Value("${esp32.url}")
    private String esp32Url;

    @GetMapping("/index")
    public String index(Model model){
        model.addAttribute("measurements", repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
        return "index";
    }

    @PostMapping("/measure")
    public String trigger(){
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(esp32Url))
                    .GET()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/index";
    }

    @PostMapping("/api/data")
    public ResponseEntity<Void> receiveData(@RequestBody Measurement measurement){
        repository.save(measurement);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/chart-data")
    @ResponseBody
    public List<Measurement> getChartData(){
        return repository.findAll(Sort.by(Sort.Direction.ASC, "timestamp"));
    }

    @GetMapping("/charts")
    public String charts(){
        return "charts";
    }

}
