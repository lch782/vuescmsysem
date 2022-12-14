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
@Table(name = "CODEDET")
public class Codedet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MCODE", nullable = false)
    private String mcode;

    @Column(name = "DCODE", nullable = false)
    private String dcode;

    @Column(name = "DCODENAME", nullable = false)
    private String dcodename;

    @Column(name = "EXTRA1")
    private String EXTRA1;

    @Column(name = "EXTRA2")
    private String EXTRA2;

    @Column(name = "EXTRA3")
    private String EXTRA3;

    @Column(name = "SORTSEQ")
    private String sortseq;

    @Column(name = "NONUSE")
    private String nonuse;

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
