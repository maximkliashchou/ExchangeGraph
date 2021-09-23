package by.mmkle.plesko.ExchangeGraph.controller;

import by.mmkle.plesko.ExchangeGraph.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path ="/graph", produces = APPLICATION_JSON_VALUE)
public class GraphController {
    @Autowired
    private GraphService graphService;

    @GetMapping
    public String getGraph() throws IOException {
        return null;
    }
}
