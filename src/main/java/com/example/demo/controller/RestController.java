package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("api")
public class RestController {

    @GetMapping("private/hello/{name}")
    public @ResponseBody String publicHello(@PathVariable String name) {
        return "private hello " + name;
    }

    @GetMapping("public/hello/{name}")
    public @ResponseBody String privateHello(@PathVariable String name) {
        return "hello " + name;
    }

}