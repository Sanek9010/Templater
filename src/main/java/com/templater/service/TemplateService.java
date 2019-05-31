package com.templater.service;

import com.templater.domain.DocTable;
import com.templater.domain.Paragraph;
import com.templater.domain.Picture;
import com.templater.domain.Template;
import com.templater.repositories.ParagraphRepository;
import com.templater.repositories.PictureRepository;
import com.templater.repositories.TableRepository;
import com.templater.repositories.TemplateRepository;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;


import static java.lang.Math.toIntExact;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepo;

    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private TableRepository tableRepository;

    public Template save(Template template){
        return templateRepo.save(template);
    }

    private Map<Long,String> getAllParts(Template template){
        List<Paragraph> paragraphs = paragraphRepository.findByTemplate(template);
        List<Picture> pictures = pictureRepository.findByTemplate(template);
        List<DocTable> tables = tableRepository.findByTemplate(template);
        Map<Long,String> contentList = new HashMap<>();
        for (Paragraph p: paragraphs) {
            contentList.put(p.getId(),p.getContentXml());
        }
        for (Picture picture: pictures) {
            contentList.put(picture.getId(),picture.getLink());
        }
        for (DocTable table: tables) {
            contentList.put(table.getId(),table.getContentXml());
        }
        return contentList;
    }

    public String getTemplateXml(Template template){
        Map<Long,String> contentList = getAllParts(template);
        StringBuilder stringBuilder = new StringBuilder();
        contentList.forEach((aLong, s) -> stringBuilder.append(s));
        String st = stringBuilder.toString().replace("&nbsp;","");
        return "<div>"+st+"</div>";
    }

    public String convertToDocx(Template template){
        //convert to docx4j:
        // возможно это все можно заменить на AltChunkXHTMLRoundTrip из docx4j
        try {
            String baseURL = System.getProperty("user.dir");
            RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
            rfonts.setAscii("Century Gothic");
            XHTMLImporterImpl.addFontMapping("Century Gothic", rfonts);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
            ndp.unmarshalDefaultNumbering();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);

            XHTMLImporter.setHyperlinkStyle("Hyperlink");
            //убираем именованные сущности из html
            String htmlWithoutEntities = getTemplateXml(template);
            Tidy tidy = new Tidy();
            tidy.setInputEncoding("UTF-8");
            tidy.setOutputEncoding("UTF-8");
            tidy.setPrintBodyOnly(true); // only print the content
            tidy.setXmlOut(true); // to XML
            tidy.setSmartIndent(true);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlWithoutEntities.getBytes("UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            tidy.parseDOM(inputStream, outputStream);
            htmlWithoutEntities = outputStream.toString("UTF-8");


            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    XHTMLImporter.convert(htmlWithoutEntities, baseURL) );
            String fileLocation = baseURL + "/OUT_from_XHTML.docx";
            wordMLPackage.save(new java.io.File(fileLocation) );

            System.out.println(
                    XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
            return fileLocation;
        } catch (JAXBException | Docx4JException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;//todo заменить на что нибудь нормальное
    }

    public Object savePart(RequestContent requestContent, Long templateId) {
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Template template;
        if(templateOpt.isPresent()){
            template = templateOpt.get();
            template.setNumberOfParts(template.getNumberOfParts()+1);
        }else {
            template = new Template();//todo заменить на нормальную обработку
        }

        if(requestContent.getEditorType().equals("Paragraph")){
            Paragraph paragraph = new Paragraph();
            paragraph.setContentXml(requestContent.getContent());
            paragraph.setId(template.getNumberOfParts());
            paragraph.setTemplate(template);
            //todo добавить стиль параграфа
            return paragraphRepository.save(paragraph);

        } else if(requestContent.getEditorType().equals("Table")){
            DocTable table = new DocTable();
            table.setContentXml(requestContent.getContent());
            table.setId(template.getNumberOfParts());
            table.setTemplate(template);
            //todo добавить стиль таблицы
            return tableRepository.save(table);
        } else {
            Picture picture = new Picture();
            picture.setId(template.getNumberOfParts());
            picture.setTemplate(template);
            //todo добавить ссылку
            return pictureRepository.save(picture);
        }
        //todo добавить сохранение sdt контента в бд
    }
}
