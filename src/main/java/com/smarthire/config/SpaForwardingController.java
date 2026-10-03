package com.smarthire.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardingController {

    @GetMapping(value = {
        "/{path:^(?!api|h2-console|health|actuator|error)[^\\.]*}",
        "/{path1:^(?!api|h2-console|health|actuator|error)[^\\.]*}/{path2:[^\\.]*}",
        "/{path1:^(?!api|h2-console|health|actuator|error)[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}"
    })
    public String redirectSpaPaths() {
        return "forward:/index.html";
    }
}
