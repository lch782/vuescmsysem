package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "ITEMINFO")
public class Iteminfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ITEMCODE", nullable = false)
    private String itemcode;

    @Column(name = "ITEMNAME", nullable = false)
    private String itemname;

    @Column(name = "ITEMNAMEENG")
    private String itemnameeng;

    @Column(name = "TYPE1")
    private String TYPE1;

    @Column(name = "TYPE2")
    private String TYPE2;

    @Column(name = "TYPE3", nullable = false)
    private String TYPE3;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "SPEC")
    private String spec;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "PACKUNIT")
    private String packunit;

    @Column(name = "MINQTY")
    private BigDecimal minqty;

    @Column(name = "SAFQTY")
    private BigDecimal safqty;

    @Column(name = "SPECQTY")
    private BigDecimal specqty;

    @Column(name = "QTY")
    private BigDecimal qty;

    @Column(name = "BUYLIMIT")
    private BigDecimal buylimit;

    @Column(name = "BOXDAN")
    private BigDecimal boxdan;

    @Column(name = "DAN")
    private BigDecimal dan;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "STORAGE")
    private String storage;

    @Column(name = "DIV1")
    private String DIV1;

    @Column(name = "DIV2")
    private String DIV2;

    @Column(name = "DIV3")
    private String DIV3;

    @Column(name = "DIV4")
    private String DIV4;

    @Column(name = "DIV5")
    private String DIV5;

    @Column(name = "DIV6")
    private String DIV6;

    @Column(name = "DIV7")
    private String DIV7;

    @Column(name = "DIV8")
    private String DIV8;

    @Column(name = "DIV9")
    private String DIV9;

    @Column(name = "IMGPATCH")
    private String imgpatch;

    @Column(name = "IMGNAME")
    private String imgname;

    @Column(name = "IMGEXT")
    private String imgext;

    @Column(name = "VATYN")
    private String vatyn;

    @Column(name = "PRDYN")
    private String prdyn;

    @Column(name = "NONUSE")
    private String nonuse;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "REGUSER")
    private String reguser;

    @Column(name = "REGDATE")
    private Date regdate;

    @Column(name = "REGIP")
    private String regip;

    @Column(name = "MODUSER")
    private String moduser;

    @Column(name = "MODDATE")
    private Date moddate;

    @Column(name = "MODIP")
    private String modip;

}
