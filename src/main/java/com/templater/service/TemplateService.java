package com.templater.service;

import com.templater.domain.Template;
import com.templater.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepo;

    public Template save(Template template){
        //template.setDateOfCreation((new Date()).toString());
        //template.setName("template1");
        return templateRepo.save(template);
    }
}
