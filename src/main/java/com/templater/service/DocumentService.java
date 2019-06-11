package com.templater.service;

import com.templater.domain.*;
import com.templater.repositories.DocumentRepository;
import com.templater.repositories.PlaceholderRepository;
import com.templater.repositories.TemplateRepository;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private TemplateRepository templateRepository;
    @Autowired
    private PlaceholderRepository placeholderRepository;
    @Autowired
    private TemplateService templateService;


    public Document createDocument(User user, Long templateId){
        Optional<Template> templateOptional = templateRepository.findById(templateId);
        Template template;
        if(templateOptional.isPresent()){
            template = templateOptional.get();
            Document document = new Document();
            document.setUser(user);
            document.setTemplate(template);
            return documentRepository.save(document);
        }else
            return null;
    }

    public Document addAllPlaceholders(Document document){
        Set<Placeholder> placeholders = document.getTemplate().getPlaceholders();
        boolean contain;
        for (Placeholder placeholder:placeholders) {
            contain = false;
            for (Placeholder documentPlaceholder: document.getPlaceholders()) {
                if(documentPlaceholder.getName().equals(placeholder.getName())){
                    contain=true;
                    break;
                }
            }
            if(!contain){
                Placeholder documentPlaceholder = new Placeholder();
                documentPlaceholder.setContentXml(placeholder.getContentXml());
                documentPlaceholder.setName(placeholder.getName());
                documentPlaceholder.setType(placeholder.getType());
                documentPlaceholder.setFilled(placeholder.getFilled());
                documentPlaceholder.setDocument(document);
                document.getPlaceholders().add(placeholderRepository.save(documentPlaceholder));
            }

        }
        // здесь мб ошибка тк документу добавляю плейсхолдеры в коде а не беру с бд
        return document;
    }

    public WordprocessingMLPackage convertToDocx(Document document){
        try{
//            String templateHtml = templateService.getTemplateXml(document.getTemplate());
//            for (Placeholder placeholder: document.getPlaceholders()) {
//                String placeholderString = "{{type:"+ placeholder.getType() + ", name:"+ placeholder.getName()+"}}";
//                int i = templateHtml.indexOf(placeholderString);
//                templateHtml = templateHtml.replace(placeholderString, placeholder.getContentXml());
//            }
//            templateHtml = templateService.cleanHtml(templateHtml);
            Map<Long, Part> parts = templateService.getAllParts(document.getTemplate());
            for (Part part:parts.values()) {
                for (Placeholder placeholder: document.getPlaceholders()) {
                    String placeholderString = "{{type:"+ placeholder.getType() + ", name:"+ placeholder.getName()+"}}";
                    int i = part.getContentXml().indexOf(placeholderString);
                    part.setContentXml(templateService.cleanHtml(part.getContentXml().replace(placeholderString, placeholder.getContentXml())));
                }
            }
            return templateService.convertToDocx(parts,document.getTemplate());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
