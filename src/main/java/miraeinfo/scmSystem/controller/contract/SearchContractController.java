package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.entity.Itembasket;
import miraeinfo.scmSystem.entity.Orderdetail;
import miraeinfo.scmSystem.entity.QOrderinfo;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.OrderdetailRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.util.*;

import static miraeinfo.scmSystem.entity.QIteminfo.*;
import static miraeinfo.scmSystem.entity.QCustinfo.*;
import static miraeinfo.scmSystem.entity.QOrderinfo.*;
import static miraeinfo.scmSystem.entity.QOrderdetail.*;

@RestController
public class SearchContractController {

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

    public SearchContractController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 거래내역 리스트 조회
    @GetMapping("/api/searchContract/searchContractList")
    public List<Map<String, Object>> getSearchContractList(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
            String word,
            String confirmYn) {
        List<Tuple> tuple = null;
        // 전체 조회
        if (confirmYn == "") {
            tuple = queryFactory
                    .select(
                            orderinfo.orddate,
                            orderinfo.orderno,
                            custinfo.custname,
                            iteminfo.itemname,
                            iteminfo.spec,
                            iteminfo.unit,
                            iteminfo.dan,
                            orderdetail.ordqty,
                            orderdetail.amount,
                            orderdetail.vat,
                            orderdetail.deldate,
                            orderinfo.confirmYn
                    )
                    .from(orderinfo)
                    .leftJoin(custinfo).on(orderinfo.custcode.eq(custinfo.custcode))
                    .leftJoin(orderdetail).on(orderinfo.orderno.eq(orderdetail.orderno))
                    .leftJoin(iteminfo).on(orderdetail.itemcode.eq(iteminfo.itemcode))
                    .where(orderinfo.ordtype.eq("WEB")
                            .and(orderinfo.orddate.goe(fromDate))
                            .and(orderinfo.orddate.loe(toDate))
                            .and(itemNameContains(word))
                    )
                    .fetch();
        // 확정 미확정이 있는 경우
        } else {
            tuple = queryFactory
                    .select(
                            orderinfo.orddate,
                            orderinfo.orderno,
                            custinfo.custname,
                            iteminfo.itemname,
                            iteminfo.spec,
                            iteminfo.unit,
                            iteminfo.dan,
                            orderdetail.ordqty,
                            orderdetail.amount,
                            orderdetail.vat,
                            orderdetail.deldate,
                            orderinfo.confirmYn
                    )
                    .from(orderinfo)
                    .leftJoin(custinfo).on(orderinfo.custcode.eq(custinfo.custcode))
                    .leftJoin(orderdetail).on(orderinfo.orderno.eq(orderdetail.orderno))
                    .leftJoin(iteminfo).on(orderdetail.itemcode.eq(iteminfo.itemcode))
                    .where(orderinfo.ordtype.eq("WEB")
                            .and(orderinfo.orddate.goe(fromDate))
                            .and(orderinfo.orddate.loe(toDate))
                            .and(iteminfo.itemname.contains(word))
                            .and(orderinfo.confirmYn.eq(confirmYn))
                    )
                    .fetch();
        }

        List<Map<String,Object>> searchContractArr = new ArrayList<>();
        Object beforeOrderNo = "";
        Object orderTotalAmount = "";
        for (Tuple row : tuple) {
            Map<String,Object> searchContractMap = new HashMap<String, Object>();
            searchContractMap.put("ordDate", row.get(orderinfo.orddate));
            // 주문번호 로우스팬을 위한 빈 문자열 입력
            if (!beforeOrderNo.equals(row.get(orderinfo.orderno))){
                searchContractMap.put("showOrderNo", row.get(orderinfo.orderno));
                searchContractMap.put("confirmYn", row.get(orderinfo.confirmYn)); // 확정 여부
                searchContractMap.put("checkBox", "visible");
                searchContractMap.put("modiCancle", "visible"); // 수정취소 보이도록
                beforeOrderNo = row.get(orderinfo.orderno);
            } else if (beforeOrderNo.equals(row.get(orderinfo.orderno))){
                searchContractMap.put("showOrderNo", "");
                searchContractMap.put("confirmYn", ""); // 확정 여부
                searchContractMap.put("checkBox", "invisible");
                searchContractMap.put("modiCancle", "invisible");
            }
            searchContractMap.put("orderNo", row.get(orderinfo.orderno));
            searchContractMap.put("custName", row.get(custinfo.custname));
            searchContractMap.put("itemName", row.get(iteminfo.itemname));
            searchContractMap.put("spec", row.get(iteminfo.spec));
            searchContractMap.put("unit", row.get(iteminfo.unit));
            searchContractMap.put("dan", row.get(iteminfo.dan));
            searchContractMap.put("qty", row.get(orderdetail.ordqty));
            searchContractMap.put("amount", row.get(orderdetail.amount));
            searchContractMap.put("vat", row.get(orderdetail.vat));
            // 부가세액이 있는 경우와 없는 경우 구분
            if (row.get(orderdetail.vat) != null){
                searchContractMap.put("paymentAmt", row.get(orderdetail.amount).add(row.get(orderdetail.vat)));
            } else {
                searchContractMap.put("paymentAmt", row.get(orderdetail.amount));
            }

            searchContractMap.put("delDate", row.get(orderdetail.deldate));  // 배송예정일
//            searchContractMap.put("confirmYn", row.get(orderinfo.confirmYn)); // 확정 여부
            searchContractArr.add(searchContractMap);
        }

        return searchContractArr;
    }

    // 거래내역 리스트 조회 검색조건
    private BooleanExpression itemNameContains(String itemName) {
        return itemName != "" ? iteminfo.itemname.contains(itemName) : null;
    }

    // 거래내역 리스트 수정/취소
    @Transactional
    @PostMapping("/api/searchContract/contractOrderCancel")
    public void cancelContractOrder (@RequestBody Map<String, Object> reqOrderInfo) {
        System.out.println(reqOrderInfo);
        Object oj1 = reqOrderInfo.get("params");
        Map<String, String> oj1_c1 = (Map<String, String>) oj1;
        String ordNo = oj1_c1.get("ordNo");
        String userId = oj1_c1.get("userId");

        List<Orderdetail> orderdetails = queryFactory
                .selectFrom(orderdetail)
                .where(orderdetail.orderno.eq(ordNo))
                .fetch();

        for (Orderdetail detail : orderdetails) {
            Itembasket itembasket = new Itembasket();
            itembasket.setUserid(userId);
            itembasket.setItemcode(detail.getItemcode());
            itembasket.setQty(detail.getOrdqty());
            itembasket.setReguser(userId);
            itembasket.setRegdate(new Date());
            itembasket.setModuser(userId);
            itembasket.setModdate(new Date());

            System.out.println(itembasket);

            em.persist(itembasket);
        }

        // 주문 인포 테이블에서 삭제
        queryFactory.delete(orderinfo)
                .where(orderinfo.orderno.eq(ordNo))
                .execute();
        // 주문 디테일 테이블에서 삭제
        queryFactory.delete(orderinfo)
                .where(orderinfo.orderno.eq(ordNo))
                .execute();
    }





}
