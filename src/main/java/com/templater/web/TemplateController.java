package com.templater.web;

import com.templater.domain.*;
import com.templater.repositories.TemplateRepository;
import com.templater.security.Authority;
import com.templater.service.TemplateService;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class TemplateController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private TemplateRepository templateRepo;

    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String indexView(){
        return "index";
    }

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    public String myTemplateList(ModelMap model,@AuthenticationPrincipal User user){
        List<Template> templates = templateRepo.findByUser(user);
        model.put("templates", templates);
        for (Authority authority:user.getAuthorities()) {
            if(authority.getAuthority().equals("ROLE_SUPERUSER")){
                return "templates";
            }
        }
        return "templatesForUsers";
    }

    @RequestMapping(value = "/allTemplates", method = RequestMethod.GET)
    public String templateList(ModelMap model){
        List<Template> templates = templateRepo.findByPrivateTemplate(false);

        model.put("templates", templates);
        return "allTemplates";
    }

    @Transactional
    @RequestMapping(value = "/templates/add/{templateId}", method = RequestMethod.GET)
    public String addToMyTemlates(@PathVariable Long templateId,@AuthenticationPrincipal User user){
        Optional<Template> templateOptional = templateRepo.findById(templateId);
        Template template = templateOptional.get();
        if(!template.getUser().getUsername().equals(user.getUsername())){
            Template templateClone = new Template(template);
            templateClone.setUser(user);
            em.persist(templateClone);
        }
        return "redirect:/allTemplates";
    }

    @RequestMapping(value = "/templates", method = RequestMethod.POST)
    public String createTemplate(@AuthenticationPrincipal User user){
        Template template = new Template();
        template.setUser(user);
        template.setNumberOfParts(0L);
        template =templateService.save(template);
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
    public String updateTemplate(@AuthenticationPrincipal User user, @PathVariable Long templateId, @ModelAttribute Template template){
        template.setUser(user);
        Template savedTemplate = templateService.save(template);
        return "redirect:/templates/"+templateId;
    }

    @RequestMapping(value = "/templates/{templateId}/delete", method = RequestMethod.GET)
    public String deleteTemplate(@PathVariable Long templateId){
        templateRepo.deleteById(templateId);
        return "redirect:/templates";
    }


    @RequestMapping(value = "/templates/{templateId}/getDocx", method = RequestMethod.GET)
    public ResponseEntity<Resource> getDocx(@PathVariable Long templateId, HttpServletResponse response) throws IOException {
        Optional<Template> templateOptional = templateRepo.findById(templateId);
        Template actualTemplate = new Template();
        if(templateOptional.isPresent()){
            actualTemplate=templateOptional.get();
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Template with id "+templateId+" not found");
        }
        String baseURL = System.getProperty("user.dir");
        String fileLocation = baseURL + "/OUT_from_XHTML.docx";
        File file = new java.io.File(fileLocation);
        try {
            Map<Long, Part> parts = templateService.getAllParts(actualTemplate);
            templateService.convertToDocx(parts, actualTemplate).save(file);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        return getResourceResponseEntity(file);
    }

    static ResponseEntity<Resource> getResourceResponseEntity(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.docx");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }
}
