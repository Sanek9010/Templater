package com.templater.domain;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

import javax.persistence.*;
import javax.persistence.Id;
import java.math.BigInteger;
import java.util.Set;

@Entity
public class ParagraphStyle implements StyleInterface {

    private Long id;
    private String name;
    private String font;
    private Boolean b;
    private Boolean i;
    private Boolean u;
    private Integer textSize;
    private String jc;
    private Double insideSpacing;
    private Double beforeSpacing;
    private Double afterSpacing;
    private Double firstLineIndent;
    private Double leftIndent;
    private Double rightIndent;
    private Set<Paragraph> paragraphs;
    private Set<TableStyle> tableStyles;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "paragraphStyle")
    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Set<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "paragraphStyle")
    public Set<TableStyle> getTableStyles() {
        return tableStyles;
    }

    public void setTableStyles(Set<TableStyle> tableStyles) {
        this.tableStyles = tableStyles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Boolean getB() {
        return b;
    }

    public void setB(Boolean b) {
        this.b = b;
    }

    public Boolean getI() {
        return i;
    }

    public void setI(Boolean i) {
        this.i = i;
    }

    public Boolean getU() {
        return u;
    }

    public void setU(Boolean u) {
        this.u = u;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public String getJc() {
        return jc;
    }

    public void setJc(String jc) {
        this.jc = jc;
    }

    public Double getInsideSpacing() {
        return insideSpacing;
    }

    public void setInsideSpacing(Double insideSpacing) {
        this.insideSpacing = insideSpacing;
    }

    public Double getBeforeSpacing() {
        return beforeSpacing;
    }

    public void setBeforeSpacing(Double beforeSpacing) {
        this.beforeSpacing = beforeSpacing;
    }

    public Double getAfterSpacing() {
        return afterSpacing;
    }

    public void setAfterSpacing(Double afterSpacing) {
        this.afterSpacing = afterSpacing;
    }

    public Double getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(Double firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public Double getLeftIndent() {
        return leftIndent;
    }

    public void setLeftIndent(Double leftIndent) {
        this.leftIndent = leftIndent;
    }

    public Double getRightIndent() {
        return rightIndent;
    }

    public void setRightIndent(Double rightIndent) {
        this.rightIndent = rightIndent;
    }

    public String getName() {
        return name;
    }

    @Transient
    @Override
    public String getType() {
        return "Параграф";
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    public PPr getPPr(){
        ObjectFactory factory = Context.getWmlObjectFactory();
        PPr pPr = factory.createPPr();
        Jc jcObj = factory.createJc();
        switch (jc){
            case "left":
                jcObj.setVal(JcEnumeration.LEFT);
                break;
            case "center":
                jcObj.setVal(JcEnumeration.CENTER);
                break;
            case "right":
                jcObj.setVal(JcEnumeration.RIGHT);
                break;
            case "all":
                jcObj.setVal(JcEnumeration.BOTH);
                break;
            default:
        }
        pPr.setJc(jcObj);

        PPrBase.Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setAfter(BigInteger.valueOf(Math.round(afterSpacing*240)));
        spacing.setBefore(BigInteger.valueOf(Math.round(beforeSpacing*240)));
        spacing.setLine(BigInteger.valueOf(Math.round(insideSpacing*240)));
        pPr.setSpacing(spacing);

        PPrBase.Ind ind = factory.createPPrBaseInd();
        ind.setFirstLine(BigInteger.valueOf(Math.round(firstLineIndent*567)));
        ind.setLeft(BigInteger.valueOf(Math.round(leftIndent*567)));
        ind.setRight(BigInteger.valueOf(Math.round(rightIndent*567)));
        pPr.setInd(ind);



        ParaRPr rPr = factory.createParaRPr();
        BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
//        if(i) rPr.setI(booleanDefaultTrue);
//        if(b) rPr.setB(booleanDefaultTrue);
//        if(u) {
//            U uObj = factory.createU();
//            uObj.setVal(UnderlineEnumeration.SINGLE);
//            rPr.setU(uObj);
//        }

        RFonts rFonts = factory.createRFonts();
        rFonts.setAscii(font);
        rFonts.setCs(font);
        rFonts.setEastAsia(font);
        rFonts.setHAnsi(font);
        rPr.setRFonts(rFonts);

        HpsMeasure hpsMeasure = factory.createHpsMeasure();
        hpsMeasure.setVal(BigInteger.valueOf(textSize*2));
        rPr.setSz(hpsMeasure);

        pPr.setRPr(rPr);
        return pPr;
    }

    public Style createParagraphStyle(){
        ObjectFactory factory = Context.getWmlObjectFactory();
        Style style = factory.createStyle();
        style.setType("paragraph");
        style.setStyleId(name);
        Style.Name styleName = factory.createStyleName();
        styleName.setVal(name);
        style.setName(styleName);
        Style.BasedOn basedOn = factory.createStyleBasedOn();
        basedOn.setVal("Normal");
        style.setBasedOn(basedOn);

        PPr pPr = getPPr();

        style.setPPr(pPr);
        BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
        style.setQFormat(booleanDefaultTrue);

        return style;
    }
}
