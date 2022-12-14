package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.entity.Custinfo;
import miraeinfo.scmSystem.entity.Itembasket;
import miraeinfo.scmSystem.entity.Orderdetail;
import miraeinfo.scmSystem.entity.Orderinfo;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.repository.ItempriceRepository;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.OrderinfoRepository;
import miraeinfo.scmSystem.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static miraeinfo.scmSystem.entity.QItembasket.itembasket;
import static miraeinfo.scmSystem.entity.QIteminfo.iteminfo;

@RestController
public class ContractBasketController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    CustinfoRepository custinfoRepository;

    @Autowired
    IteminfoRepository iteminfoRepository;

    @Autowired
    ItempriceRepository itempriceRepository;

    @Autowired
    OrderinfoRepository orderinfoRepository;

    public ContractBasketController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 장바구니 정보 들고오기
    @GetMapping("/api/contractBasket/contractBasketList")
    public List<Map<String, Object>> getContractBasketList(@RequestParam String userId) {

        BigDecimal vatRatio = new BigDecimal("0.1");

        List<Tuple> tuple = queryFactory
                .select(
                        itembasket.userid,
                        itembasket.itemcode,
                        iteminfo.itemname,
                        iteminfo.spec,
                        iteminfo.unit,
                        iteminfo.dan,
                        itembasket.qty,
                        iteminfo.vatyn,
                        iteminfo.qty,
                        iteminfo.boxdan
                )
                .from(itembasket)
                .leftJoin(iteminfo).on(itembasket.itemcode.eq(iteminfo.itemcode))
                // .leftJoin(itemprice).on(itembasket.itemcode.eq(itemprice.itemcode))
                .where(itembasket.userid.eq(userId))
                .fetch();

//        BigDecimal bdDan = new BigDecimal(row.get(iteminfo.dan).toBigInteger());

        BigDecimal vat = new BigDecimal(0);
        BigDecimal vatTotal = new BigDecimal(0);

        List<Map<String,Object>> basketArr = new ArrayList<>();
        for (Tuple row : tuple) {
            Map<String,Object> basketMap = new HashMap<String, Object>();
            basketMap.put("userid", row.get(itembasket.userid));
            basketMap.put("itemCode", row.get(itembasket.itemcode));
            basketMap.put("itemName", row.get(iteminfo.itemname));
            basketMap.put("spec", row.get(iteminfo.spec));
            basketMap.put("unit", row.get(iteminfo.unit));
            basketMap.put("dan", row.get(iteminfo.dan));
            basketMap.put("qty", row.get(itembasket.qty));
            basketMap.put("vatyn", row.get(iteminfo.vatyn));
            if (row.get(iteminfo.vatyn).equals("Y")) {
                vat = row.get(iteminfo.dan).multiply(vatRatio);
                vatTotal = vat.multiply(row.get(itembasket.qty));
            }
            basketMap.put("surtax", vatTotal);
            basketMap.put("supplyprice", (row.get(iteminfo.dan).multiply(row.get(itembasket.qty))));
            basketMap.put("itemQty", row.get(iteminfo.qty));
            // 박스당 단가를 따로 계산
            BigDecimal boxCount = row.get(itembasket.qty).divide(row.get(iteminfo.qty), RoundingMode.FLOOR);
            boxCount = boxCount.setScale(0, RoundingMode.FLOOR);
            BigDecimal zero = BigDecimal.ZERO;
            BigDecimal qtyInBox = BigDecimal.ZERO; // 박스에 들어있는 총 갯수
            BigDecimal resBoxAmount = BigDecimal.ZERO;
            BigDecimal remainQty = BigDecimal.ZERO;
            BigDecimal remainQtyAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = BigDecimal.ZERO; // 최종 합산 금액
            if (row.get(iteminfo.boxdan) != null && !boxCount.equals(zero)) {
                qtyInBox = boxCount.multiply(row.get(iteminfo.qty));
                resBoxAmount = qtyInBox.multiply(row.get(iteminfo.boxdan));

                remainQty = row.get(itembasket.qty).remainder(row.get(iteminfo.qty));
                if (!remainQty.equals(zero)){
                    remainQtyAmount = remainQty.multiply(row.get(iteminfo.dan));
                }
                totalAmount = resBoxAmount.add(remainQtyAmount);

                basketMap.put("supplyprice", totalAmount);
                if (row.get(iteminfo.vatyn).equals("Y")) {
                    vatTotal = totalAmount.multiply(vatRatio);
                    basketMap.put("surtax", vatTotal);
                } else if (row.get(iteminfo.vatyn).equals("N")) {
                    vatTotal = BigDecimal.ZERO;
                }
                basketMap.put("amt", totalAmount.add(vatTotal));

            } else {
                basketMap.put("amt", (row.get(iteminfo.dan).multiply(row.get(itembasket.qty))).add(vatTotal));
            }

            basketArr.add(basketMap);
        }

        return basketArr;
    }

    // 장바구니에 넣는 함수 하나씩
    @Transactional
    @PostMapping("/api/contractBasket/putContractBasket")
    public void putContractBasket(@RequestBody Itembasket itemBasket) {
        // 데이터 수정 및 삽입전 Entity에 바인딩
        itemBasket.setReguser(itemBasket.getUserid());
        itemBasket.setModuser(itemBasket.getUserid());
        itemBasket.setRegdate(new Date());
        itemBasket.setModdate(new Date());

//        System.out.println("itemBasket = " + itemBasket);

        // 해당 사용자의 장바구니에 선택한 물품이 이미 존재하는지 조회
        List<Tuple> findBasketItem = queryFactory
                .select(
                        itembasket.userid,
                        itembasket.itemcode,
                        itembasket.qty
                )
                .from(itembasket)
                .where(itembasket.userid.eq(itemBasket.getUserid())
                        .and(itembasket.itemcode.eq(itemBasket.getItemcode()))
                      )
                .fetch();
        // 존재하면 merge로 수량 변경
        if (findBasketItem.size() > 0) {
            for (Tuple row : findBasketItem) {
                itemBasket.setQty(itemBasket.getQty().add(row.get(itembasket.qty)));
                em.merge(itemBasket);
            }
        }
        // 존재하지 않으면 insert
        else {
            em.persist(itemBasket);
        }
    }

    // 아이템을 하나씩 선택해서 지울때
    @Transactional
    @DeleteMapping("/api/contractBasket/contractBasketListDelete")
    public void contractBasketListDelete(
            @RequestParam String bizNo, String itemCode) {
        queryFactory.delete(itembasket)
                .where(itembasket.userid.eq(bizNo)
                        .and(itembasket.itemcode.eq(itemCode))
                      )
                .execute();
    }

    // 체크된 아이템을 모두 지울 때
    @Transactional
    @PostMapping("/api/contractBasket/deleteSelectedItem")
    public void deleteSelectedItem(@RequestBody Map<String, Object> itembasketList) throws Exception {

        Object oj1 = itembasketList.get("params");
        Map<String, String> oj1_c1 = (Map<String, String>) oj1;
//        String custCode = (oj1_c1.get("custCode"));
        String custName = (oj1_c1.get("custName"));
        String userId = (oj1_c1.get("userId"));
        Object oj2 = oj1_c1.get("rowData");
        List<Map<String, String >> oj2_c1 = (List<Map<String, String>>) oj2;

        for (int i = 0; i < oj2_c1.size(); i++) {
            Map<String, String> imap = oj2_c1.get(i);
            String itemCode = imap.get("itemCode");
            queryFactory.delete(itembasket)
                    .where(itembasket.userid.eq(userId)
                            .and(itembasket.itemcode.eq(itemCode)))
                    .execute();
        }
    }

    // 장바구니의 모든 아이템을 지운다.
    @Transactional
    @DeleteMapping("/api/contractBasket/contractBasketListDeleteAll")
    public void contractBasketListDeleteAll(@RequestParam String bizNo) {
        queryFactory.delete(itembasket)
                .where(itembasket.userid.eq(bizNo))
                .execute();
    }

    // 장바구니의 모든 데이터를 주문을 넣는다.
    @Transactional
    @PostMapping("/api/contractBasket/putSelectedContractBasket")
    public void putSelectedContractBasket(@RequestBody Map<String,Object> itembasketList
                                        ) throws Exception {

        Object oj1 = itembasketList.get("params");
        Map<String, String> oj1_c1 = (Map<String, String>) oj1;
        System.out.println(oj1_c1);
        String custCode = (oj1_c1.get("custCode"));
        String custName = (oj1_c1.get("custName"));
        String smsNumer = (oj1_c1.get("smsNumber"));
        Object oj2 = oj1_c1.get("rowData");
        List<Map<String, String >> oj2_c1 = (List<Map<String, String>>) oj2;


        // 채번 가져오는 프로시저
        StoredProcedureQuery storedProcedure = em.createNamedStoredProcedureQuery("findOrderNo");
        storedProcedure.setParameter("seqId", "ORD");
        storedProcedure.setParameter("regUser", "admin");
        storedProcedure.setParameter("modIp", "");
        storedProcedure.execute();
        String orderNo = (String) storedProcedure.getOutputParameterValue("rtnVal");

        Orderinfo orderInfo = new Orderinfo();

        for (int i = 0; i < oj2_c1.size(); i++) {
            Orderdetail orderdetail = new Orderdetail();
            Map<String, String> imap = oj2_c1.get(i);
            String userid = imap.get("userid");
            String itemCode = imap.get("itemCode");
            String qty_s = String.valueOf(imap.get("qty"));
            BigDecimal qty = new BigDecimal(qty_s);
            String dan_s = String.valueOf(imap.get("dan"));
            BigDecimal dan = new BigDecimal(dan_s);
            String vat_s = String.valueOf(imap.get("surtax"));
            BigDecimal vat = new BigDecimal(vat_s);
            BigDecimal amount = BigDecimal.valueOf(qty.intValue() * dan.intValue());
            String spec = imap.get("spec");
            if (i == 0) {
                // orderifno 테이블 Insert
                orderInfo.setOrderno(orderNo);
                orderInfo.setOrddate(new Date());
                orderInfo.setCustcode(custCode);
                orderInfo.setOrdercust("");
                orderInfo.setOrdtype("WEB");
                orderInfo.setDealtype("001");
                orderInfo.setOrdertel(smsNumer);
                orderInfo.setConfirmYn("N");
                orderInfo.setFinishYn("N");
                orderInfo.setReguser(userid);
                orderInfo.setRegdate(new Date());
                orderInfo.setModuser(userid);
                orderInfo.setModdate(new Date());
                em.persist(orderInfo);
            }
            // orderdetail 테이블 Insert
            orderdetail.setOrderno(orderNo);
            orderdetail.setOrdseq(i+1);
            orderdetail.setItemcode(itemCode);
            orderdetail.setOrdqty(qty);
            orderdetail.setDan(dan);
            orderdetail.setVat(vat);
            orderdetail.setAmount(amount);
            orderdetail.setReguser(userid);
            orderdetail.setRegdate(new Date());
            orderdetail.setModuser(userid);
            orderdetail.setModdate(new Date());
            em.persist(orderdetail);
        }

    }
    
    // 토탈 금액을 계산하는 함수
    @GetMapping("/api/contractBasket/getTotalBill")
    public Map<String, Object> getTotalBill (@RequestParam String userId){
        //  사용자 정보 들고오기
        Custinfo custinfo = custinfoRepository.findByIdNative(userId);

        // 장바구니 정보 들고오기
        List<Tuple> tuple = queryFactory
                .select(
                        itembasket.userid,
                        itembasket.itemcode,
                        iteminfo.itemname,
                        iteminfo.spec,
                        iteminfo.unit,
                        iteminfo.dan,
                        itembasket.qty,
                        iteminfo.vatyn
                )
                .from(itembasket)
                .leftJoin(iteminfo).on(itembasket.itemcode.eq(iteminfo.itemcode))
                // .leftJoin(itemprice).on(itembasket.itemcode.eq(itemprice.itemcode))
                .where(itembasket.userid.eq(userId))
                .fetch();
//        System.out.println(tuple);
//        System.out.println(custinfo.getCreditlimit());

        Map<String, Object> resArr = new HashMap<>();
        BigDecimal totalQtyDan = new BigDecimal("0");
        BigDecimal bdDanQty = new BigDecimal(0);
        BigDecimal bdVat = new BigDecimal(0);
        BigDecimal bdVatQty = new BigDecimal(0);
        BigDecimal totalVat = new BigDecimal(0);
        BigDecimal vatRatio = new BigDecimal("0.1");
        BigDecimal totalBill = new BigDecimal(0);
        BigDecimal creditLimit = null;
        // 여신한도를 들고온다.
        if (custinfo.getCreditlimit() != null){
            creditLimit = new BigDecimal(String.valueOf(custinfo.getCreditlimit()));
        }
        else {
            creditLimit = new BigDecimal("0.00");
        // 부가세, 단가 총단가를 계산한다.
        }
        for (Tuple row: tuple){
            BigDecimal bdDan = new BigDecimal(row.get(iteminfo.dan).toBigInteger());
            bdDanQty = bdDan.multiply(row.get(itembasket.qty));
            totalQtyDan = totalQtyDan.add(bdDanQty);
            if (Objects.equals(row.get(iteminfo.vatyn), "Y")){
                bdVat = bdDan.multiply(vatRatio);
            }
            bdVatQty = bdVat.multiply(row.get(itembasket.qty));
            totalVat = totalVat.add(bdVatQty);
        }

        totalBill = totalQtyDan.add(totalVat);
//        applyCredit =
//        System.out.println("단가합계 : " + totalQtyDan);
//        System.out.println("vat합계 : " + totalVat);
//        System.out.println("총합계 : " + totalBill);

        BigDecimal nowCustCredit =  getNowCustCredit(userId);

        resArr.put("totalDan", totalQtyDan);
        resArr.put("totalVat", totalVat);
        resArr.put("totalBill", totalBill);
        resArr.put("creditLimit", creditLimit);
        
        // 처리 필요
        resArr.put("afterContractCredit", creditLimit.subtract(totalBill));


        return resArr;
    }

    // 사용자 현재 여신 상태 계산하여 들고오기
    public BigDecimal getNowCustCredit(String userId){
        BigDecimal resCredit = new BigDecimal(0);
        Custinfo custinfo = custinfoRepository.findByIdNative(userId);
        BigDecimal creditLimit = null;
        if (custinfo.getCreditlimit() != null){
            creditLimit = new BigDecimal(String.valueOf(custinfo.getCreditlimit()));
        }
        else {
            creditLimit = new BigDecimal("0.00");
        }
        String custCode = custinfo.getCustcode();
        List<Orderinfo> orderinfo = orderinfoRepository.findAllByCustcode(custCode);

        List<String> orderNoList = new ArrayList<>();
        for (Orderinfo value : orderinfo) {
            orderNoList.add(value.getOrderno());
        }

        // orderNoList에 따라서 계산하여 리런해준다.

        return resCredit;
    }
}
