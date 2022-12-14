package miraeinfo.scmSystem.controller.order;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.entity.QCodedet;
import miraeinfo.scmSystem.repository.BuyorderdRepository;
import miraeinfo.scmSystem.repository.BuyordermRepository;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.SalesmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static miraeinfo.scmSystem.entity.QBuyorderd.buyorderd;
import static miraeinfo.scmSystem.entity.QBuyorderm.buyorderm;

import static miraeinfo.scmSystem.entity.QCompinfo.compinfo;
import static miraeinfo.scmSystem.entity.QCustinfo.custinfo;
import static miraeinfo.scmSystem.entity.QInorderd.inorderd;
import static miraeinfo.scmSystem.entity.QInorderm.inorderm;
import static miraeinfo.scmSystem.entity.QIteminfo.iteminfo;

@RestController
public class OrderController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    IteminfoRepository iteminfoRepository;

    @Autowired
    BuyordermRepository buyordermRepository;

    @Autowired
    BuyorderdRepository buyorderdRepository;

    @Autowired
    SalesmRepository salesmRepository;

    public OrderController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @GetMapping("/api/order/getMontlySales")
    public List<Object> getMontlySales(@RequestParam String bizNo, String custCode) {

        List<Object> getSales = null;

        StoredProcedureQuery storedProcedure = em.createNamedStoredProcedureQuery("montlySales");
        storedProcedure.setParameter("BIZNO", bizNo);
        storedProcedure.setParameter("CUSTCODE", custCode);
        storedProcedure.execute();

        getSales = storedProcedure.getResultList();

        return getSales;
    }

    // 자재발주 리스트를 불러오는 함수
    @GetMapping("/api/order/getCheckOrder")
    public List<Map<String,Object>> getCheckOrder (@RequestParam String id,
                                                   @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                                   @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                                   String type1,
                                                   String type2,
                                                   String type3,
                                                   String itemName,
                                                   String buySts){

        /*
            프로시저를 사용한 이유는 소계를 뽑아오기 위함인데
            QueryDSL에선 GroupBy를 지원하지만
            Group By Rollup은 지원하지 않기때문
            해당 프로시저에 대한 정의는
            Buyorderm 엔티티 상단에 있음.
         */
        StoredProcedureQuery storedProcedure = em.createNamedStoredProcedureQuery("findBuyOrderList");
        // 나중에 파라미터로 받아온 id값 넣고 프로시저에서 Where문에 주석 풀기
        storedProcedure.setParameter("USERID", id);
        storedProcedure.setParameter("FROMDATE", fromDate);
        storedProcedure.setParameter("TODATE", toDate);
        storedProcedure.setParameter("TYPE1", type1);
        storedProcedure.setParameter("TYPE2", type2);
        storedProcedure.setParameter("TYPE3", type3);
        storedProcedure.setParameter("ITEMNAME", itemName);
        storedProcedure.setParameter("BUYSTS", buySts);
        storedProcedure.execute();

        List<Object[]> resultList = storedProcedure.getResultList();
        List<Map<String,Object>> checkOrderArr = new ArrayList<>();
        String showBuyCode = "";
        int rowNo = 0;
        int buyCodeTotal = 0;
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        for (int i = 0; i < resultList.size(); i++) {
            Object checkOrder = resultList.get(i);
            Object[] result = (Object[]) checkOrder;
            Map<String,Object> checkOrderMap = new HashMap<String, Object>();
            String BuyYear = sdf.format(result[0]).substring(0,4);
            if (BuyYear.equals("1900")) {
                checkOrderMap.put("rowNo", buyCodeTotal+"건");
                checkOrderMap.put("buyDate", "");
                checkOrderMap.put("inDate", "");
                rowNo--;
                buyCodeTotal = 0;
            }
            else {
                checkOrderMap.put("rowNo", i + 1 + rowNo);
                String buyDate = sdf.format(result[0]);
                checkOrderMap.put("buyDate", buyDate);
                String inDate = sdf.format(result[1]);
                checkOrderMap.put("inDate", inDate);
                buyCodeTotal++;
            }

            checkOrderMap.put("itemCode", result[2]);
            checkOrderMap.put("itemName", result[3]);
            checkOrderMap.put("spec", result[4]);
            checkOrderMap.put("brand", result[5]);
            if (result[6].equals("소계")) {
                checkOrderMap.put("dan", "");
            }
            else {
                checkOrderMap.put("dan", result[8]);
            }
            checkOrderMap.put("vat", result[9]);
            if (!showBuyCode.equals(result[6])) {
                showBuyCode = (String) result[6];
                checkOrderMap.put("showBuyCode", showBuyCode);
            } else {
                checkOrderMap.put("showBuyCode", "");
            }
            checkOrderMap.put("buyCode", result[6]);
            checkOrderMap.put("unit", result[7]);
            checkOrderMap.put("qty", result[11]);
            checkOrderMap.put("amount", result[12]);
            checkOrderMap.put("sumAmt", result[13]);
            checkOrderMap.put("remark", result[10]);
            checkOrderMap.put("restQty", result[14]);
            checkOrderArr.add(checkOrderMap);
        }
        return checkOrderArr;
    }

    @GetMapping("/api/order/getPurchaseInfo")
    public List<Map<String, Object>> PurchaseInfo(@RequestParam String id,
                                                  @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                                  @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                                  String type1,
                                                  String type2,
                                                  String type3,
                                                  String itemName,
                                                  String buySts) {

        StoredProcedureQuery storedProcedureQuery = em.createNamedStoredProcedureQuery("purchaseInfo");
        storedProcedureQuery.setParameter("USERID", id);
        storedProcedureQuery.setParameter("FROMDATE", fromDate);
        storedProcedureQuery.setParameter("TODATE", toDate);
        storedProcedureQuery.setParameter("TYPE1", type1);
        storedProcedureQuery.setParameter("TYPE2", type2);
        storedProcedureQuery.setParameter("TYPE3", type3);
        storedProcedureQuery.setParameter("ITEMNAME", itemName);
        storedProcedureQuery.setParameter("BUYSTS", buySts);
        storedProcedureQuery.execute();

        List<Object[]> resultList = storedProcedureQuery.getResultList();
        List<Map<String,Object>> purchaseInfoArr = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            Object purchaseInfo = resultList.get(i);
            Object[] result = (Object[]) purchaseInfo;

            Map<String, Object> purchaseInfoMap = new HashMap<>();
            purchaseInfoMap.put("bizNo", result[0]);
            purchaseInfoMap.put("compName", result[1]);
            purchaseInfoMap.put("ceoName", result[2]);
            purchaseInfoMap.put("address", result[3]);
            purchaseInfoMap.put("compTel", result[4]);
            purchaseInfoMap.put("compFax", result[5]);
            purchaseInfoMap.put("manager", result[6]);
            purchaseInfoMap.put("custName", result[7]);
            purchaseInfoMap.put("custTel", result[8]);
            purchaseInfoMap.put("custFax", result[9]);
            purchaseInfoMap.put("custAddr", result[10]);
            purchaseInfoMap.put("remark", result[11]);
            purchaseInfoMap.put("inDate", result[12]);
            purchaseInfoMap.put("buyCode", result[13]);

            purchaseInfoArr.add(purchaseInfoMap);
        }

        return purchaseInfoArr;
    }

    @GetMapping("/api/order/getPurchaseItem")
    public List<Map<String, Object>> getPurchaseItem(@RequestParam String id,
                                                     @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                                     @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                                     String type1,
                                                     String type2,
                                                     String type3,
                                                     String itemName,
                                                     String buySts) {

        StoredProcedureQuery storedProcedureQuery = em.createNamedStoredProcedureQuery("purchaseItem");
        storedProcedureQuery.setParameter("USERID", id);
        storedProcedureQuery.setParameter("FROMDATE", fromDate);
        storedProcedureQuery.setParameter("TODATE", toDate);
        storedProcedureQuery.setParameter("TYPE1", type1);
        storedProcedureQuery.setParameter("TYPE2", type2);
        storedProcedureQuery.setParameter("TYPE3", type3);
        storedProcedureQuery.setParameter("ITEMNAME", itemName);
        storedProcedureQuery.setParameter("BUYSTS", buySts);
        storedProcedureQuery.execute();

        List<Object[]> resultList = storedProcedureQuery.getResultList();
        List<Map<String,Object>> getPurchaseItemArr = new ArrayList<>();

        for(int i = 0; i < resultList.size(); i++) {
            Object purchaseItem = resultList.get(i);
            Object[] result = (Object[]) purchaseItem;

            Map<String, Object> getPurchaseItemMap = new HashMap<>();
            getPurchaseItemMap.put("rowNo", result[0]);
            getPurchaseItemMap.put("itemName", result[1]);
            getPurchaseItemMap.put("itemQty", result[2]);
            getPurchaseItemMap.put("buyCode", result[3]);
            getPurchaseItemMap.put("sumQty", result[4]);

            getPurchaseItemArr.add(getPurchaseItemMap);
        }

        return getPurchaseItemArr;
    }

    // 납품등록을 하는 함수
    @Transactional
    @PostMapping("/api/order/buyOrderInsert")
    public void buyOrderInsert(@RequestBody Map<String,Object> buyOrderInsert ) throws Exception {
        StoredProcedureQuery storedProcedure = em.createNamedStoredProcedureQuery("InOrderInsert");
        Object reqData = buyOrderInsert.get("params");
        Map<String, Object> reqDataMap = (Map<String, Object>) reqData;
        String bizNo = (String) reqDataMap.get("bizNo");
        String custCode = (String) reqDataMap.get("custCode");
        int inSeq = 1;
        List<Map<String, String >> rowDataList = (List<Map<String, String>>) reqDataMap.get("rowData");
        String inCode = "";
        storedProcedure.setParameter("JOB","M");
        storedProcedure.setParameter("INCODE","");
        storedProcedure.setParameter("INDATE",new Date());
        storedProcedure.setParameter("INTYPE","01");
        storedProcedure.setParameter("INSTS","01");
        storedProcedure.setParameter("EMPCODE","ADMIN");
        storedProcedure.setParameter("REMARK","");
        storedProcedure.setParameter("BUYCODE","");
        storedProcedure.setParameter("ITEMCODE","");
        storedProcedure.setParameter("INSEQ",0);
        storedProcedure.setParameter("STOCKTYPE","");
        storedProcedure.setParameter("MANDATE",new Date());
        storedProcedure.setParameter("SHELFLIFE","");
        storedProcedure.setParameter("INQTY",BigDecimal.ZERO);
        storedProcedure.setParameter("DAN",BigDecimal.ZERO);
        storedProcedure.setParameter("AMOUNT",BigDecimal.ZERO);
        storedProcedure.setParameter("VAT",BigDecimal.ZERO);
        storedProcedure.setParameter("REGUSER","ADMIN");
        storedProcedure.setParameter("ISDELETE","");
        storedProcedure.execute();
        inCode = (String) storedProcedure.getOutputParameterValue("RTNVAL");
        for (int i = 0; i < rowDataList.size(); i++) {
            Map<String, String> rowData = rowDataList.get(i);
            // 납품수량이 입력되어 있고, 소계가 아닌 RowData만 찾아서 Insert
            if (rowData.get("inQty") != null && !rowData.get("buyCode").equals("소계")) {
                storedProcedure = em.createNamedStoredProcedureQuery("InOrderInsert");
                storedProcedure.setParameter("JOB","");
                storedProcedure.setParameter("INCODE",inCode);
                storedProcedure.setParameter("INDATE",new Date(0));
                storedProcedure.setParameter("INTYPE","01");
                storedProcedure.setParameter("INSTS","01");
                storedProcedure.setParameter("EMPCODE","ADMIN");
                storedProcedure.setParameter("REMARK","");
                storedProcedure.setParameter("BUYCODE",rowData.get("buyCode"));
                storedProcedure.setParameter("ITEMCODE",rowData.get("itemCode"));
                storedProcedure.setParameter("INSEQ",inSeq);
                storedProcedure.setParameter("STOCKTYPE","01");
                storedProcedure.setParameter("MANDATE",new Date());
                storedProcedure.setParameter("SHELFLIFE","");
                BigDecimal inQty = new BigDecimal(rowData.get("inQty"));
                storedProcedure.setParameter("INQTY",inQty);
                String dan = String.valueOf(rowData.get("dan"));
                BigDecimal dan_s = new BigDecimal(dan);
                storedProcedure.setParameter("DAN",dan_s);
                String amount = String.valueOf(rowData.get("amount"));
                BigDecimal amount_s = new BigDecimal(amount);
                storedProcedure.setParameter("AMOUNT",amount_s);
                String vat = String.valueOf(rowData.get("vat"));
                BigDecimal vat_s = new BigDecimal(vat);
                storedProcedure.setParameter("VAT",vat_s);
                storedProcedure.setParameter("REGUSER","ADMIN");
                storedProcedure.setParameter("ISDELETE","");
                storedProcedure.execute();
                inSeq++;
            }
        }
    }

    // 자재 입고를 확인하는 함수
    @GetMapping("/api/order/confirmDeliveryItem")
    public List<Map<String, Object>> confirmDeliveryItem (
            @RequestParam String bizNo,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
            String type1,
            String type2,
            String type3,
            String itemName,
            Boolean tradingStatementFlag
    ){
        /*
             하나의 Entity를 여러번 조인하려면 새로운 객체를 생성해줘야함.
         */
        QCodedet codeDet1 = new QCodedet("codeDet1");
        QCodedet codeDet2 = new QCodedet("codeDet2");
        QCodedet codeDet3 = new QCodedet("codeDet3");
        QCodedet codeDet4 = new QCodedet("codeDet4");
        List<Tuple> confirmDeliveryList = queryFactory.select(
                inorderm.incode,
                codeDet1.dcodename,
                inorderd.buycode,
                iteminfo.itemcode,
                iteminfo.itemname,
                iteminfo.spec,
                iteminfo.brand,
                inorderd.shelflife,
                inorderd.mandate,
                inorderd.regdate,
                buyorderd.qty,
                inorderd.inqty,
                inorderd.dan,
                inorderd.amount,
                inorderd.vat,
                // 서브쿼리 사용은 JPAExpressions 넣어주기
                JPAExpressions.select(
                        buyorderd.qty.subtract(inorderd.inqty.sum()))
                        .from(inorderd)
                        .where(buyorderd.buycode.eq(inorderd.buycode)
                                .and(buyorderd.itemcode.eq(inorderd.itemcode))))
                .from(inorderm)
                .leftJoin(inorderd).on(inorderm.incode.eq(inorderd.incode))
                .leftJoin(iteminfo).on(inorderd.itemcode.eq(iteminfo.itemcode))
                .leftJoin(buyorderd).on(inorderd.buycode.eq(buyorderd.buycode)
                        .and(inorderd.itemcode.eq(buyorderd.itemcode)))
                .leftJoin(buyorderm).on(buyorderd.buycode.eq(buyorderm.buycode))
                .leftJoin(custinfo).on(buyorderm.custcode.eq(custinfo.custcode))
                .leftJoin(codeDet1).on(codeDet1.mcode.eq("INSTS").and(inorderm.insts.eq(codeDet1.dcode)))
                .leftJoin(codeDet2).on(codeDet2.mcode.eq("TYPE1").and(iteminfo.TYPE1.eq(codeDet2.dcode)))
                .leftJoin(codeDet3).on(codeDet2.mcode.eq("TYPE2").and(iteminfo.TYPE1.eq(codeDet3.dcode)))
                .leftJoin(codeDet4).on(codeDet2.mcode.eq("TYPE3").and(iteminfo.TYPE1.eq(codeDet4.dcode)))
                .where(inorderm.indate.goe(fromDate)
                        /*
                            나중에 거래처별 조회할떄 윗줄을 지우고 주석풀기
                            custinfo.id.eq()안에 파라미터로 받아온 ID값 집어넣기
                            custinfo.id.eq(id)
                            .and(inorderm.indate.goe(fromDate))
                         */
                        .and(inorderm.indate.loe(toDate))
                        .and(itemNameContains(itemName))
                        .and(TYPE1Eq(type1, codeDet2))
                        .and(TYPE2Eq(type2, codeDet3))
                        .and(TYPE3Eq(type3, codeDet4))
                        .and(tradingStatementEq(codeDet1, tradingStatementFlag))
                )
                .fetch();

        List<Map<String,Object>> confirmDeliveryArr = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd");
        int rowNo = 1;
        for (Tuple row : confirmDeliveryList) {
            Map<String,Object> confirmDeliveryMap = new HashMap<String, Object>();
            confirmDeliveryMap.put("rowNo", rowNo);
            confirmDeliveryMap.put("buyCode", row.get(inorderd.buycode));
            confirmDeliveryMap.put("itemCode", row.get(iteminfo.itemcode));
            confirmDeliveryMap.put("itemName", row.get(iteminfo.itemname));
            confirmDeliveryMap.put("brand", row.get(iteminfo.brand));
            confirmDeliveryMap.put("shelfLife", row.get(inorderd.shelflife));
            String manDate = sdf.format(row.get(inorderd.mandate));
            confirmDeliveryMap.put("manDate", manDate);
            confirmDeliveryMap.put("qty", row.get(buyorderd.qty));
            // 서브쿼리 결과물 사용
            confirmDeliveryMap.put("restQty", row.get(JPAExpressions.select(
                            buyorderd.qty.subtract(inorderd.inqty.sum()))
                    .from(inorderd)
                    .where(buyorderd.buycode.eq(inorderd.buycode)
                            .and(buyorderd.itemcode.eq(inorderd.itemcode)))));
            confirmDeliveryMap.put("inCode", row.get(inorderm.incode));
            confirmDeliveryMap.put("inQty", row.get(inorderd.inqty));
            confirmDeliveryMap.put("dan", row.get(inorderd.dan));
            confirmDeliveryMap.put("amount", row.get(inorderd.amount));
            confirmDeliveryMap.put("vat", row.get(inorderd.vat));
            BigDecimal amount = row.get(inorderd.amount);
            BigDecimal vat = row.get(inorderd.vat);
            BigDecimal sumAmt = amount.add(vat);
            confirmDeliveryMap.put("sumAmt", sumAmt);
            confirmDeliveryMap.put("inSts", row.get(codeDet1.dcodename));
            rowNo++;
            confirmDeliveryArr.add(confirmDeliveryMap);
        }
        return confirmDeliveryArr;
    }



    // 거래명세서
    @GetMapping("/api/order/getTradingStatementInfo")
    public List<Map<String, Object>> getTradingStatement (@RequestParam String id,
                                                          @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                                          @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                                          String type1,
                                                          String type2,
                                                          String type3,
                                                          String itemName,
                                                          String inCode){

        StoredProcedureQuery storedProcedureQuery = em.createNamedStoredProcedureQuery("P_TRADEINFO_Q");
        storedProcedureQuery.setParameter("USERID", id);
        storedProcedureQuery.setParameter("FROMDATE", fromDate);
        storedProcedureQuery.setParameter("TODATE", toDate);
        storedProcedureQuery.setParameter("TYPE1", type1);
        storedProcedureQuery.setParameter("TYPE2", type2);
        storedProcedureQuery.setParameter("TYPE3", type3);
        storedProcedureQuery.setParameter("ITEMNAME", itemName);
        storedProcedureQuery.setParameter("INCODE", inCode);

        List<Object[]> resultList = storedProcedureQuery.getResultList();
        List<Map<String,Object>> purchaseInfoArr = new ArrayList<>();

        for (int i = 0; i < resultList.size(); i++) {
            Object tradingInfo = resultList.get(i);
            Object[] result = (Object[]) tradingInfo;

            Map<String, Object> tradingInfoMap = new HashMap<>();
            tradingInfoMap.put("rowNo", result[0]);

        }

        return null;
    }

    // 매출실적
    @GetMapping("/api/order/getSalesPerformance")
    public List<Map<String, Object>> getSalesPerformance (
            @RequestParam String bizNo,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
            @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate
    ){
        List<Tuple> tuple = queryFactory
                .select(
                        inorderd.itemcode,
                        iteminfo.itemname,
                        iteminfo.brand,
                        inorderd.inqty.sum(),
                        inorderd.dan,
                        inorderd.amount.sum(),
                        inorderd.vat.sum()
                )
                .from(inorderm)
                .leftJoin(inorderd).on(inorderm.incode.eq(inorderd.incode))
                .leftJoin(iteminfo).on(inorderd.itemcode.eq(iteminfo.itemcode))
                .leftJoin(buyorderd).on(inorderd.buycode.eq(buyorderd.buycode)
                        .and(inorderd.itemcode.eq(buyorderd.itemcode)))
                .leftJoin(buyorderm).on(buyorderd.buycode.eq(buyorderm.buycode))
                .leftJoin(custinfo).on(buyorderm.custcode.eq(custinfo.custcode))
                .where(inorderm.indate.goe(fromDate)
                        /*
                           나중에 거래처별 조회할떄 윗줄을 지우고 주석풀기
                            custinfo.id.eq()안에 파라미터로 받아온 ID값 집어넣기
                            custinfo.id.eq(id)
                            .and(inorderm.indate.goe(fromDate))
                        */
                        .and(inorderm.indate.loe(toDate))
                        // .and(inorderm.insts.eq("03")) 입고상태가 완료인것
                )
                .groupBy(inorderd.itemcode,
                        iteminfo.itemname,
                        iteminfo.brand,
                        inorderd.dan
                        )
                .fetch();

        List<Map<String, Object>> salesPerformanceArr = new ArrayList<>();
        int rowNo = 1;
        for (Tuple row : tuple) {
            Map<String, Object> salesPerformanceMap = new HashMap<>();
            salesPerformanceMap.put("rowNo", rowNo);
            salesPerformanceMap.put("salCode", row.get(inorderd.itemcode));
            salesPerformanceMap.put("itemName", row.get(iteminfo.itemname));
            salesPerformanceMap.put("brand", row.get(iteminfo.brand));
            salesPerformanceMap.put("inQty", row.get(inorderd.inqty.sum()));
            salesPerformanceMap.put("inDan", row.get(inorderd.dan));

            salesPerformanceMap.put("inAmount", row.get(inorderd.amount.sum()));
            salesPerformanceMap.put("inVat", row.get(inorderd.vat.sum()));
            BigDecimal inAmount = row.get(inorderd.amount.sum());
            BigDecimal inVat = row.get(inorderd.vat.sum());
            salesPerformanceMap.put("inPrice", inAmount.add(inVat));
            salesPerformanceMap.put("taxAmount", row.get(inorderd.amount.sum()));
            salesPerformanceMap.put("taxVat", row.get(inorderd.vat.sum()));
            salesPerformanceMap.put("taxPrice", inAmount.add(inVat));
            salesPerformanceArr.add(salesPerformanceMap);
            rowNo++;
        }

        return salesPerformanceArr;
    }
    /*
       BooleanExpression을 이용한 동적쿼리
       값이 안넘어왔을땐 Where문에 추가하지않음.
    */
    private BooleanExpression TYPE1Eq(String type1, QCodedet codeDet2) {
        return type1 != "" ? codeDet2.mcode.eq("TYPE1").and(iteminfo.TYPE1.eq(type1)) : null;
    }
    private BooleanExpression TYPE2Eq(String type2, QCodedet codeDet3) {
        return type2 != "" ? codeDet3.mcode.eq("TYPE2").and(iteminfo.TYPE2.eq(type2)) : null;
    }
    private BooleanExpression TYPE3Eq(String type3, QCodedet codeDet4) {
        return type3 != "" ? codeDet4.mcode.eq("TYPE3").and(iteminfo.TYPE3.eq(type3)) : null;
    }
    private BooleanExpression itemNameContains(String itemName) {
        return itemName != "" ? iteminfo.itemname.contains(itemName) : null;
    }
    private BooleanExpression tradingStatementEq(QCodedet codeDet1,Boolean tradingStatementFlag) {
        return tradingStatementFlag == true ? codeDet1.dcodename.eq("입하") : null;
    }

}
