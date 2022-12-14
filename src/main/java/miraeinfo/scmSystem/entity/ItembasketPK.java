package miraeinfo.scmSystem.entity;

import lombok.Data;

import java.io.Serializable;

// ITEMBASKET Table에 있는 기본키를 Entity에서 사용하기 위해 만든 클래스
@Data
public class ItembasketPK implements Serializable { 
    
    private String userid;
    private String itemcode;

}
