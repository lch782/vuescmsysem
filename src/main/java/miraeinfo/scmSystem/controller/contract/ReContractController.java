package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.entity.Itembasket;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.OrderdetailRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

import static miraeinfo.scmSystem.entity.QCustinfo.custinfo;
import static miraeinfo.scmSystem.entity.QItembasket.itembasket;
import static miraeinfo.scmSystem.entity.QIteminfo.iteminfo;
import static miraeinfo.scmSystem.entity.QOrderdetail.orderdetail;
import static miraeinfo.scmSystem.entity.QOrderinfo.orderinfo;

@RestController
public class ReContractController {

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

    public ReContractController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 거래내역 리스트
    @GetMapping("/api/reContract/reContractList")
    public List<Map<String, Object>> getSearchContractList(
            @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
            String searchType ,
            String word) {
        List<Tuple> tuple = queryFactory
                    .select(
                            orderinfo.orderno,
                            orderinfo.orddate,
                            orderdetail.itemcode,
                            iteminfo.itemname,
                            iteminfo.spec,
                            iteminfo.unit,
                            iteminfo.qty,
                            orderdetail.ordqty,
                            orderdetail.dan,
                            orderdetail.amount
                    )
                    .from(orderinfo)
                    .leftJoin(custinfo).on(orderinfo.custcode.eq(custinfo.custcode))
                    .leftJoin(orderdetail).on(orderinfo.orderno.eq(orderdetail.orderno))
                    .leftJoin(iteminfo).on(orderdetail.itemcode.eq(iteminfo.itemcode))
                    .where(orderinfo.ordtype.eq("WEB")
                            .and(orderinfo.orddate.goe(fromDate))
                            .and(orderinfo.orddate.loe(toDate))
                            .and(searchReContract(searchType, word))
                            .and(orderinfo.confirmYn.eq("Y"))
                    )
                    .fetch();

        List<Map<String,Object>> searchContractArr = new ArrayList<>();
        for (Tuple row : tuple) {
            Map<String,Object> searchContractMap = new HashMap<String, Object>();
            searchContractMap.put("ordNo", row.get(orderinfo.orderno));
            searchContractMap.put("ordDate", row.get(orderinfo.orddate));
            searchContractMap.put("itemCode", row.get(orderdetail.itemcode));
            searchContractMap.put("itemName", row.get(iteminfo.itemname));
            searchContractMap.put("spec", row.get(iteminfo.spec));
            searchContractMap.put("unit", row.get(iteminfo.unit));
            searchContractMap.put("qty", row.get(iteminfo.qty));
            searchContractMap.put("ordQty", row.get(orderdetail.ordqty));
            searchContractMap.put("dan", row.get(orderdetail.dan));
            searchContractMap.put("amt", row.get(orderdetail.amount));
            searchContractMap.put("qtyField", 0);
            searchContractMap.put("boxField", 0);
            searchContractArr.add(searchContractMap);
        }

        return searchContractArr;
    }

    @Transactional
    @PostMapping("/api/recontract/putContractBasket")
    public void putContractBasket(@RequestBody Map<String,Object> recontractList ) throws Exception {

        Object oj1 = recontractList.get("params");
        Map<String, String> oj1_c1 = (Map<String, String>) oj1;
        String userId = (oj1_c1.get("userId"));
        Object oj2 = oj1_c1.get("rowData");
        /*
            그리드에 있는 버튼을 눌렀을떄 LinkedHashMap으로 와서
            체크박스로 선택한것과 같은 형태로 바꿔주기위해 ArrayList 선언 후 add
         */
        ArrayList<Map<String, String>> tmp = new ArrayList<>();
        List<Map<String, String >> oj2_c1 = null;
        if (oj2.getClass().getName() == "java.util.LinkedHashMap") {
            tmp.add((Map<String, String>) oj2);
            oj2_c1 = tmp;
        } else {
            oj2_c1 = (List<Map<String, String>>) oj2;
        }
        Itembasket itemBasket = null;
        for (int i = 0; i < oj2_c1.size(); i++) {
            Map<String, String> imap = oj2_c1.get(i);
            itemBasket = new Itembasket();
            // 해당 사용자의 장바구니에 선택한 물품이 이미 존재하는지 조회

            // itemcode 와 itemCode 체크
            String itemCode = null;
            if (imap.get("itemCode") != null) {
                itemCode = imap.get("itemCode");
            }
            else if (imap.get("itemcode") != null) {
                itemCode = imap.get("itemcode");
            }

            List<Tuple> findBasketItem = queryFactory
                    .select(
                            itembasket.userid,
                            itembasket.itemcode,
                            itembasket.qty
                    )
                    .from(itembasket)
                    .where(itembasket.userid.eq(userId)
                            .and(itembasket.itemcode.eq(itemCode))
                    )
                    .fetch();
            itemBasket.setUserid(userId);
            itemBasket.setItemcode(imap.get("itemCode"));
            itemBasket.setReguser(userId);
            itemBasket.setRegdate(new Date());
            itemBasket.setModuser(userId);
            itemBasket.setModdate(new Date());
            int boxField = Integer.parseInt(String.valueOf(imap.get("boxField")));
            int qtyField = Integer.parseInt(String.valueOf(imap.get("qtyField")));
            String qty_s = String.valueOf(imap.get("qty"));
            int qty = Integer.parseInt(qty_s);
            BigDecimal allQty = new BigDecimal((qty * boxField) + qtyField);
            String ordQty_s = String.valueOf(imap.get("ordQty"));
            BigDecimal ordQty = new BigDecimal(ordQty_s);
            // 존재하면 merge로 수량 변경
            if (findBasketItem.size() > 0) {
                for (Tuple row : findBasketItem) {
                    BigDecimal existQty = row.get(itembasket.qty);
                    if (allQty.intValue() != 0) {
                        itemBasket.setQty(BigDecimal.valueOf(allQty.intValue() + existQty.intValue()));
                    } else {
                        itemBasket.setQty(ordQty);
                    }
                    em.merge(itemBasket);
                }
            }
            // 존재하지 않으면 insert
            else {
                if (allQty.intValue() != 0) {
                    itemBasket.setQty(allQty);
                } else {
                    itemBasket.setQty(ordQty);
                }
                em.persist(itemBasket);
            }
        }
    }

    private BooleanExpression searchReContract (String searchType, String word){
        BooleanExpression result = null;
        if (searchType.equals("itemName") && !word.equals("")) {
            result = iteminfo.itemname.contains(word);
        }
        else if (searchType.equals("contractNo") && !word.equals("")) {
            result = orderinfo.orderno.contains(word);
        }
        else if (searchType.equals("itemCode") && !word.equals("")) {
            result = orderdetail.itemcode.contains(word);
        }
        else if (searchType.equals("spec") && !word.equals("")) {
            result = iteminfo.spec.contains(word);
        }
        else if (searchType.equals("dan") && !word.equals("")) {
            BigDecimal dan = new BigDecimal(word);
            result = orderdetail.dan.eq(dan);
        }
        else if (searchType.equals("qty") && !word.equals("")) {
            BigDecimal qty = new BigDecimal(word);
            result = orderdetail.ordqty.eq(qty);
        }
        else if (searchType.equals("supplyPrice") && !word.equals("")) {
            BigDecimal supplyPrice = new BigDecimal(word);
            result = orderdetail.amount.eq(supplyPrice);
        }
        else if (searchType.equals("vat") && !word.equals("")) {
            BigDecimal vat = new BigDecimal(word);
            result = orderdetail.vat.eq(vat);
        }
        else if (searchType.equals("amount") && !word.equals("")) {
            BigDecimal amount = new BigDecimal(word);
            result = orderdetail.amount.eq(amount);
        }

        return result;
    }

}
