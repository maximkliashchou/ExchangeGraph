package by.mmkle.plesko.ExchangeGraph.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path ="/hello", produces = APPLICATION_JSON_VALUE)
public class HelloWorldController {
    @GetMapping
    public String sayHello(){
        return "Hello World";
    }
}
