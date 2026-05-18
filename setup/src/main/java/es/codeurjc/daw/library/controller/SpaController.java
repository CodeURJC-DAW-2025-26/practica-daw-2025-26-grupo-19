package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    // Redirects any request from the SPA to React's index.html.
    @RequestMapping({"/new", "/new/", "/new/**/{path:[^\\.]*}"})
    public String forwardToSpa() {
        return "forward:/new/index.html";
    }
}