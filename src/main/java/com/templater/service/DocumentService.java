package com.templater.service;

import com.templater.domain.*;
import com.templater.repositories.DocumentRepository;
import com.templater.repositories.PlaceholderRepository;
import com.templater.repositories.TemplateRepository;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public Document createDocument(Long templateId,User user){
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
        TreeSet<Placeholder> sortedPlacehoders = new TreeSet<>(Comparator.comparingLong(Placeholder::getId));
        sortedPlacehoders.addAll(placeholders);
        boolean contain;
        for (Placeholder placeholder:sortedPlacehoders) {
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
        return document;
    }

    public WordprocessingMLPackage convertToDocx(Document document){
        try{
            Map<Long, Part> parts = templateService.getAllParts(document.getTemplate());
            for (Part part:parts.values()) {
                for (Placeholder placeholder: document.getPlaceholders()) {
                    if(placeholder.getType().equals("PictureSdt")) {
                        if (part instanceof Picture) {
                            if ((part).getContentXml().equals(placeholder.getName())) {
                                ((Picture) part).setPictureFile(placeholder.getPictureBytes());
                            }
                        }else {
                            String placeholderString = "{{type:" + placeholder.getType() + ", name:" + placeholder.getName() + "}}";
                            part.setContentXml(templateService.cleanHtml(part.getContentXml().replace(placeholderString, "")));
                        }
                    } else {
                        if (!(part instanceof Picture)) {
                            if(placeholder.getContentXml()!=null) {
                                String placeholderString = "{{type:" + placeholder.getType() + ", name:" + placeholder.getName() + "}}";
                                int i = part.getContentXml().indexOf(placeholderString);
                                if (i != -1)
                                    part.setContentXml(templateService.cleanHtml(part.getContentXml().replace(placeholderString, placeholder.getContentXml())));
                            }
                        }
                    }
                }
            }
            return templateService.convertToDocx(parts,document.getTemplate());
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
