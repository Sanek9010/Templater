package com.templater.web;

import com.templater.domain.Document;
import com.templater.domain.Placeholder;
import com.templater.domain.Template;
import com.templater.domain.User;
import com.templater.repositories.DocumentRepository;
import com.templater.repositories.TemplateRepository;
import com.templater.security.Authority;
import com.templater.service.DocumentService;
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

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.templater.web.TemplateController.getResourceResponseEntity;

@Controller
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/documents", method = RequestMethod.GET)
    public String documentsView(@AuthenticationPrincipal User user, ModelMap model){
        List<Document> documents = documentRepository.findByUser(user);
        model.put("documents", documents);
        for (Authority authority:user.getAuthorities()) {
            if(authority.getAuthority().equals("ROLE_SUPERUSER")){
                return "documents";
            }
        }
        return "documentsForUsers";
    }

    @RequestMapping(value = "/documents", method = RequestMethod.POST)
    public String createDocument(@AuthenticationPrincipal User user){
//        for (Authority authority:user.getAuthorities()) {
//            if(authority.getAuthority().equals("ROLE_SUPERUSER")){
//                return "redirect:/templates";
//            }
//        }
        return "redirect:/templates";
    }

    @RequestMapping(value = "/documents/create/{templateId}", method = RequestMethod.POST)
    public String createDocument(@AuthenticationPrincipal User user, @PathVariable Long templateId){
        Document document = documentService.createDocument(templateId,user);

        return "redirect:/documents/"+document.getId();
    }


    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.GET)
    public String documentView(@AuthenticationPrincipal User user, @PathVariable Long documentId, ModelMap model, HttpServletResponse response) throws IOException {
        Optional<Document> documentOptional = documentRepository.findById(documentId);
        if(documentOptional.isPresent()){
            Document document = documentService.addAllPlaceholders(documentOptional.get());
            model.put("document", document);
            for (Authority authority:user.getAuthorities()) {
                if(authority.getAuthority().equals("ROLE_SUPERUSER")){
                    return "document";
                }
            }
            return "documentForUsers";
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Document with id "+documentId+" not found");
            return "document";
        }
    }

    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.POST)
    public String updateDocumentHeader(@PathVariable Long documentId, @ModelAttribute Document document){
        for (Placeholder p: document.getPlaceholders()) {
            if(p.getType().equals("PictureSdt")){
                try {
                    p.setPictureBytes(p.getPictureFile().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Document savedTemplate = documentRepository.save(document);

        return "redirect:/documents/"+documentId;
    }

    @RequestMapping(value = "/documents/{documentId}/delete", method = RequestMethod.GET)
    public String deleteTemplate(@PathVariable Long documentId){
        documentRepository.deleteById(documentId);
        return "redirect:/documents";
    }


    @RequestMapping(value = "/documents/{documentId}/getDocx", method = RequestMethod.GET)
    public ResponseEntity<Resource> getDocx(@PathVariable Long documentId) throws IOException {
        Optional<Document> documentOptional = documentRepository.findById(documentId);
        Document actualDocument = new Document();
        if(documentOptional.isPresent()){
            actualDocument=documentOptional.get();
        } else {
            //todo вывести ошибку
        }
        String baseURL = System.getProperty("user.dir");
        String fileLocation = baseURL + "/OUT_from_XHTML.docx";
        File file = new java.io.File(fileLocation);
        try {
            documentService.convertToDocx(actualDocument).save(file);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        return getResourceResponseEntity(file);
    }
}