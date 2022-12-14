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
@Table(name = "SALESM")
public class Salesm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SALCODE", nullable = false)
    private String salcode;

    @Column(name = "OUTCODE")
    private String outcode;

    @Column(name = "ITEMCODE")
    private String itemcode;

    @Column(name = "SALTYPE")
    private String saltype;

    @Column(name = "SALDATE")
    private Date saldate;

    @Column(name = "CUSTCODE")
    private String custcode;

    @Column(name = "QTY")
    private BigDecimal qty;

    @Column(name = "DAN")
    private BigDecimal dan;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "VAT")
    private BigDecimal vat;

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
