package com.templater.domain;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

import javax.persistence.*;
import javax.persistence.Id;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TableStyle implements StyleInterface {

    private Long id;
    private String name;
    private int sz;
    private String val;
    private Set<Paragraph> paragraphs = new HashSet<>();
    private ParagraphStyle paragraphStyle;

    @OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "tableStyle")
    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Set<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Transient
    @Override
    public String getType() {
        return "Таблица";
    }

    @ManyToOne
    public ParagraphStyle getParagraphStyle() {
        return paragraphStyle;
    }

    public void setParagraphStyle(ParagraphStyle paragraphStyle) {
        this.paragraphStyle = paragraphStyle;
    }

    public int getSz() {
        return sz;
    }

    public void setSz(int sz) {
        this.sz = sz;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Transient
    public Style createTableStyle(){
        ObjectFactory factory = Context.getWmlObjectFactory();
        Style style = factory.createStyle();
        style.setType("table");
        style.setStyleId(name);
        Style.Name styleName = factory.createStyleName();
        styleName.setVal(name);
        style.setName(styleName);
        BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
        style.setQFormat(booleanDefaultTrue);

        PPr pPr = paragraphStyle.getPPr();

        TblPr tblPr = factory.createTblPr();
        TblBorders tblBorders = factory.createTblBorders();
        CTBorder ctBorder = factory.createCTBorder();
        if(val.equals("single"))
            ctBorder.setVal(STBorder.SINGLE);
        else
            ctBorder.setVal(STBorder.DOUBLE);
        ctBorder.setSz(BigInteger.valueOf(sz));
        tblBorders.setInsideH(ctBorder);
        tblBorders.setInsideV(ctBorder);
        tblBorders.setLeft(ctBorder);
        tblBorders.setRight(ctBorder);
        tblBorders.setBottom(ctBorder);
        tblBorders.setTop(ctBorder);
        tblPr.setTblBorders(tblBorders);

        style.setPPr(pPr);
        style.setTblPr(tblPr);

        return style;
    }
}
