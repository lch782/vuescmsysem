package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 외상이력테이블
 */
@Data
@Entity
@Table(name = "CREDIT")
public class Credit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ORDERNO", nullable = false)
    private String orderno;

    @Column(name = "ORDSEQ")
    private Integer ordseq;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "STS")
    private String sts;

    @Column(name = "PRC")
    private BigDecimal prc;

    @Column(name = "VAT")
    private BigDecimal vat;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

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
