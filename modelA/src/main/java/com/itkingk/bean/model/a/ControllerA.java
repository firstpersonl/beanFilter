package com.itkingk.bean.model.a;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * model a controller
 * @author 93633
 */
@RestController
@RequestMapping
public class ControllerA {
    @GetMapping(value = "/hello")
    public String sayHello () {
        return "hello this is model a.";
    }
}
