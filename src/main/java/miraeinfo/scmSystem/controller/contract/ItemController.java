package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import miraeinfo.scmSystem.controller.main.config.ConfigMain;
import miraeinfo.scmSystem.entity.*;
import miraeinfo.scmSystem.repository.IteminfoRepository;
import miraeinfo.scmSystem.repository.IteminfoRepositoryMini;
import miraeinfo.scmSystem.repository.ItempriceRepository;
import miraeinfo.scmSystem.repository.ItemtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

import static miraeinfo.scmSystem.entity.QItembasket.itembasket;
import static miraeinfo.scmSystem.entity.QIteminfo.iteminfo;

@RestController
public class ItemController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    IteminfoRepository iteminfoRepository;
    @Autowired
    IteminfoRepositoryMini iteminfoRepositoryMini;
    @Autowired
    ItemtypeRepository itemtypeRepository;
    @Autowired
    ItempriceRepository itempriceRepository;

    public ItemController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @GetMapping("/api/contract/items")
    public List<IteminfoMini> getItems() {
//        List<Iteminfo> iteminfo = iteminfoRepository.findAll();
        List<IteminfoMini> iteminfoMini = iteminfoRepositoryMini.findAllMini();
        return iteminfoMini;
    }

    @GetMapping("/api/contract/itemsall")
    // 모든 아이템 정보를 가져오는 함수
    public List<Iteminfo> getItemsAll() {
        List<Iteminfo> IteminfoList = queryFactory
                .selectFrom(iteminfo)
                .fetch();
        return IteminfoList;
    }

    @GetMapping("/api/contract/items/search")
    // 아이템 정보를 가져오는 함수
    public List<Iteminfo> getSearchItems(@RequestParam String word, String searchType, String type2, String type3){
        List<Iteminfo> iteminfoList = queryFactory
                .selectFrom(iteminfo)
                .where(searchTypeAndWord(searchType, word)
                        .and(iteminfo.TYPE2.eq(type2))
                        .and(iteminfo.TYPE3.eq(type3)))
                .fetch();

        return iteminfoList;
    }

    @Transactional
    @PostMapping("/api/registContract/putContractBasket")
    public void putContractBasket(@RequestBody Map<String,Object> recontractList ) throws Exception {
        Object oj1 = recontractList.get("params");

        Map<String, String> oj1_c1 = (Map<String, String>) oj1;
        String bizNo = (oj1_c1.get("bizNo"));
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
                    .where(itembasket.userid.eq(bizNo)
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

    private BooleanExpression searchTypeAndWord(String searchType, String word) {
        BooleanExpression result = null;

        if (word == null){
            word = "";
        }

        if (searchType.equals("itemName")) {
            result = iteminfo.itemname.contains(word);
        }
        else if (searchType.equals("itemCode")) {
            result = iteminfo.itemcode.contains(word);
        }
        else if (searchType.equals("spec")) {
            result = iteminfo.spec.contains(word);
        }
        else if (searchType.equals("dan")) {
            BigDecimal dan = new BigDecimal(word);
            result = iteminfo.dan.eq(dan);
        }
        else if (searchType.equals("qty")) {
            BigDecimal qty = new BigDecimal(word);
            result = iteminfo.qty.eq(qty);
        }
        else if (searchType.equals("vat")) {
            BigDecimal errorNum = new BigDecimal("999999999");
            BigDecimal dan = (new BigDecimal(word)).multiply(BigDecimal.valueOf(10));
            BigDecimal vat = new BigDecimal(word);
            if (iteminfo.vatyn != null && iteminfo.vatyn.equals("Y") ) {
                result = iteminfo.dan.eq(dan);
            }
            else {
                result = iteminfo.dan.eq(errorNum);
            }
        }

        return result;
    }

    @GetMapping("/api/contract/items/typeDiv")
    // 아이템의 최상위 분류를 구해준다.
    public HashMap<String , List<Codedet>> getItemTypeDiv(){
        List<Codemast> codemast = queryFactory
                .select(Projections.bean(Codemast.class,
                        QCodemast.codemast.mcode,
                        QCodemast.codemast.mcodename
                ))
                .from(QCodemast.codemast)
                .where(QCodemast.codemast.mcode.like("TYPE%"))
                .fetch();

        HashMap<String , List<Codedet>> typeDivList = new HashMap<>();
        //타입별 분류
        for (Codemast cm : codemast){
               List<Codedet> cd = queryFactory
                    .select(Projections.bean(Codedet.class,
                            QCodedet.codedet.mcode,
                            QCodedet.codedet.dcode,
                            QCodedet.codedet.dcodename
                    ))
                    .from(QCodedet.codedet)
                    .where(QCodedet.codedet.mcode.like(cm.getMcode()))
                    .fetch();
               if (cd != null){
                   typeDivList.put(cm.getMcode(), cd);
               }
        }
        return typeDivList;
    }

    @GetMapping("/api/contract/items/typeDivInfo")
    // 분류 구분의 정보를 가져 오는 함수
    public List<Itemtype> getItemTypeDivInfo(){

        List<Itemtype> itemtype = itemtypeRepository.findAllByNonuseAndWeb("N", "Y");

        return itemtype;
    }

    // 아이템의 가격을 구하는 함수
    @GetMapping("/api/contract/items/getItemPrice")
    public List<Object> getItemPrice(@RequestParam String custCode, String itemcode) {
        List<Object> itemprice = itempriceRepository.findByCustcodeAndItemcode(custCode, itemcode);

        if (!itemprice.isEmpty()){
            return itemprice;
        }
        else if (itemprice.isEmpty()) {
            List<Object> iteminfo = iteminfoRepository.findByItemcode(itemcode);
            return iteminfo;
        }
        return null;
    }

    // 세번째 분류를 구하는 함수
    @GetMapping("/api/contract/getThirdDivType")
    public List<List<Codedet>> getThirdDiv (@RequestParam String middleDiv){

        List<Itemtype> itemtypes = queryFactory
                .selectFrom(QItemtype.itemtype)
                .where(QItemtype.itemtype.TYPE2.eq(middleDiv)
                        .and(QItemtype.itemtype.web.eq("Y")))
                .fetch();

        List<List<Codedet>> resCodedet = new ArrayList<>();
        for (Itemtype itemtype : itemtypes) {
            String type3 = itemtype.getTYPE3();

            List<Codedet> cd = queryFactory
                    .select(Projections.bean(Codedet.class,
                            QCodedet.codedet.mcode,
                            QCodedet.codedet.dcode,
                            QCodedet.codedet.dcodename))
                    .from(QCodedet.codedet)
                    .where(QCodedet.codedet.mcode.eq(ConfigMain.type3)
                            .and(QCodedet.codedet.dcode.eq(type3)))
                    .fetch();
            if (cd != null){
                resCodedet.add(cd);
            }
        }

        return resCodedet;


    }
}
