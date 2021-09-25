package by.mmkle.plesko.ExchangeGraph.controller;

import by.mmkle.plesko.ExchangeGraph.dto.GraphCandleDto;
import by.mmkle.plesko.ExchangeGraph.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/graph", produces = APPLICATION_JSON_VALUE)
public class GraphController {
    @Autowired
    private GraphService graphService;

    @PostMapping("/list/{code}")
    public List<GraphCandleDto> list(@PathVariable Integer code) {
        return graphService.list(code);
    }
}
