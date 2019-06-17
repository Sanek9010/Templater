package com.templater.service;

import com.google.gson.Gson;
import com.templater.domain.*;
import com.templater.repositories.*;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.FormattingOption;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    @Autowired
    private TableStyleRepository tableStyleRepository;


    private XHTMLImporterImpl xhtmlImporter;

    @PersistenceContext
    private EntityManager em;


    public Template save(Template template){
        return templateRepo.save(template);
    }

//    public Template saveAddedTemplate(Template template, User user){
//        Template saved = new Template();
//        saved.setUser(user);
//        saved.setPrivateTemplate(template.getPrivateTemplate());
//        saved.setDateOfCreation(template.getDateOfCreation());
//        saved.setName(template.getName());
//        saved.setNumberOfParts(template.getNumberOfParts());
//        saved.setParagraphs(new HashSet<>());
//        saved.setDocTables(new HashSet<>());
//        saved = save(saved);
//        for (Placeholder placeholder:template.getPlaceholders()) {
//            Placeholder newP = new Placeholder();
//            newP.setTemplate(saved);
//            newP.setPictureBytes(placeholder.getPictureBytes());
//            newP.setPictureFile(placeholder.getPictureFile());
//            newP.setFilled(placeholder.getFilled());
//            newP.setType(placeholder.getType());
//            newP.setName(placeholder.getName());
//            newP.setContentXml(placeholder.getContentXml());
//            saved.getPlaceholders().add(newP);
//        }
//        for (Picture picture:template.getPictures()) {
//            Picture picture1 = new Picture();
//            picture1.setTemplate(saved);
//            picture1.setPictureFile(picture.getPictureFile());
//            picture1.setContentXml(picture.getContentXml());
//            picture1.setNumberInTemplate(picture.getNumberInTemplate());
//            saved.getPictures().add(picture1);
//        }
//        for (DocTable table:template.getDocTables()) {
//            DocTable table1 = new DocTable();
//            table1.setTemplate(saved);
//            table1.setContentXml(table.getContentXml());
//            table1.setNumberInTemplate(table.getNumberInTemplate());
//            table1.setTableStyle(table.getTableStyle());
//            saved.getDocTables().add(table1);
//        }
//        for (Paragraph paragraph:template.getParagraphs()) {
//            Paragraph paragraph1 = new Paragraph();
//            paragraph1.setTemplate(saved);
//            paragraph1.setNumberInTemplate(paragraph.getNumberInTemplate());
//            paragraph1.setParagraphStyle(paragraph.getParagraphStyle());
//            paragraph1.setContentXml(paragraph.getContentXml());
//            saved.getParagraphs().add(paragraph1);
//        }
//        save(saved);
//        return saved;
//    }

    public Map<Long,Part> getAllParts(Template template){
        List<Paragraph> paragraphs = paragraphRepository.findByTemplate(template);
        List<Picture> pictures = pictureRepository.findByTemplate(template);
        List<DocTable> tables = tableRepository.findByTemplate(template);
        Map<Long,Part> contentList = new TreeMap<>();
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
        htmlWithoutEntities = htmlWithoutEntities.replaceAll("\r\n"," ");
        htmlWithoutEntities = htmlWithoutEntities.trim().replaceAll(" +", " ");
        return htmlWithoutEntities;
    }

    public String cleanHtml(String htmlWithoutEntities){
        Document document = Jsoup.parse(htmlWithoutEntities);
        document.select("button").remove();
        htmlWithoutEntities = document.outerHtml();
        htmlWithoutEntities = htmlWithoutEntities.replace("<br>","\n");
        return htmlWithoutEntities;
    }

    public org.docx4j.wml.P newImage( WordprocessingMLPackage wordMLPackage,
                                             byte[] bytes,
                                             String filenameHint, String altText,
                                             int id1, int id2) throws Exception {

        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);

        Inline inline = imagePart.createImageInline( filenameHint, altText,
                id1, id2, false);

        // Now add the inline in w:p/w:r/w:drawing
        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
        org.docx4j.wml.P  p = factory.createP();
        org.docx4j.wml.R  run = factory.createR();
        p.getContent().add(run);
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);

        return p;

    }

    public List<Object> getPartsInXml(Map<Long,Part> parts, XHTMLImporterImpl XHTMLImporter, WordprocessingMLPackage wordMLPackage) throws Docx4JException {
        String baseURL = System.getProperty("user.dir");
        List<Object> result= new ArrayList<>();
        List<Object> old = new ArrayList<>();
        ObjectFactory factory = Context.getWmlObjectFactory();
        for (Map.Entry<Long,Part> entry: parts.entrySet()) {
            if(entry.getValue() instanceof Picture){
                String filenameHint = null;
                String altText = (entry.getValue()).getContentXml();
                int id1 = 0;
                int id2 = 1;
                // Image 1: no width specified
                try {
                    result.add(newImage( wordMLPackage, ((Picture)(entry.getValue())).getPictureFile(),
                            filenameHint, altText,
                            id1, id2 ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String escapedPart = "<div>"+getEscapedHtml(entry.getValue().getContentXml())+"</div>";
                List<Object> objects1 =  XHTMLImporter.convert(escapedPart, baseURL);
                List<Object> objects = new ArrayList<>();
                for (int i = (objects1.size() - old.size()); i > 0; i--) {
                    objects.add(objects1.get(objects1.size()-i));
                }
                for (Object o:objects) {
                    if(o instanceof P){
                        P p = (P)o;
                        PPr pPr = p.getPPr();
                        if(pPr.getJc().getVal()==JcEnumeration.LEFT){
                            pPr.setJc(null);
                        }
                        pPr.setSpacing(null);
                        pPr.setInd(null);
                        if(entry.getValue() instanceof Paragraph) {
                            PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
                            pStyle.setVal(((Paragraph) entry.getValue()).getParagraphStyle().getName());
                            pPr.setPStyle(pStyle);
                        }
                        pPr.setRPr(factory.createParaRPr());
                        p.setPPr(pPr);
                        for (Object pElement:p.getContent()) {
                            if(pElement instanceof R){
                                R r = (R)pElement;
                                try {
                                    RPr rPr = r.getRPr();
                                    RPr newRPr = factory.createRPr();
                                    newRPr.setI(rPr.getI());
                                    newRPr.setB(rPr.getB());
                                    newRPr.setU(rPr.getU());
                                    newRPr.setPosition(rPr.getPosition());
                                    r.setRPr(newRPr);
                                }catch (Exception e){
                                    //e.printStackTrace();
                                }
                                //убираем fonts которые пришли с html
                            }
                        }
                        result.add(p);
                    }else if(o instanceof Tbl){
                        Tbl tbl = (Tbl)o;
                        TblPr tblPr = tbl.getTblPr();
                        tblPr.setTblBorders(null);
                        if(entry.getValue() instanceof DocTable) {
                            CTTblPrBase.TblStyle tblStyle = factory.createCTTblPrBaseTblStyle();
                            tblStyle.setVal(((DocTable)entry.getValue()).getTableStyle().getName());
                            tblPr.setTblStyle(tblStyle);
                        }
                        tbl.setTblPr(tblPr);

                        for (Object tblElement:tbl.getContent()) {
                            if(tblElement instanceof Tr){
                                Tr tr = (Tr)tblElement;
                                for (Object trElement: tr.getContent()) {
                                    if(trElement instanceof Tc){
                                        Tc tc = (Tc)trElement;
                                        for (Object tcElement: tc.getContent()){
                                            if(tcElement instanceof P){
                                                P p = (P)tcElement;
                                                PPr pPr = p.getPPr();
                                                if(pPr.getJc().getVal()==JcEnumeration.LEFT){
                                                    pPr.setJc(null);
                                                }
                                                pPr.setSpacing(null);
                                                pPr.setInd(null);
                                                pPr.setRPr(factory.createParaRPr());
                                                p.setPPr(pPr);
                                                for (Object pElement:p.getContent()) {
                                                    if(pElement instanceof R){
                                                        R r = (R)pElement;
                                                        try {
                                                            RPr rPr = r.getRPr();
                                                            RPr newRPr = factory.createRPr();
                                                            newRPr.setI(rPr.getI());
                                                            newRPr.setB(rPr.getB());
                                                            newRPr.setU(rPr.getU());
                                                            newRPr.setPosition(rPr.getPosition());
                                                            r.setRPr(newRPr);
                                                        }catch (Exception e){}
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //убираем fonts которые пришли с html
                            }
                        }

                        result.add(o);
                    }
                }
                old.clear();
                old.addAll(objects1);
            }
        }
        return result;

    }

    public void addStyles(WordprocessingMLPackage wordMLPackage,Template template){
        Map<Long,Part> parts = getAllParts(template);
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        StyleDefinitionsPart stylesPart = mainDocumentPart.getStyleDefinitionsPart();
        List<ParagraphStyle> paragraphStyles = new ArrayList<>();
        parts.forEach((aLong, part) -> {
            if(part instanceof Paragraph){
                if(!paragraphStyles.contains(((Paragraph) part).getParagraphStyle())) {
                    Style style = ((Paragraph) part).getParagraphStyle().createParagraphStyle();
                    stylesPart.getJaxbElement().getStyle().add(style);
                    paragraphStyles.add(((Paragraph) part).getParagraphStyle());
                }
            }
        });

    }

    public WordprocessingMLPackage convertToDocx(Map<Long,Part> parts, Template template){
//        Map<Long,Part> parts = getAllParts(template);
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
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            //XHTMLImporter.setRunFormatting(FormattingOption.CLASS_TO_STYLE_ONLY);
            //XHTMLImporter.setHyperlinkStyle("Hyperlink");
            List<Object> convert = getPartsInXml(parts,xhtmlImporter,wordMLPackage);
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
            throw new NullPointerException("Template not found");
        }
        if((requestContent.getEditorType().equals("Paragraph")||
                requestContent.getEditorType().equals("Table")||
                requestContent.getEditorType().equals("Picture")||
                requestContent.getEditorType().equals("PictureSdt"))&&
                (requestContent.getNumberOfPart()< template.getNumberOfParts())){
            Map<Long, Part> allParts = getAllParts(template);
            allParts.forEach((aLong, part) -> {
                if(part.getNumberInTemplate()>=requestContent.getNumberOfPart()){
                    if(part instanceof Paragraph){
                        Paragraph paragraph = (Paragraph)part;
                        paragraph.setNumberInTemplate(paragraph.getNumberInTemplate()+1);
                        paragraphRepository.save(paragraph);
                    } else if(part instanceof DocTable){
                        DocTable table = (DocTable)part;
                        table.setNumberInTemplate(table.getNumberInTemplate()+1);
                        tableRepository.save(table);
                    } else if(part instanceof Picture){
                        Picture picture = (Picture)part;
                        picture.setNumberInTemplate(picture.getNumberInTemplate()+1);
                        pictureRepository.save(picture);
                    }
                }
            });
        }
        if(requestContent.getEditorType().equals("Paragraph")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            Paragraph paragraph = new Paragraph();
            paragraph.setContentXml(requestContent.getContent());
            paragraph.setNumberInTemplate( requestContent.getNumberOfPart());
            paragraph.setTemplate(template);
            paragraph.setParagraphStyle(paragraphStyleRepository.findById(Long.parseLong(requestContent.getStyleId())).get());
            return paragraphRepository.save(paragraph);
        } else if(requestContent.getEditorType().equals("Table")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            DocTable table = new DocTable();
            table.setContentXml(requestContent.getContent());
            table.setNumberInTemplate(requestContent.getNumberOfPart());
            table.setTemplate(template);
            table.setTableStyle(tableStyleRepository.findById(Long.parseLong(requestContent.getStyleId())).get());
            return tableRepository.save(table);
        } else if(requestContent.getEditorType().equals("Picture")){
            template.setNumberOfParts(template.getNumberOfParts()+1);
            Picture picture = new Picture();
            picture.setNumberInTemplate(requestContent.getNumberOfPart());
            picture.setTemplate(template);
            picture.setPictureFile(requestContent.getPicture());
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
            return placeholderRepository.save(placeholder);
        } else if(requestContent.getEditorType().equals("RichSdt")){
            Placeholder placeholder = new Placeholder();
            placeholder.setTemplate(template);
            placeholder.setType(requestContent.getEditorType());
            placeholder.setName(requestContent.getContent());
            placeholder.setFilled(false);
            return placeholderRepository.save(placeholder);
        } else if(requestContent.getEditorType().equals("PictureSdt")){
            Placeholder placeholder = new Placeholder();
            placeholder.setTemplate(template);
            placeholder.setType(requestContent.getEditorType());
            placeholder.setName(requestContent.getContent());
            placeholder.setFilled(false);
            template.setNumberOfParts(template.getNumberOfParts()+1);
            Picture picture = new Picture();
            picture.setNumberInTemplate(requestContent.getNumberOfPart());
            picture.setTemplate(template);
            picture.setContentXml(requestContent.getContent());
            pictureRepository.save(picture);
            return placeholderRepository.save(placeholder);
        } else{
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
