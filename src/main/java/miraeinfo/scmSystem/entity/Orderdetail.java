package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "ORDERDETAIL")
@IdClass(OrderdetailPK.class)
public class Orderdetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ORDERNO", nullable = false)
    private String orderno;

    @Id
    @Column(name = "ORDSEQ", nullable = false)
    private Integer ordseq;

    @Column(name = "ITEMCODE")
    private String itemcode;

    @Column(name = "DELDATE")
    private Date deldate;

    @Column(name = "SHIPDATE")
    private Date shipdate;

    @Column(name = "ORDQTY")
    private BigDecimal ordqty;

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
