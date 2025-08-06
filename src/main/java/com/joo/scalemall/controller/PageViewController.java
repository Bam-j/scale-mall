package com.joo.scalemall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class PageViewController {

    @GetMapping
    public String index(){
        return "index";
    }
}
