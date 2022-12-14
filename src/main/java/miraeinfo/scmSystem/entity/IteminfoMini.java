package miraeinfo.scmSystem.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "ITEMINFO")
public class IteminfoMini implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ITEMCODE", nullable = false)
    private String itemcode;

    @Column(name = "ITEMNAME", nullable = false)
    private String itemname;

    @Column(name = "TYPE1")
    private String TYPE1;

    @Column(name = "TYPE2")
    private String TYPE2;

    @Column(name = "TYPE3", nullable = false)
    private String TYPE3;

}
