package by.it.academy.enterprise.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {
    private final Logger logger = LoggerFactory.getLogger("LOG");

    @RequestMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("greeting", "Hello Spring MVC");
        logger.info("Request /hello ${greeting} parameter set success");
        return "helloworld";

    }

}