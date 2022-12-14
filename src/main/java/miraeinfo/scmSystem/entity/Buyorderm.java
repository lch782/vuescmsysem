package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "BUYORDERM")
@NamedStoredProcedureQueries({
        // 발주관리 및 납품등록 조회
        @NamedStoredProcedureQuery(
                name = "findBuyOrderList",
                procedureName = "P_CHECKORDER_Q",
                parameters = {
                        @StoredProcedureParameter(name = "USERID", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "FROMDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TODATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE1", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE2", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE3", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ITEMNAME", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "BUYSTS", type = String.class, mode = ParameterMode.IN),
                }
        ),
        @NamedStoredProcedureQuery(
                name = "purchaseInfo",
                procedureName = "P_PURCHASEINFO_Q",
                parameters = {
                        @StoredProcedureParameter(name = "USERID", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "FROMDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TODATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE1", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE2", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE3", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ITEMNAME", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "BUYSTS", type = String.class, mode = ParameterMode.IN),
                }
        ),
        @NamedStoredProcedureQuery(
                name = "purchaseItem",
                procedureName = "P_PURCHASEITEM_Q",
                parameters = {
                        @StoredProcedureParameter(name = "USERID", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "FROMDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TODATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE1", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE2", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE3", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ITEMNAME", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "BUYSTS", type = String.class, mode = ParameterMode.IN),
                }
        )
})
public class Buyorderm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "BUYCODE", nullable = false)
    private String buycode;

    @Column(name = "CUSTCODE")
    private String custcode;

    @Column(name = "BUYTYPE")
    private String buytype;

    @Column(name = "BUYDATE")
    private Date buydate;

    @Column(name = "EMPCODE")
    private String empcode;

    @Column(name = "BUYSTS")
    private String buysts;

    @Column(name = "CONFIRM_YN")
    private String confirmYn;

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
