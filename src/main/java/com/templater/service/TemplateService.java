package com.templater.service;

import com.google.gson.Gson;
import com.templater.domain.*;
import com.templater.repositories.*;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.RFonts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
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
    @Autowired
    private PlaceholderRepository placeholderRepository;


    public Template save(Template template){
        return templateRepo.save(template);
    }

    private Map<Long,String> getAllParts(Template template){
        List<Paragraph> paragraphs = paragraphRepository.findByTemplate(template);
        List<Picture> pictures = pictureRepository.findByTemplate(template);
        List<DocTable> tables = tableRepository.findByTemplate(template);
        Map<Long,String> contentList = new HashMap<>();
        for (Paragraph p: paragraphs) {
            contentList.put(p.getNumberInTemplate(),p.getContentXml());
        }
        for (Picture picture: pictures) {
            contentList.put(picture.getNumberInTemplate(),picture.getLink());
        }
        for (DocTable table: tables) {
            contentList.put(table.getNumberInTemplate(),table.getContentXml());
        }
        return contentList;
    }

    public String getTemplateXml(Template template){
        Map<Long,String> contentList = getAllParts(template);
        StringBuilder stringBuilder = new StringBuilder();
        contentList.forEach((aLong, s) -> stringBuilder.append(s));
        String st = stringBuilder.toString();//.replace("&nbsp;","");
        st  ="<div>"+st+"</div>";
        return st;
    }

    //убираем именованные сущности из html
    public String getEscapedHtml(String htmlWithoutEntities) {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setPrintBodyOnly(true); // only print the content
        tidy.setXmlOut(true); // to XML
        tidy.setSmartIndent(false);
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlWithoutEntities.getBytes("UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            tidy.parseDOM(inputStream, outputStream);
            htmlWithoutEntities = outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        htmlWithoutEntities = htmlWithoutEntities.replace("\r\n","");
        return htmlWithoutEntities;
    }

    public String cleanHtml(String htmlWithoutEntities){
        Document document = Jsoup.parse(htmlWithoutEntities);
        document.select("button").remove();
        htmlWithoutEntities = document.outerHtml();
        Whitelist whitelist = Whitelist.relaxed();
        htmlWithoutEntities = Jsoup.clean(htmlWithoutEntities,whitelist);
        return htmlWithoutEntities;
    }

    public String convertToDocx(String escapedString){
        escapedString = getEscapedHtml(escapedString);
        //convert to docx4j:
        // возможно это все можно заменить на AltChunkXHTMLRoundTrip из docx4j
        try {
            String baseURL = System.getProperty("user.dir");
            RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
            rfonts.setAscii("Times New Roman");
            XHTMLImporterImpl.addFontMapping("Times New Roman", rfonts);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
            ndp.unmarshalDefaultNumbering();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
            XHTMLImporter.setHyperlinkStyle("Hyperlink");
            wordMLPackage.getMainDocumentPart().getContent().addAll(
                    XHTMLImporter.convert(escapedString, baseURL) );
            String fileLocation = baseURL + "/OUT_from_XHTML.docx";
            wordMLPackage.save(new java.io.File(fileLocation) );
            System.out.println(
                    XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
            return fileLocation;
        } catch (JAXBException | Docx4JException e) {
            e.printStackTrace();
        }
        return null;//todo заменить на что нибудь нормальное
    }

    public Object savePart(RequestContent requestContent, Long templateId) {
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Template template;
        if(templateOpt.isPresent()){
            template = templateOpt.get();
        }else {
            template = new Template();//todo заменить на нормальную обработку
        }

        if(requestContent.getEditorType().equals("Paragraph")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            Paragraph paragraph = new Paragraph();
            paragraph.setContentXml(requestContent.getContent());
            paragraph.setNumberInTemplate(template.getNumberOfParts());
            paragraph.setTemplate(template);
            //todo добавить стиль параграфа
            return paragraphRepository.save(paragraph);

        } else if(requestContent.getEditorType().equals("Table")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            DocTable table = new DocTable();
            table.setContentXml(requestContent.getContent());
            table.setNumberInTemplate(template.getNumberOfParts());
            table.setTemplate(template);
            //todo добавить стиль таблицы
            return tableRepository.save(table);
        } else if(requestContent.getEditorType().equals("Picture")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            Picture picture = new Picture();
            picture.setNumberInTemplate(template.getNumberOfParts());
            picture.setTemplate(template);
            //todo добавить ссылку
            return pictureRepository.save(picture);
        } else if(requestContent.getEditorType().equals("ListSdt")){
            Placeholder placeholder = new Placeholder();
            placeholder.setTemplate(template);
            placeholder.setType(requestContent.getEditorType());
            placeholder.setName(requestContent.getContent());
            placeholder.setFilled(false);
            return placeholderRepository.save(placeholder);
        } else if(requestContent.getEditorType().equals("SimpleSdt")){
            Placeholder placeholder = new Placeholder();
            placeholder.setTemplate(template);
            placeholder.setType(requestContent.getEditorType());
            placeholder.setName(requestContent.getContent());
            placeholder.setFilled(false);
            return placeholderRepository.save(placeholder);//потом можно это объеденить с ListSdt
        }else{
            Placeholder placeholder = new Placeholder();
            placeholder.setTemplate(template);
            placeholder.setType(requestContent.getEditorType());
            Gson gson = new Gson();
            TableSdtHeader tableSdtHeader = gson.fromJson(requestContent.getContent(),TableSdtHeader.class);
            placeholder.setName(tableSdtHeader.getName());
            String st = gson.toJson(tableSdtHeader.getHeaders());
            placeholder.setContentXml(st);
            placeholder.setFilled(false);
            return placeholderRepository.save(placeholder);
        }
    }
}
