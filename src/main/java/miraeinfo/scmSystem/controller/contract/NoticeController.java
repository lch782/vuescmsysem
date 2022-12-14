package miraeinfo.scmSystem.controller.contract;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.Getter;
import lombok.Setter;
import miraeinfo.scmSystem.entity.Notice;
import miraeinfo.scmSystem.entity.QNotice;
import miraeinfo.scmSystem.repository.NoticeRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static miraeinfo.scmSystem.entity.QNotice.notice;

@RestController
@Getter
@Setter
public class NoticeController {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Autowired
    NoticeRepository noticeRepository;

    public NoticeController(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @GetMapping("/api/home/getHomeNotice")
    public List<Notice> getHomeNotice (@RequestParam String searchGroup) {
        List<Notice> notice = noticeRepository.findAllByNumseqGreaterThanAndCompcodeEqualsOrderByNumseqAsc(0, searchGroup);
        return notice;
    }

    @GetMapping("/api/notice/getNoticeList")
    public List<Map<String,Object>> getNoticeList (@RequestParam int page,
                                       @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                       @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                       String searchCondition,
                                       String searchWord,
                                       String searchGroup,
                                       String companyCode) {

        /*
        Expressions.dateTimeTemplate(Date.class,"CONVERT(varchar(10),{0},23)"
        Datetime형식을 Date로 변경 MSSQL 문법 "CONVERT(varchar(10),{0},23)"를 이용하여 형변환
         */
        List<Tuple> resNotice = queryFactory
                .select(
                        notice.seq,
                        notice.numseq,
                        new CaseBuilder()
                                .when(notice.numseq.eq(0)).then("-")
                                .otherwise((notice.numseq.stringValue()).trim())
                                .as("numseq_case"),
                        notice.compcode,
                        notice.title,
                        notice.summary,
                        notice.contents,
                        notice.selqty,
                        notice.imgpatch,
                        notice.imgname,
                        notice.reguser,
                        notice.regdate,
                        notice.regip,
                        notice.moduser,
                        notice.moddate,
                        notice.modip)
                .from(notice)
                .where(Expressions.dateTimeTemplate(Date.class,"CONVERT(varchar(10),{0},23)",notice.regdate).goe(fromDate)
                    .and(Expressions.dateTimeTemplate(Date.class,"CONVERT(varchar(10),{0},23)",notice.regdate).loe(toDate))
                        .and(searchMethod(searchWord, searchCondition))
                        .and(notice.compcode.contains(searchGroup))
                )
                .offset((page-1)*10)
                .orderBy(notice.seq.desc())
                .limit(10)
                .fetch();

//        System.out.println(resNotice);

        List<Map<String,Object>> searchNoticeArr = new ArrayList<>();
        for(Tuple row: resNotice) {
            Map<String,Object> searchNoticeMap = new HashMap<String, Object>();
            searchNoticeMap.put("seq", row.get(notice.seq));
            searchNoticeMap.put("numseq", row.get(notice.numseq));
            searchNoticeMap.put("numseq_case", row.toArray()[2]);
            searchNoticeMap.put("compcode", row.get(notice.compcode));
            searchNoticeMap.put("title", row.get(notice.title));
            searchNoticeMap.put("summary", row.get(notice.summary));
            searchNoticeMap.put("contents", row.get(notice.contents));
            searchNoticeMap.put("selqty", row.get(notice.selqty));
            searchNoticeMap.put("imgpatch", row.get(notice.imgpatch));
            searchNoticeMap.put("imgname", row.get(notice.imgname));
            searchNoticeMap.put("reguser", row.get(notice.reguser));
            searchNoticeMap.put("regdate", row.get(notice.regdate));
            searchNoticeMap.put("regip", row.get(notice.regip));
            searchNoticeMap.put("moduser", row.get(notice.moduser));
            searchNoticeMap.put("moddate", row.get(notice.moddate));
            searchNoticeMap.put("modip", row.get(notice.modip));

            searchNoticeArr.add(searchNoticeMap);
        }

        return searchNoticeArr;
    }

    @GetMapping("/api/notice/getNoticeListPage")
    public long getNoticeListPage (@RequestParam String searchWord,
                                   String searchCondition,
                                   @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDate,
                                   @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate) {
        long listCount = 0;
        long pageCount = 0;

        listCount = queryFactory
                .select(notice.count())
                .from(notice)
                .where(Expressions.dateTimeTemplate(Date.class,"CONVERT(varchar(10),{0},23)",notice.regdate).goe(fromDate)
                        .and(Expressions.dateTimeTemplate(Date.class,"CONVERT(varchar(10),{0},23)",notice.regdate).loe(toDate))
                        .and(searchMethod(searchWord, searchCondition)))
                .fetchOne();

        if (listCount % 10 != 0) {
            pageCount = (listCount/10)+1;
        } else {
            pageCount = listCount/10;
        }

        return pageCount;
    }

    @Transactional
    @PostMapping("/api/notice/createNotice")
    public void createNotice (@RequestBody Map<String, Object> noticeInfo, HttpServletRequest request) {

        Object noticeCreate = noticeInfo.get("params");
        Map<String, String> noticeMap = (Map<String, String>) noticeCreate;

        String port = noticeMap.get("port");

        Notice notice = new Notice();
        String getCompcode = String.valueOf(noticeMap.get("compcode"));
        getCompcode = getCompcode.replace("[", "");
        getCompcode = getCompcode.replace("]", "");
        getCompcode = getCompcode.replace(" ", "");

        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();

        notice.setTitle(noticeMap.get("title"));
        notice.setSummary(String.valueOf(noticeMap.get("summary")));
        notice.setContents(noticeMap.get("content"));
        int numSeq = 0;
        if (Objects.equals(noticeMap.get("numseq"), "0")){
            numSeq = 0;
        }
        else if (noticeMap.get("numseq") != null && Integer.parseInt(String.valueOf(noticeMap.get("numseq"))) > 0){
            numSeq = Integer.parseInt(String.valueOf(noticeMap.get("numseq")));
        }
        notice.setNumseq(numSeq);
        notice.setSelqty(0);
        notice.setRegdate(new Date());
        notice.setModdate(new Date());
        notice.setImgname(noticeMap.get("imgname"));
        String imgPath = scheme + serverName + ":" + port + "/img/notice/main/" + noticeMap.get("imgname");
        notice.setImgpatch(imgPath);
        notice.setReguser(noticeMap.get("userId"));

        if (getCompcode.contains(",")) {
            String[] arrayCompcode;
            arrayCompcode = getCompcode.split(",");
            for(int i=0; i<arrayCompcode.length; i++){
                Notice noticeMulti = new Notice();

                noticeMulti.setTitle(noticeMap.get("title"));
                noticeMulti.setSummary(String.valueOf(noticeMap.get("summary")));
                noticeMulti.setContents(noticeMap.get("content"));
                noticeMulti.setNumseq(Integer.parseInt(String.valueOf(noticeMap.get("numseq"))));
                noticeMulti.setSelqty(0);
                noticeMulti.setRegdate(new Date());
                noticeMulti.setModdate(new Date());
                noticeMulti.setCompcode(arrayCompcode[i]);
                noticeMulti.setImgname(noticeMap.get("imgname"));
                String imgPathMulti = scheme + serverName + ":" + port + "/img/notice/main/" + noticeMap.get("imgname");
                notice.setImgpatch(imgPathMulti);
                notice.setReguser(noticeMap.get("userId"));

                em.persist(noticeMulti);
            }
        } else{
            notice.setCompcode(getCompcode);

            em.persist(notice);
        }

    }

    @GetMapping("/api/notice/getNoticeContent")
    public List<Notice> getNoticeContent (@RequestParam int seq) {

        List<Notice> getDetail = null;

        StoredProcedureQuery storedProcedure = em.createNamedStoredProcedureQuery("selectNoticeInfo");
        storedProcedure.setParameter("SEQ", seq);
        storedProcedure.execute();

        getDetail = storedProcedure.getResultList();

        return getDetail;
    }

    @Transactional
    @PatchMapping("/api/notice/updateNoticeContent")
    public void updateNoticeContent(@RequestBody Map<String, Object> noticeInfo, HttpServletRequest request) {
        Object noticeUpdate = noticeInfo.get("params");
        Map<String, String> noticeUpdateMap = (Map<String, String>) noticeUpdate;

        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();
        String serverPort = (request.getServerPort() == 80) ? "" : ":" + request.getServerPort();

        Notice notice = new Notice();

        notice.setSeq(Integer.valueOf(noticeUpdateMap.get("seq")));
        notice.setTitle(noticeUpdateMap.get("title"));
        notice.setSummary(noticeUpdateMap.get("summary"));
        notice.setContents(noticeUpdateMap.get("content"));
        notice.setNumseq(Integer.parseInt(String.valueOf(noticeUpdateMap.get("numseq"))));
        notice.setCompcode(String.valueOf(noticeUpdateMap.get("compcode")));
        notice.setSelqty(Integer.valueOf(String.valueOf(noticeUpdateMap.get("selqty"))));
        notice.setRegdate(new Date());
        notice.setModdate(new Date());
        notice.setImgname(String.valueOf(noticeUpdateMap.get("imgname")));
        String root_path = scheme + serverName + serverPort + "/frontend/public/img/notice/main/";
        String imgPath = scheme + serverName + serverPort + "/img/notice/main/" + noticeUpdateMap.get("imgname");
        notice.setImgpatch(imgPath);

        em.merge(notice);
    }

    @Transactional
    @DeleteMapping("api/notice/deleteNotice")
    public void noticeDelete(@RequestParam int seq){
        queryFactory.delete(notice)
                .where(notice.seq.eq(seq))
                .execute();
    }

    @PostMapping("/api/notice/uploadFile")
    public String uploadFile(@RequestParam MultipartFile file, HttpServletRequest request, String port)  throws IOException {

        String fullPath = "";
        String fileName = null;
        String milName = null;
        String saveName = null;

        String scheme = request.getScheme() + "://";
        String serverName = request.getServerName();

        String root_path = System.getProperty("user.dir") + "/frontend/public/img/notice/";
        String url_path = scheme + serverName + ":" + port + "/img/notice/";


        if (file != null && !file.isEmpty()) {

            fileName = file.getOriginalFilename();
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            String fil11 = fileName.substring(0,fileName.indexOf("."));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            long milliSecond = System.currentTimeMillis();
            milName = String.valueOf(milliSecond).substring(10, 13);

            saveName = (sdf.format(new Date()) + milName  ) + "."+fileExt;
            File ckfile = new File(request + "/" + saveName);

            int i = 0;
            while (ckfile.exists()) {
                i++;
                saveName = ( (sdf.format(new Date()) + milName) ) + i  + "."+fileExt;
                ckfile = new File(request + "/" + saveName);
            }

            try {
                byte[] bytes = file.getBytes();

                File outFile = new File(request + "/" + saveName);

                FileOutputStream fileOutputStream = new FileOutputStream(
                        outFile);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                //
            }
        } else {
            saveName = "not";
        }

        // 파일 저장
        if (!file.isEmpty()) {
            fileName = saveName;
            fullPath = root_path + saveName;
            file.transferTo(new File(fullPath));
        }

        String urlFullPath = url_path + saveName;

        return urlFullPath;
    }

    @PostMapping("/api/notice/uploadMainFile")
    public static String uploadMainFile(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {

        String root_path = System.getProperty("user.dir") + "/frontend/public/img/notice/main/";

        String fullPath = "";
        String fileName = null;
        String milName = null;
        String saveName =null;

        // 파일 이름 변경
        if (file != null && !file.isEmpty()) {

            fileName = file.getOriginalFilename();
            String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            String fil11 = fileName.substring(0,fileName.indexOf("."));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            long milliSecond = System.currentTimeMillis();
            milName = String.valueOf(milliSecond).substring(10, 13);

            saveName = (sdf.format(new Date()) + milName  ) + "."+fileExt;
            File ckfile = new File(request + "/" + saveName);

            int i = 0;
            while (ckfile.exists()) {
                i++;
                saveName = ( (sdf.format(new Date()) + milName) ) + i  + "."+fileExt;
                ckfile = new File(request + "/" + saveName);
            }
            try {
                byte[] bytes = file.getBytes();

                File outFile = new File(request + "/" + saveName);

                FileOutputStream fileOutputStream = new FileOutputStream(
                        outFile);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (IOException e) {
                //
            }
        } else {
            saveName = "not";
        }

        if (!file.isEmpty()) {
            fileName = saveName;
            fullPath = root_path + saveName;
            file.transferTo(new File(fullPath));
        }

        return saveName;
    }

    private BooleanExpression searchMethod(String searchWord, String searchCondition) {
        BooleanExpression result = null;
        if(searchCondition.equals("title") && !searchWord.equals("")) {
            result = notice.title.contains(searchWord);
        } else if(searchCondition.equals("content") && !searchWord.equals("")) {
            result = notice.contents.contains(searchWord);
        }
        return result;
    }
    
    // 공지사항 우선순위를 설정하는 함수
    @Transactional
    @PostMapping("/api/contract/notice/setNoticeNumseq")
    public void setNoticeNumseq(@RequestBody Map<String, Object> param) {
        Object oj1 = param.get("params");
        Map<String, Object> oj1_c1 = (Map<String, Object>) oj1;
        Object oj2 = oj1_c1.get("changedNumseqArr");

        ArrayList<ArrayList<Integer>> oj2_c1 = (ArrayList<ArrayList<Integer>>) oj2;

        for (int i = 0; i < oj2_c1.size(); i++) {
            int numseq = Integer.parseInt(String.valueOf(oj2_c1.get(i).get(0)));
            int seq = Integer.parseInt(String.valueOf(oj2_c1.get(i).get(1)));

            // 업데이트될 글을 찾아서 바꿔준다.
            List<Notice> notice = queryFactory
                    .selectFrom(QNotice.notice)
                            .where(QNotice.notice.seq.eq(seq))
                                    .fetch();
            notice.get(0).setNumseq(numseq);
            em.merge(notice.get(0));

            String compName = notice.get(0).getCompcode();

            // 원래의 글이 있는지 검사하고 있으면 우선순위를 0으로 바꿔준다.
            List<Notice> notices_before = queryFactory
                    .selectFrom(QNotice.notice)
                            .where(QNotice.notice.numseq.eq(numseq)
                                    .and(QNotice.notice.compcode.eq(compName))
                                    .and(QNotice.notice.seq.ne(seq)))
                                    .fetch();
            if (notices_before.size() > 0) {
                notices_before.get(0).setNumseq(0);
                em.merge(notices_before.get(0));
            }
        }
    }

    // 앞서 설정된 우선순위가 있는지 체크하여 리턴해준다.
    @Transactional
    @PostMapping("/api/contract/notice/checkSameNumseq")
    public String checkSameNumseq (@RequestBody Map<String, Object> param) {
        String result = "N";
        Object oj1 = param.get("params");
        Map<String, Object> oj1_c1 = (Map<String, Object>) oj1;
        Object oj2 = oj1_c1.get("changedNumseqArr");

        ArrayList<ArrayList<Integer>> oj2_c1 = (ArrayList<ArrayList<Integer>>) oj2;

        for (int i = 0; i < oj2_c1.size(); i++) {
            int numseq = Integer.parseInt(String.valueOf(oj2_c1.get(i).get(0)));
            int seq = Integer.parseInt(String.valueOf(oj2_c1.get(i).get(1)));

            if (numseq == 0) {
                continue;
            }

            // 업데이트될 글을 찾는다.
            List<Notice> notice = queryFactory
                    .selectFrom(QNotice.notice)
                    .where(QNotice.notice.seq.eq(seq))
                    .fetch();

            String compName = notice.get(0).getCompcode();

            // 원래의 글이 있는지 검사한다.
            List<Notice> notices_before = queryFactory
                    .selectFrom(QNotice.notice)
                    .where(QNotice.notice.numseq.eq(numseq)
                            .and(QNotice.notice.compcode.eq(compName))
                            .and(QNotice.notice.seq.ne(seq)))
                    .fetch();
            if (notices_before.size() > 0) {
                result = "Y";
            }
        }

        return result;
    }
}
