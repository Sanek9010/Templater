package com.templater.service;

import com.templater.domain.Document;
import com.templater.domain.Placeholder;
import com.templater.domain.Template;
import com.templater.domain.User;
import com.templater.repositories.DocumentRepository;
import com.templater.repositories.PlaceholderRepository;
import com.templater.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if(document.getPlaceholders()==null){
            Set<Placeholder> placeholders = document.getTemplate().getPlaceholders();
            for (Placeholder placeholder:placeholders) {
                Placeholder documentPlaceholder = new Placeholder();
                documentPlaceholder.setContentXml(placeholder.getContentXml());
                documentPlaceholder.setName(placeholder.getName());
                documentPlaceholder.setType(placeholder.getType());
                documentPlaceholder.setDocument(document);
                placeholderRepository.save(documentPlaceholder);
            }
            return documentRepository.findById(document.getId()).get();
        }else return document;
    }
}
