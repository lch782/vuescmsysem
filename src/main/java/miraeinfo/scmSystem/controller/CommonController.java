package miraeinfo.scmSystem.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.entity.Custinfo;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.List;

import static miraeinfo.scmSystem.entity.QCustinfo.custinfo;

@RestController
public class CommonController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    CustinfoRepository custinfoRepository;
    @Autowired
    OrderinfoRepository orderinfoRepository;

    public CommonController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 거래처정보 들고오기
    @GetMapping("/api/common/custinfo")
    public List<Custinfo> getCustInfo(@RequestParam String bizNo) {
        return queryFactory
                .select(custinfo)
                .from(custinfo)
                .where(custinfo.bizno.eq(bizNo))
                .fetch();
    }
}
