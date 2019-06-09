package com.templater.web;

import com.templater.domain.ParagraphStyle;
import com.templater.domain.Part;
import com.templater.domain.Template;
import com.templater.repositories.StyleRepository;
import com.templater.repositories.TemplateRepository;
import com.templater.service.EditPartPOJO;
import com.templater.service.RequestContent;
import com.templater.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/templates/{templateId}")
public class PartController {

    @Autowired
    private TemplateRepository templateRepo;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private StyleRepository styleRepository;

//    @RequestMapping(value = "paragraph", method = RequestMethod.GET)
//    public String getParagraph(@PathVariable Long templateId, ModelMap model){
//        model.put("templateId", templateId);
//        return "fragments/templateFragments :: paragraphFragment";
//    }

    @RequestMapping(value = "getParts", method = RequestMethod.GET)
    public String getParts(@PathVariable Long templateId, ModelMap modelMap){
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Map<Long, Part> templateXml = new HashMap<>();
        if(templateOpt.isPresent())
            templateXml = templateService.getAllParts(templateOpt.get());
        modelMap.put("templateXmlMap", templateXml);
        return "fragments/templateFragments :: templateXml";
    }

    @RequestMapping(value = "getStyles", method = RequestMethod.GET)
    public String getStyles(@PathVariable Long templateId, ModelMap modelMap){
        List<ParagraphStyle> styles = styleRepository.findAll();
        modelMap.put("styles", styles);
        return "fragments/templateFragments :: selectStyle";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<?> createPart(@PathVariable Long templateId, @RequestBody RequestContent requestContent, HttpServletResponse response) throws IOException {
        //todo использовать выбраный стиль
        templateService.savePart(requestContent,templateId);
        return ResponseEntity.ok("done");
    }

    @RequestMapping(value = "deletePart", method = RequestMethod.POST)
    public ResponseEntity<?> deletePart(@PathVariable Long templateId, @RequestBody Integer requestContent, HttpServletResponse response) throws IOException {
        templateService.deletePart(requestContent,templateId);
        return ResponseEntity.ok("done");
    }

    @RequestMapping(value = "editPart", method = RequestMethod.POST)
    public ResponseEntity<?> editPart(@PathVariable Long templateId, @RequestBody EditPartPOJO requestContent, HttpServletResponse response) throws IOException {
        templateService.editPart(requestContent,templateId);
        return ResponseEntity.ok("done");
    }


}
