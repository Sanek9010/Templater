package com.templater.web;

import com.templater.domain.Template;
import com.templater.repositories.TemplateRepository;
import com.templater.service.RequestContent;
import com.templater.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/templates/{templateId}")
public class PartController {

    @Autowired
    TemplateRepository templateRepo;

    @Autowired
    TemplateService templateService;

//    @RequestMapping(value = "paragraph", method = RequestMethod.GET)
//    public String getParagraph(@PathVariable Long templateId, ModelMap model){
//        model.put("templateId", templateId);
//        return "fragments/templateFragments :: paragraphFragment";
//    }

    @RequestMapping(value = "getParts", method = RequestMethod.GET)
    public String getParts(@PathVariable Long templateId, ModelMap modelMap){
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        String templateXml="";
        if(templateOpt.isPresent())
            templateXml = templateService.getTemplateXml(templateOpt.get());
        modelMap.put("templateXmlText", templateXml);
        return "fragments/templateFragments :: templateXml";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<?> createPart(@PathVariable Long templateId, @RequestBody RequestContent requestContent, HttpServletResponse response) throws IOException {
        templateService.savePart(requestContent,templateId);
        return ResponseEntity.ok("done");
}


}
