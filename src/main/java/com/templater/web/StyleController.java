package com.templater.web;

import com.templater.domain.ParagraphStyle;
import com.templater.domain.StyleInterface;
import com.templater.domain.TableStyle;
import com.templater.repositories.StyleRepository;
import com.templater.repositories.TableStyleRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class StyleController {

    @Autowired
    private StyleRepository styleRepository;
    @Autowired
    private TableStyleRepository tableStyleRepository;

    @RequestMapping(value = "/styles", method = RequestMethod.GET)
    public String getStyles(ModelMap model){
        List<ParagraphStyle> styles = styleRepository.findAll();
        List<TableStyle> tableStyles = tableStyleRepository.findAll();
        List<StyleInterface> stylesInt = new ArrayList<>(styles);
        stylesInt.addAll(tableStyles);
        model.put("styles",stylesInt);
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
        styleRepository.save(style);
        return "redirect:/styles/"+styleId;
    }

    @RequestMapping(value = "/styles/{styleId}/delete", method = RequestMethod.GET)
    public String deleteTemplate(@PathVariable Long styleId){
        styleRepository.deleteById(styleId);
        return "redirect:/styles";
    }

    @RequestMapping(value = "/styles/tableStyles/{styleId}", method = RequestMethod.GET)
    public String tableStyleView(@PathVariable Long styleId, ModelMap model, HttpServletResponse response) throws IOException {
        Optional<TableStyle> styleOptional = tableStyleRepository.findById(styleId);
        List<ParagraphStyle> styles = styleRepository.findAll();
        if(styleOptional.isPresent()){
            model.put("style", styleOptional.get());
            model.put("paragraphStyles", styles);
            return "tableStyle";
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Style with id "+styleId+" not found");
            return "tableStyle";
        }
    }

    @RequestMapping(value = "/styles/tableStyles/{styleId}", method = RequestMethod.POST)
    public String tableStyleView(@PathVariable Long styleId, @ModelAttribute TableStyle style) {
        tableStyleRepository.save(style);
        return "redirect:/styles/";
    }

    @RequestMapping(value = "/styles/createTableStyle", method = RequestMethod.POST)
    public String createTableStyle(){
        TableStyle style = new TableStyle();
        style = tableStyleRepository.save(style);
        return "redirect:/styles/tableStyles/"+style.getId();
    }
}
