package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

@Data
@Entity
@Table(name = "INORDERM")
@NamedStoredProcedureQueries({
        // 납품등록 Insert
        @NamedStoredProcedureQuery(
                name = "InOrderInsert",
                procedureName = "P_MT0210_S",
                parameters = {
                        @StoredProcedureParameter(name = "JOB", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INCODE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INTYPE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INSTS", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "EMPCODE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "REMARK", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "BUYCODE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ITEMCODE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INSEQ", type = Integer.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "STOCKTYPE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "MANDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "SHELFLIFE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INQTY", type = BigDecimal.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "DAN", type = BigDecimal.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "AMOUNT", type = BigDecimal.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "VAT", type = BigDecimal.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "REGUSER", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ISDELETE", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "RTNVAL", type = String.class, mode = ParameterMode.OUT)
                }
        ),
        @NamedStoredProcedureQuery(
                name = "TradingInfo",
                procedureName = "P_TRADEINFO_Q",
                parameters = {
                        @StoredProcedureParameter(name = "USERID", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "FROMDATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TODATE", type = Date.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE1", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE2", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "TYPE3", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "ITEMNAME", type = String.class, mode = ParameterMode.IN),
                        @StoredProcedureParameter(name = "INCODE", type = String.class, mode = ParameterMode.IN)
                }
        )
})
public class Inorderm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "INCODE", nullable = false)
    private String incode;

    @Column(name = "INDATE", nullable = false)
    private Date indate;

    @Column(name = "EMPCODE")
    private String empcode;

    @Column(name = "INTYPE")
    private String intype;

    @Column(name = "INSTS")
    private String insts;

    @Column(name = "TESTCODE")
    private String testcode;

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
