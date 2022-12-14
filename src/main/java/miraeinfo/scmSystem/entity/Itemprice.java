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
@Table(name = "ITEMPRICE")
public class Itemprice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ITEMCODE", nullable = false)
    private String itemcode;

    @Column(name = "CUSTCODE", nullable = false)
    private String custcode;

    /**
     * 적용일자
     */
    @Column(name = "SDATE", nullable = false)
    private Date sdate;

    @Column(name = "DAN", nullable = false)
    private BigDecimal dan;

    @Column(name = "REASON")
    private String reason;

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
