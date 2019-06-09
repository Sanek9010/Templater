package com.templater.web;

import com.templater.domain.ParagraphStyle;
import com.templater.repositories.StyleRepository;
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
import java.util.List;
import java.util.Optional;

@Controller
public class StyleController {

    @Autowired
    private StyleRepository styleRepository;

    @RequestMapping(value = "/styles", method = RequestMethod.GET)
    public String getStyles(ModelMap model){
        List<ParagraphStyle> styles = styleRepository.findAll();
        model.put("styles",styles);
        return "styles";
    }

    @RequestMapping(value = "/styles", method = RequestMethod.POST)
    public String createStyle(){
        ParagraphStyle style = new ParagraphStyle();
        style = styleRepository.save(style);
        return "redirect:/styles/"+style.getId();
    }

    @RequestMapping(value = "/styles/{styleId}", method = RequestMethod.GET)
    public String templateView(@PathVariable Long styleId, ModelMap model, HttpServletResponse response) throws IOException {
        Optional<ParagraphStyle> styleOptional = styleRepository.findById(styleId);
        if(styleOptional.isPresent()){
            model.put("style", styleOptional.get());
            return "style";
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Style with id "+styleId+" not found");
            return "style";
        }
    }

    @RequestMapping(value = "/styles/{styleId}", method = RequestMethod.POST)
    public String updateTemplate(@PathVariable Long styleId, @ModelAttribute ParagraphStyle style){
        ParagraphStyle savedTemplate = styleRepository.save(style);
        return "redirect:/styles/"+styleId;
    }

    @RequestMapping(value = "/styles/{styleId}/delete", method = RequestMethod.GET)
    public String deleteTemplate(@PathVariable Long styleId){

        styleRepository.deleteById(styleId);
        return "redirect:/styles";
    }
}
