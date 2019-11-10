package com.itkingk.bean.model.b;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * model a controller
 * @author 93633
 */
@RestController
@RequestMapping
public class ControllerB {
    @GetMapping("/hello")
    public String sayHello () {
        return "hello this is model b.";
    }
}
