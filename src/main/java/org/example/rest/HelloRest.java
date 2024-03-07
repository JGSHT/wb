package org.example.rest;


import org.example.core.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("hello")
public class HelloRest {

    @RequestMapping
    public Mono<R<String>> hello(@RequestParam("name") String name){
        return Mono.justOrEmpty(R.success("hello " + name));
    }

}
