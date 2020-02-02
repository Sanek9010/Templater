package com.templater.service;

import com.google.gson.Gson;
import com.templater.domain.*;
import com.templater.repositories.*;
import org.docx4j.XmlUtils;
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
    private PlaceholderRepository placeholderRepository;
    @Autowired
    private ParagraphStyleRepository paragraphStyleRepository;
    @Autowired
    private TableStyleRepository tableStyleRepository;
    @Autowired
    private PartGroupRepository partGroupRepository;

    public Template save(Template template) {
        return templateRepo.save(template);
    }

    public Template deepSave(Template template) {
        templateRepo.save(template);
        for (PartGroup partGroup : template.getPartGroups()) {
            partGroupRepository.save(partGroup);
            for (Paragraph p : partGroup.getParagraphs()) {
                paragraphRepository.save(p);
            }
            for (Picture p : partGroup.getPictures()) {
                pictureRepository.save(p);
            }
        }
        for (Placeholder p : template.getPlaceholders()) {
            placeholderRepository.save(p);
        }
        template = templateRepo.save(template);
        return template;
    }

    public Map<Long, Part> getDefaultParts(Template template) {
        List<Paragraph> paragraphs = new ArrayList<>();
        List<Picture> pictures = new ArrayList<>();
        addPartsToLists(template, paragraphs, pictures);
        Map<Long, Part> contentList = new TreeMap<>();
        for (Paragraph p : paragraphs) {
            if (p.getPartGroup().getName().equals("Default"))
                contentList.put(p.getNumberInTemplate(), p);
        }
        for (Picture picture : pictures) {
            if (picture.getPartGroup().getName().equals("Default"))
                contentList.put(picture.getNumberInTemplate(), picture);
        }
        return contentList;
    }

    private void addPartsToLists(Template template, List<Paragraph> paragraphs, List<Picture> pictures){
        for (PartGroup partGroup : template.getPartGroups()) {
            paragraphs.addAll(partGroup.getParagraphs());
            pictures.addAll(partGroup.getPictures());
        }
    }

    public Map<Long, Part> getAllParts(Template template) {
        List<Paragraph> paragraphs = new ArrayList<>();
        List<Picture> pictures = new ArrayList<>();
        addPartsToLists(template, paragraphs, pictures);
        Map<Long, Part> contentList = new TreeMap<>();
        for (Paragraph p : paragraphs) {
            contentList.put(p.getNumberInTemplate(), p);
        }
        for (Picture picture : pictures) {
            contentList.put(picture.getNumberInTemplate(), picture);
        }
        return contentList;
    }

    private String getEscapedHtml(String htmlWithoutEntities) {
        Tidy tidy = setupTidy();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlWithoutEntities.getBytes("UTF-8"));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            tidy.parseDOM(inputStream, outputStream);
            htmlWithoutEntities = outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        htmlWithoutEntities = htmlWithoutEntities.replaceAll("\r\n", " ");
        htmlWithoutEntities = htmlWithoutEntities.trim().replaceAll(" +", " ");
        return htmlWithoutEntities;
    }

    private Tidy setupTidy(){
        Tidy tidy = new Tidy();
        tidy.setInputEncoding("UTF-8");
        tidy.setOutputEncoding("UTF-8");
        tidy.setPrintBodyOnly(true);
        tidy.setXmlOut(true);
        tidy.setSmartIndent(false);
        return tidy;
    }

    String cleanHtml(String htmlWithoutEntities) {
        Document document = Jsoup.parse(htmlWithoutEntities);
        document.select("button").remove();
        htmlWithoutEntities = document.outerHtml();
        htmlWithoutEntities = htmlWithoutEntities.replace("<br>", "\n");
        return htmlWithoutEntities;
    }

    private org.docx4j.wml.P newImage(WordprocessingMLPackage wordMLPackage,
                                      byte[] bytes,
                                      String filenameHint, String altText,
                                      int id1, int id2) throws Exception {
        BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
        Inline inline = imagePart.createImageInline(filenameHint, altText,
                id1, id2, false);
        org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
        org.docx4j.wml.P p = factory.createP();
        org.docx4j.wml.R run = factory.createR();
        p.getContent().add(run);
        org.docx4j.wml.Drawing drawing = factory.createDrawing();
        run.getContent().add(drawing);
        drawing.getAnchorOrInline().add(inline);
        return p;
    }

    private List<Object> getPartsInXml(Map<Long, Part> parts, XHTMLImporterImpl XHTMLImporter, WordprocessingMLPackage wordMLPackage) throws Docx4JException {
        String baseURL = System.getProperty("user.dir");
        List<Object> result = new ArrayList<>();
        List<Object> old = new ArrayList<>();
        ObjectFactory factory = Context.getWmlObjectFactory();
        for (Map.Entry<Long, Part> entry : parts.entrySet()) {
            if (entry.getValue() instanceof Picture) {
                String filenameHint = null;
                String altText = (entry.getValue()).getContentXml();
                int id1 = 0;
                int id2 = 1;
                try {
                    result.add(newImage(wordMLPackage, ((Picture) (entry.getValue())).getPictureBytes(),
                            filenameHint, altText,
                            id1, id2));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String escapedPart = "<div>" + getEscapedHtml(entry.getValue().getContentXml()) + "</div>";
                List<Object> objects1 = XHTMLImporter.convert(escapedPart, baseURL);
                List<Object> objects = new ArrayList<>();
                for (int i = (objects1.size() - old.size()); i > 0; i--) {
                    objects.add(objects1.get(objects1.size() - i));
                }
                for (Object o : objects) {
                    if (o instanceof P) {
                        P p = (P) o;
                        PPr pPr = p.getPPr();
                        if (pPr.getJc().getVal() == JcEnumeration.LEFT) {
                            pPr.setJc(null);
                        }
                        pPr.setSpacing(null);
                        pPr.setInd(null);
                        if (entry.getValue() instanceof Paragraph) {
                            PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
                            pStyle.setVal(((Paragraph) entry.getValue()).getParagraphStyle().getName());
                            pPr.setPStyle(pStyle);
                        }
                        setPProperties(factory, p, pPr);
                        result.add(p);
                    } else if (o instanceof Tbl) {
                        Tbl tbl = (Tbl) o;
                        TblPr tblPr = tbl.getTblPr();
                        tblPr.setTblBorders(null);
                        if (entry.getValue() instanceof Paragraph) {
                            CTTblPrBase.TblStyle tblStyle = factory.createCTTblPrBaseTblStyle();
                            tblStyle.setVal(((Paragraph) entry.getValue()).getTableStyle().getName());
                            tblPr.setTblStyle(tblStyle);
                        }
                        tbl.setTblPr(tblPr);
                        for (Object tblElement : tbl.getContent()) {
                            if (tblElement instanceof Tr) {
                                Tr tr = (Tr) tblElement;
                                for (Object trElement : tr.getContent()) {
                                    if (trElement instanceof Tc) {
                                        Tc tc = (Tc) trElement;
                                        for (Object tcElement : tc.getContent()) {
                                            if (tcElement instanceof P) {
                                                P p = (P) tcElement;
                                                PPr pPr = p.getPPr();
                                                if (pPr.getJc().getVal() == JcEnumeration.LEFT) {
                                                    pPr.setJc(null);
                                                }
                                                pPr.setSpacing(null);
                                                pPr.setInd(null);
                                                setPProperties(factory, p, pPr);
                                            }
                                        }
                                    }
                                }
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

    private void setPProperties(ObjectFactory factory, P p, PPr pPr) {
        pPr.setRPr(factory.createParaRPr());
        p.setPPr(pPr);
        for (Object pElement : p.getContent()) {
            if (pElement instanceof R) {
                R r = (R) pElement;
                try {
                    RPr rPr = r.getRPr();
                    RPr newRPr = factory.createRPr();
                    newRPr.setI(rPr.getI());
                    newRPr.setB(rPr.getB());
                    newRPr.setU(rPr.getU());
                    newRPr.setPosition(rPr.getPosition());
                    r.setRPr(newRPr);
                } catch (Exception e) {
                }
            }
        }
    }

    private void addStyles(WordprocessingMLPackage wordMLPackage, Template template) {
        Map<Long, Part> parts = getDefaultParts(template);
        MainDocumentPart mainDocumentPart = wordMLPackage.getMainDocumentPart();
        StyleDefinitionsPart stylesPart = mainDocumentPart.getStyleDefinitionsPart();
        List<ParagraphStyle> paragraphStyles = new ArrayList<>();
        List<TableStyle> tableStyles = new ArrayList<>();
        parts.forEach((aLong, part) -> {
            if (part instanceof Paragraph) {
                if (!paragraphStyles.contains(((Paragraph) part).getParagraphStyle())) {
                    Style style = ((Paragraph) part).getParagraphStyle().createParagraphStyle();
                    stylesPart.getJaxbElement().getStyle().add(style);
                    paragraphStyles.add(((Paragraph) part).getParagraphStyle());
                }
                if (!tableStyles.contains(((Paragraph) part).getTableStyle())) {
                    Style style = ((Paragraph) part).getTableStyle().createTableStyle();
                    stylesPart.getJaxbElement().getStyle().add(style);
                    tableStyles.add(((Paragraph) part).getTableStyle());
                }
            }
        });

    }

    public WordprocessingMLPackage convertToDocx(Map<Long, Part> parts, Template template) {
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
            addStyles(wordMLPackage, template);
            XHTMLImporterImpl xhtmlImporter = new XHTMLImporterImpl(wordMLPackage);
            List<Object> convert = getPartsInXml(parts, xhtmlImporter, wordMLPackage);
            wordMLPackage.getMainDocumentPart().getContent().addAll(convert);
            System.out.println(
                    XmlUtils.marshaltoString(wordMLPackage.getMainDocumentPart().getJaxbElement(), true, true));
            return wordMLPackage;
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object savePart(RequestContent requestContent, Long templateId) {
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        Template template;
        if (templateOpt.isPresent()) {
            template = templateOpt.get();
        } else {
            throw new NullPointerException("Template not found");
        }
        if ((requestContent.getEditorType().equals("Paragraph") ||
                requestContent.getEditorType().equals("Picture") ||
                requestContent.getEditorType().equals("PictureSdt")) &&
                (requestContent.getNumberOfPart() < template.getNumberOfParts())) {
            Map<Long, Part> allParts = getDefaultParts(template);
            allParts.forEach((aLong, part) -> {
                if (part.getNumberInTemplate() >= requestContent.getNumberOfPart()) {
                    if (part instanceof Paragraph) {
                        Paragraph paragraph = (Paragraph) part;
                        paragraph.setNumberInTemplate(paragraph.getNumberInTemplate() + 1);
                        paragraphRepository.save(paragraph);
                    } else if (part instanceof Picture) {
                        Picture picture = (Picture) part;
                        picture.setNumberInTemplate(picture.getNumberInTemplate() + 1);
                        pictureRepository.save(picture);
                    }
                }
            });
        }
        PartGroup partGroup = null;
        if (requestContent.getPartGroup() != null && !requestContent.getPartGroup().equals("")) {
            partGroup = partGroupRepository.findById(Long.parseLong(requestContent.getPartGroup())).get();
        } else {
            partGroup = partGroupRepository.findByNameAndTemplate("Default", template).get();
        }
        switch (requestContent.getEditorType()) {
            case "Paragraph":
                template.setNumberOfParts(template.getNumberOfParts() + 1);
                Paragraph paragraph = new Paragraph();
                paragraph.setContentXml(requestContent.getContent());
                paragraph.setNumberInTemplate(requestContent.getNumberOfPart());
                paragraph.setPartGroup(partGroup);
                if (!requestContent.getStyleId().equals(""))
                    paragraph.setParagraphStyle(paragraphStyleRepository.findById(Long.parseLong(requestContent.getStyleId())).get());
                else
                    paragraph.setParagraphStyle(paragraphStyleRepository.findAll().get(0));
                if (!requestContent.getTableStyleId().equals(""))
                    paragraph.setTableStyle(tableStyleRepository.findById(Long.parseLong(requestContent.getTableStyleId())).get());
                else
                    paragraph.setTableStyle(tableStyleRepository.findAll().get(0));
                return paragraphRepository.save(paragraph);
            case "Picture": {
                template.setNumberOfParts(template.getNumberOfParts() + 1);
                Picture picture = new Picture();
                picture.setNumberInTemplate(requestContent.getNumberOfPart());
                picture.setPartGroup(partGroup);
                picture.setPictureBytes(requestContent.getPicture());
                return pictureRepository.save(picture);
            }
            case "ListSdt":
            case "SimpleSdt":
            case "RichSdt":
            case "PartGroup": {
                Placeholder placeholder = new Placeholder();
                placeholder.setTemplate(template);
                placeholder.setType(requestContent.getEditorType());
                placeholder.setName(requestContent.getContent());
                placeholder.setFilled(false);
                return placeholderRepository.save(placeholder);
            }
            case "PictureSdt": {
                Placeholder placeholder = new Placeholder();
                placeholder.setTemplate(template);
                placeholder.setType(requestContent.getEditorType());
                placeholder.setName(requestContent.getContent());
                placeholder.setFilled(false);
                template.setNumberOfParts(template.getNumberOfParts() + 1);
                Picture picture = new Picture();
                picture.setNumberInTemplate(requestContent.getNumberOfPart());
                picture.setPartGroup(partGroup);
                picture.setContentXml(requestContent.getContent());
                pictureRepository.save(picture);
                return placeholderRepository.save(placeholder);
            }
            default: {
                Placeholder placeholder = new Placeholder();
                placeholder.setTemplate(template);
                placeholder.setType(requestContent.getEditorType());
                Gson gson = new Gson();
                TableSdtHeader tableSdtHeader = gson.fromJson(requestContent.getContent(), TableSdtHeader.class);
                placeholder.setName(tableSdtHeader.getName());
                String st = gson.toJson(tableSdtHeader.getHeaders());
                placeholder.setContentXml(st);
                placeholder.setFilled(false);
                return placeholderRepository.save(placeholder);
            }
        }
    }

    public void deletePart(int partId, Long templateId) {
        Template template = getTemplateById(templateId);
        Map<Long, Part> parts = getDefaultParts(template);
        try {
            parts.forEach((aLong, part) -> {
                if (aLong == (long) partId) {
                    if (part instanceof Paragraph) {
                        paragraphRepository.deleteById(part.getId());
                    } else if (part instanceof Picture) {
                        pictureRepository.deleteById(part.getId());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Template getTemplateById(Long templateId) {
        Optional<Template> templateOpt = templateRepo.findById(templateId);
        return templateOpt.orElseGet(Template::new);
    }

    public void editPart(EditPartPOJO editPartPOJO, Long templateId) {
        Template template = getTemplateById(templateId);
        Map<Long, Part> parts = getDefaultParts(template);
        try {
            parts.forEach((aLong, part) -> {
                if (aLong == (long) editPartPOJO.getPartId()) {
                    if (part instanceof Paragraph) {
                        Paragraph paragraph = (Paragraph) part;
                        if (!editPartPOJO.getStyleId().equals(""))
                            paragraph.setParagraphStyle(paragraphStyleRepository.findById(Long.parseLong(editPartPOJO.getStyleId())).get());
                        else
                            paragraph.setParagraphStyle(paragraphStyleRepository.findAll().get(0));
                        if (!editPartPOJO.getTableStyleId().equals(""))
                            paragraph.setTableStyle(tableStyleRepository.findById(Long.parseLong(editPartPOJO.getTableStyleId())).get());
                        else
                            paragraph.setTableStyle(tableStyleRepository.findAll().get(0));
                        paragraph.setContentXml(editPartPOJO.getContent());
                        paragraphRepository.save(paragraph);
                    } else if (part instanceof Picture) {
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
