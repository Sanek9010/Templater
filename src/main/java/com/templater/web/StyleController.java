package com.templater.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class StyleController {
    @RequestMapping(value = "/styles", method = RequestMethod.GET)
    public String getStyles(){
        //some code
        return "styles";
    }
}
