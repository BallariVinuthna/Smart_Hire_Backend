package com.smarthire.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    @GetMapping(value = "/{path:^(?!api|h2-console|health|actuator|error)[^\\.]*}")
    public String redirectRootPaths() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/{path:^(?!api|h2-console|health|error).*$}/**/{subpath:[^\\.]*}")
    public String redirectSubPaths() {
        return "forward:/index.html";
    }
}
