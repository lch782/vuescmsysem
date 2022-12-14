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
@Table(name = "INORDERD")
public class Inorderd implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "INCODE", nullable = false)
    private String incode;

    @Column(name = "INSEQ", nullable = false)
    private Integer inseq;

    @Column(name = "STOCKTYPE")
    private String stocktype;

    @Column(name = "BUYCODE")
    private String buycode;

    @Column(name = "ITEMCODE")
    private String itemcode;

    @Column(name = "MANDATE")
    private Date mandate;

    @Column(name = "SHELFLIFE")
    private String shelflife;

    @Column(name = "INQTY", nullable = false)
    private BigDecimal inqty;

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

    @Column(name = "MODUSER")
    private String moduser;

    @Column(name = "MODDATE")
    private Date moddate;

}
