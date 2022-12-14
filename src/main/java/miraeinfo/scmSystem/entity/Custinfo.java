package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "CUSTINFO")
public class Custinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTCODE", nullable = false)
    private String custcode;

    @Column(name = "GROUPCODE")
    private String groupcode;

    @Column(name = "CUSTTYPE", nullable = false)
    private String custtype;

    @Column(name = "CUSTNAME", nullable = false)
    private String custname;

    @Column(name = "BIZMANTYPE")
    private String bizmantype;

    @Column(name = "FOREIGN_YN")
    private String foreignYn;

    @Column(name = "BIZNO")
    private String bizno;

    @Column(name = "CEONAME")
    private String ceoname;

    @Column(name = "BIZTYPE")
    private String biztype;

    @Column(name = "BIZITEM")
    private String bizitem;

    @Column(name = "CREDITLIMIT")
    private BigDecimal creditlimit;

    @Column(name = "ZIPCODE")
    private String zipcode;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "ADDRESS2")
    private String ADDRESS2;

    @Column(name = "COMPCODE")
    private String compcode;

    @Column(name = "TEL")
    private String tel;

    @Column(name = "TEL2")
    private String TEL2;

    @Column(name = "FAX")
    private String fax;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "BILLNAME")
    private String billname;

    @Column(name = "BILLTEL")
    private String billtel;

    @Column(name = "BILLEMAIL")
    private String billemail;

    @Column(name = "PICNAME")
    private String picname;

    @Column(name = "PICEMAIL")
    private String picemail;

    @Column(name = "NONUSE")
    private String nonuse;

    @Column(name = "ISADMIN")
    private String isadmin;

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

    @Column(name = "ID")
    private String id;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

}
