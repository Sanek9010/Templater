package com.templater.service;

import com.google.gson.Gson;
import com.templater.domain.*;
import com.templater.repositories.*;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;


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
    @Autowired
    private ParagraphStyleRepository paragraphStyleRepository;


    public Template save(Template template){
        return templateRepo.save(template);
    }

    public Map<Long,Part> getAllParts(Template template){
        List<Paragraph> paragraphs = paragraphRepository.findByTemplate(template);
        List<Picture> pictures = pictureRepository.findByTemplate(template);
        List<DocTable> tables = tableRepository.findByTemplate(template);
        Map<Long,Part> contentList = new HashMap<>();
        for (Paragraph p: paragraphs) {
            contentList.put(p.getNumberInTemplate(),p);
        }
        for (Picture picture: pictures) {
            contentList.put(picture.getNumberInTemplate(),picture);
        }
        for (DocTable table: tables) {
            contentList.put(table.getNumberInTemplate(),table);
        }
        return contentList;
    }

    public String getTemplateXml(Template template){
        Map<Long,Part> contentList = getAllParts(template);
        StringBuilder stringBuilder = new StringBuilder();
        contentList.forEach((aLong, s) -> stringBuilder.append(s.getContentXml()));
        String st = stringBuilder.toString();//.replace("&nbsp;","");
        st  ="<div>"+st+"</div>";
        return st;
    }

    //убираем именованные сущности из html
    public String getEscapedHtml(String htmlWithoutEntities) {
        //todo как то справиться с форматированием которое делает jTidy(вроде сделал но нужны тесты)
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
        htmlWithoutEntities = htmlWithoutEntities.trim().replaceAll(" +", " ");
        return htmlWithoutEntities;
    }

    public String cleanHtml(String htmlWithoutEntities){
        Document document = Jsoup.parse(htmlWithoutEntities);
        document.select("button").remove();
        htmlWithoutEntities = document.outerHtml();
        htmlWithoutEntities = htmlWithoutEntities.replace("<br>","\n");
//        Whitelist whitelist = Whitelist.relaxed();
//        htmlWithoutEntities = Jsoup.clean(htmlWithoutEntities,whitelist);
        return htmlWithoutEntities;
    }

    public List<Object> getPartsInXml(Map<Long,Part> parts, XHTMLImporterImpl XHTMLImporter) throws Docx4JException {
        String baseURL = System.getProperty("user.dir");
        List<Object> result= new ArrayList<>();
        List<Object> old = new ArrayList<>();
        ObjectFactory factory = Context.getWmlObjectFactory();
        for (Map.Entry<Long,Part> part: parts.entrySet()) {
            String escapedPart = getEscapedHtml(part.getValue().getContentXml());
            List<Object> objects1 =  XHTMLImporter.convert(escapedPart, baseURL);
            List<Object> objects = new ArrayList<>();
            for (int i = (objects1.size() - old.size()); i > 0; i--) {
                objects.add(objects1.get(objects1.size()-i));
            }
            for (Object o:objects) {
                if(o instanceof P){
                    P p = (P)o;
                    PPr pPr = factory.createPPr();
                    PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
                    pStyle.setVal(((Paragraph) part.getValue()).getParagraphStyle().getName());
                    pPr.setPStyle(pStyle);
                    pPr.setRPr(factory.createParaRPr());
                    p.setPPr(pPr);
                    for (Object pElement:p.getContent()) {
                        if(pElement instanceof R){
                            R r = (R)pElement;
                            //убираем fonts которые пришли с html
                            r.getRPr().setRFonts(factory.createRFonts());
                        }
                    }
                    result.add(p);
                }else if(o instanceof Tbl){
                    result.add(o);
                }
            }
            old.clear();
            old.addAll(objects1);
        }
        return result;
    }

    public void addStyles(WordprocessingMLPackage wordMLPackage,Template template){
        Map<Long,Part> parts = getAllParts(template);
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        StyleDefinitionsPart stylesPart = mainDocumentPart.getStyleDefinitionsPart();

        parts.forEach((aLong, part) -> {
            if(part instanceof Paragraph){
                Style style = ((Paragraph) part).getParagraphStyle().createParagraphStyle();
                stylesPart.getJaxbElement().getStyle().add(style);
            }
        });

    }

    public WordprocessingMLPackage convertToDocx(Template template){
        Map<Long,Part> parts = getAllParts(template);
        //convert to docx4j:
        // возможно это все можно заменить на AltChunkXHTMLRoundTrip из docx4j
        try {
            //String baseURL = System.getProperty("user.dir");
            //RFonts rfonts = Context.getWmlObjectFactory().createRFonts();
            //rfonts.setAscii("Times New Roman");
            //XHTMLImporterImpl.addFontMapping("Times New Roman", rfonts);
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            addStyles(wordMLPackage,template);
            //NumberingDefinitionsPart ndp = new NumberingDefinitionsPart();
            //wordMLPackage.getMainDocumentPart().addTargetPart(ndp);
            //ndp.unmarshalDefaultNumbering();
            XHTMLImporterImpl XHTMLImporter = new XHTMLImporterImpl(wordMLPackage);
            //XHTMLImporter.setRunFormatting(FormattingOption.CLASS_TO_STYLE_ONLY);
            //XHTMLImporter.setHyperlinkStyle("Hyperlink");
            List<Object> convert = getPartsInXml(parts,XHTMLImporter);
            wordMLPackage.getMainDocumentPart().getContent().addAll(convert);
            //String fileLocation = baseURL + "/OUT_from_XHTML.docx";
            //wordMLPackage.save(new java.io.File(fileLocation) );
            System.out.println(
                    XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
            return wordMLPackage;
        } catch ( Docx4JException e) {
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
            paragraph.setParagraphStyle(paragraphStyleRepository.findById(Long.parseLong(requestContent.getStyleId())).get());
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

    public void deletePart(int partId, Long templateId){
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Template template;
        if(templateOpt.isPresent()){
            template = templateOpt.get();
        }else {
            template = new Template();//todo заменить на нормальную обработку
        }
        Map<Long,Part> parts = getAllParts(template);
        try {
            parts.forEach((aLong, part) -> {
                if(aLong== (long)partId){
                    if(part instanceof Paragraph){
                        paragraphRepository.deleteById(part.getId());
                    }else if(part instanceof DocTable){
                        tableRepository.deleteById(part.getId());
                    }else if(part instanceof Picture){
                        pictureRepository.deleteById(part.getId());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editPart(EditPartPOJO editPartPOJO, Long templateId){
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Template template;
        if(templateOpt.isPresent()){
            template = templateOpt.get();
        }else {
            template = new Template();//todo заменить на нормальную обработку
        }
        Map<Long,Part> parts = getAllParts(template);
        try {
            parts.forEach((aLong, part) -> {
                if(aLong== (long)editPartPOJO.getPartId()){
                    if(part instanceof Paragraph){
                        Paragraph paragraph = (Paragraph)part;
                        paragraph.setContentXml(editPartPOJO.getContent());
                        paragraphRepository.save(paragraph);
                    }else if(part instanceof DocTable){
                        DocTable table = (DocTable) part;
                        table.setContentXml(editPartPOJO.getContent());
                        tableRepository.save(table);
                    }else if(part instanceof Picture){
                        Picture picture = (Picture) part;
                        picture.setContentXml(editPartPOJO.getContent());
                        pictureRepository.save(picture);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
