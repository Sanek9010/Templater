package com.templater.web;

import com.templater.domain.Document;
import com.templater.domain.Placeholder;
import com.templater.domain.Template;
import com.templater.domain.User;
import com.templater.repositories.DocumentRepository;
import com.templater.repositories.TemplateRepository;
import com.templater.service.DocumentService;
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
        return "documents";
    }

    @RequestMapping(value = "/documents", method = RequestMethod.POST)
    public String createDocument(){
        return "redirect:/templates";
    }

    @RequestMapping(value = "/documents/create/{templateId}", method = RequestMethod.POST)
    public String createDocument(@AuthenticationPrincipal User user, @PathVariable Long templateId){
        Document document = documentService.createDocument(user,templateId);

        return "redirect:/documents/"+document.getId();
    }


    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.GET)
    public String documentView(@PathVariable Long documentId, ModelMap model, HttpServletResponse response) throws IOException {
        Optional<Document> documentOptional = documentRepository.findById(documentId);
        if(documentOptional.isPresent()){
            Document document = documentService.addAllPlaceholders(documentOptional.get());
            model.put("document", document);
            return "document";
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Document with id "+documentId+" not found");
            return "document";
        }
    }

    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.POST)
    public String updateDocumentHeader(@PathVariable Long documentId, @ModelAttribute Document document){
        Document savedTemplate = documentRepository.save(document);
        //todo реализовать редактирование шаблонов, и документов, В ДОКУМЕНТЫ ДОБАВИТЬ ВОЗМОЖНОСТЬ СКАЧАТЬ DOCX
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
        File file = new File(documentService.convertToDocx(actualDocument));
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