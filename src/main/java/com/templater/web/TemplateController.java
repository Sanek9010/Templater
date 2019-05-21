package com.templater.web;

import com.templater.domain.Template;
import com.templater.repositories.TemplateRepository;
import com.templater.service.TemplateService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateRepository templateRepo;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String indexView(){
        return "index";
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    public String templateList(){
        return "templates";
    }

    @RequestMapping(value = "/templates", method = RequestMethod.POST)
    public String createTemplate(){
        Template template = templateService.save(new Template());
        return "redirect:/templates/"+template.getId();
    }

    @RequestMapping(value = "/templates/{templateId}", method = RequestMethod.GET)
    public String templateView(@PathVariable Long templateId, ModelMap model, HttpServletResponse response) throws IOException {
        Optional<Template> template = templateRepo.findById(templateId);
        if(template.isPresent()){
            model.put("template", template.get());
            return "template";
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Template with id "+templateId+" not found");
            return "template";
        }
    }

    @RequestMapping(value = "/templates/{templateId}", method = RequestMethod.POST)
    public String saveTemplate(@PathVariable Long templateId, @ModelAttribute Template template){
        templateService.save(template);
        return "redirect:/templates";
    }
}
