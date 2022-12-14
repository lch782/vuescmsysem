package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.OrderdetailRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.*;

import static miraeinfo.scmSystem.entity.QCustinfo.custinfo;
import static miraeinfo.scmSystem.entity.QIteminfo.iteminfo;
import static miraeinfo.scmSystem.entity.QOrderdetail.orderdetail;
import static miraeinfo.scmSystem.entity.QOrderinfo.orderinfo;

@RestController
public class SearchContractDeliveryController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    CustinfoRepository custinfoRepository;

    @Autowired
    IteminfoRepository iteminfoRepository;

    @Autowired
    OrderinfoRepository orderinfoRepository;

    @Autowired
    OrderdetailRepository orderdetailRepository;

    public SearchContractDeliveryController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 거래내역 리스트
    @GetMapping("/api/searchContractDelivery/searchContractDeliveryList")
    public List<Map<String, Object>> getSearchContractDeliveryList(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
            String word) {
        List<Tuple> tuple = queryFactory
                    .select(
                            orderinfo.orddate,
                            orderinfo.orderno,
                            custinfo.custname,
                            iteminfo.itemname,
                            orderdetail.amount,
                            orderdetail.deldate,
                            orderdetail.ordqty
                    )
                    .from(orderinfo)
                    .leftJoin(custinfo).on(orderinfo.custcode.eq(custinfo.custcode))
                    .leftJoin(orderdetail).on(orderinfo.orderno.eq(orderdetail.orderno))
                    .leftJoin(iteminfo).on(orderdetail.itemcode.eq(iteminfo.itemcode))
                    .where(orderinfo.ordtype.eq("WEB")
                            .and(orderinfo.orddate.goe(fromDate))
                            .and(orderinfo.orddate.loe(toDate))
                            .and(iteminfo.itemname.contains(word))
                            .and(orderinfo.confirmYn.contains("Y"))
                    )
                    .fetch();

        List<Map<String,Object>> searchContractArr = new ArrayList<>();
        for (Tuple row : tuple) {
            Map<String,Object> searchContractMap = new HashMap<String, Object>();
            searchContractMap.put("ordDate", row.get(orderinfo.orddate));
            searchContractMap.put("orderNo", row.get(orderinfo.orderno));
            searchContractMap.put("custName", row.get(custinfo.custname));
            searchContractMap.put("itemName", row.get(iteminfo.itemname));
            searchContractMap.put("qty", row.get(orderdetail.ordqty));
            searchContractMap.put("amt", row.get(orderdetail.amount));
            searchContractMap.put("delDate", row.get(orderdetail.deldate));
            searchContractArr.add(searchContractMap);
        }

        return searchContractArr;
    }

}
