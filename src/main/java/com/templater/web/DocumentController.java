package com.templater.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DocumentController {

    @RequestMapping(value = "/document/{documentId}", method = RequestMethod.GET)
    public String documentView(@PathVariable Long documentId){

        return "document";
    }

    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public String documentsView(){
        return "documents";
    }
}