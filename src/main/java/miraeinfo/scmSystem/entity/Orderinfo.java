package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "ORDERINFO")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "findOrderNo",
                procedureName = "USP_SM0199_Q",
                parameters = {
                        @StoredProcedureParameter(name = "seqId", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "regUser", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "modIp", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "rtnVal", type = String.class, mode = ParameterMode.OUT)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "montlySales",
                procedureName = "P_ORDER_Q",
                parameters = {
                        @StoredProcedureParameter(name = "BIZNO", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "CUSTCODE", type = String.class, mode = ParameterMode.IN)
                }
        )
})
public class Orderinfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ORDERNO", nullable = false)
    private String orderno;

    @Column(name = "ORDDATE", nullable = false)
    private Date orddate;

    @Column(name = "CUSTCODE")
    private String custcode;

    @Column(name = "ORDERCUST")
    private String ordercust;

    @Column(name = "ORDTYPE", nullable = false)
    private String ordtype;

    @Column(name = "DEALTYPE", nullable = false)
    private String dealtype;

    @Column(name = "ORDERTEL")
    private String ordertel;

    /**
     * 수주확정여부
     */
    @Column(name = "CONFIRM_YN")
    private String confirmYn;

    @Column(name = "FINISH_YN", nullable = false)
    private String finishYn;

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
