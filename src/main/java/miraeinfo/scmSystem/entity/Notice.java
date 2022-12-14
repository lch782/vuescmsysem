package miraeinfo.scmSystem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Getter
@Setter
@Table(name = "NOTICE")
@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(
                name = "selectNoticeInfo",
                procedureName = "P_NOTICE_Q",
                resultClasses = { Notice.class },
                parameters = {
                        @StoredProcedureParameter(
                                name = "SEQ", type = Integer.class, mode = ParameterMode.IN
                        )
                }
        )
})
public class Notice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SEQ", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "NUMSEQ")
    private int numseq;

    @Column(name = "COMPCODE")
    private String compcode;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "CONTENTS")
    private String contents;

    @Column(name = "IMGPATCH")
    private String imgpatch;

    @Column(name = "IMGNAME")
    private String imgname;

    @Column(name = "REGUSER")
    private String reguser;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Column(name = "REGDATE")
    private Date regdate;

    @Column(name = "REGIP")
    private String regip;

    @Column(name = "MODUSER")
    private String moduser;

    @JsonFormat(pattern = "yyyy.MM.dd")
    @Column(name = "MODDATE")
    private Date moddate;

    @Column(name = "MODIP")
    private String modip;

    @Column(name = "SELQTY")
    private Integer selqty;


}
