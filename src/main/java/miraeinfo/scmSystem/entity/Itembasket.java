package miraeinfo.scmSystem.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Getter
@Table(name = "ITEMBASKET")
@IdClass(ItembasketPK.class)
public class Itembasket implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USERID", nullable = false)
    private String userid;

    @Id
    @Column(name = "ITEMCODE", nullable = false)
    private String itemcode;

    @Column(name = "QTY")
    private BigDecimal qty;

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
