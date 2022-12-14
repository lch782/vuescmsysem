package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.controller.main.config.ConfigMain;
import miraeinfo.scmSystem.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ContractCommonController {

    private final EntityManager em;
    private  final JPAQueryFactory queryFactory;



    public ContractCommonController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

//    @GetMapping("/")
//    public String homeIndex(){
//        return "index.html";
//    }
    
    // 주문하기의 2차 카테고리를 구하는 함수
    @GetMapping("/api/contract/getMiddleCategory")
    public List<Codedet> getMiddleCategory(){

        List<Codemast> codemast = queryFactory
                .select(Projections.bean(Codemast.class,
                        QCodemast.codemast.mcode,
                        QCodemast.codemast.mcodename
                ))
                .from(QCodemast.codemast)
                .where(QCodemast.codemast.mcode.like("TYPE%"))
                .fetch();

        List<Codedet> typeMiddleDivList = new ArrayList<>();
        //타입별 분류
        for (Codemast cm : codemast){
            if (cm.getMcode().equals(ConfigMain.type2)){
                    List<Codedet> cd = queryFactory
                            .select(Projections.bean(Codedet.class,
                                    QCodedet.codedet.mcode,
                                    QCodedet.codedet.dcode,
                                    QCodedet.codedet.dcodename
                            ))
                            .from(QCodedet.codedet)
                            .where(QCodedet.codedet.mcode.like(cm.getMcode())
                            .and(QCodedet.codedet.dcode.notLike("A"))
                            .and(QCodedet.codedet.dcode.notLike("D"))
                            .and(QCodedet.codedet.dcode.notLike("E"))
                            .and(QCodedet.codedet.dcode.notLike("F"))
                            .and(QCodedet.codedet.dcode.notLike("G"))
                            .and(QCodedet.codedet.dcode.notLike("L")))
                            .where(QCodedet.codedet.nonuse.isNull()
                                    .or(QCodedet.codedet.nonuse.like("N")))
                            .fetch();
//                       System.out.println(cd);
                if (cd != null){
                    typeMiddleDivList = cd;
                }
            }
        }
        return typeMiddleDivList;
    }

    // 총갯수를 박스와 낱개로 바꿔주는 함수
    @GetMapping("/api/contract/getTotalToQtyBox")
    public Map<String, Integer> getTotalToQtyBox (@RequestParam String itemCode, int totalQty) {
        return null;
    }
}
