package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "COMPINFO")
public class Compinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "COMPCODE", nullable = false)
    private String compcode;

    @Column(name = "COMPNAME", nullable = false)
    private String compname;

    @Column(name = "COMPNAMEENG", nullable = false)
    private String compnameeng;

    @Column(name = "BIZNO", nullable = false)
    private String bizno;

    @Column(name = "COMPNO", nullable = false)
    private String compno;

    @Column(name = "CEONAME", nullable = false)
    private String ceoname;

    @Column(name = "BIZTYPE", nullable = false)
    private String biztype;

    @Column(name = "BIZITEM", nullable = false)
    private String bizitem;

    @Column(name = "ZIPCODE", nullable = false)
    private String zipcode;

    @Column(name = "ADDRSS1", nullable = false)
    private String ADDRSS1;

    @Column(name = "ADDRSS2", nullable = false)
    private String ADDRSS2;

    @Column(name = "TEL", nullable = false)
    private String tel;

    @Column(name = "TEL2", nullable = false)
    private String TEL2;

    @Column(name = "FAX", nullable = false)
    private String fax;

    @Column(name = "EMAIL1", nullable = false)
    private String EMAIL1;

    @Column(name = "EMAIL2", nullable = false)
    private String EMAIL2;

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

    @Column(name = "PASSWORD", nullable = false)
    private String password;

}
