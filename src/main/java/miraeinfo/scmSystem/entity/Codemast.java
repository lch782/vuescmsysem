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
@Table(name = "CODEMAST")
public class Codemast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "MCODE", nullable = false)
    private String mcode;

    @Column(name = "MCODENAME", nullable = false)
    private String mcodename;

    @Column(name = "SYS_YN")
    private String sysYn;

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
