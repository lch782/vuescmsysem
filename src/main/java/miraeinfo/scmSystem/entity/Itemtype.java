package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "ITEMTYPE")
public class Itemtype implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SEQ", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "TYPE1", nullable = false)
    private String TYPE1;

    @Column(name = "TYPE2", nullable = false)
    private String TYPE2;

    @Column(name = "TYPE3", nullable = false)
    private String TYPE3;

    @Column(name = "NONUSE")
    private String nonuse;

    @Column(name = "WEB")
    private String web;

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
