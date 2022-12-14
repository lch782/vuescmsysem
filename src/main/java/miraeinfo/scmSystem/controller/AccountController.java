package miraeinfo.scmSystem.controller;

import io.jsonwebtoken.Claims;
import miraeinfo.scmSystem.entity.Custinfo;
import miraeinfo.scmSystem.repository.CustinfoRepository;
import miraeinfo.scmSystem.service.JwtService;
// import miraeinfo.scmSystem.service.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@RestController
public class AccountController {

    @Autowired
    CustinfoRepository custinfoRepository;
    @Autowired
    JwtService jwtService;

    @PostMapping("/api/account/login")
    public ResponseEntity login(@RequestBody Map<String, String> params,
                                HttpServletResponse res) {
//        String castUserId = castingBizno(params.get("bizno"));

        Custinfo custinfo = custinfoRepository.findByIdAndPassword(params.get("userId"), params.get("password"));

        if (custinfo != null) {
         // JwtService jwtService = new JwtServiceImpl();
            String bizno = custinfo.getBizno();
            String custType = custinfo.getCusttype();
            String custCode = custinfo.getCustcode();
            String custName = custinfo.getCustname();
            String custAddress = custinfo.getAddress();
            String companyCode = custinfo.getCompcode();
            String userId = custinfo.getId();

            Map<String, String> custInfo = new HashMap<>();
            custInfo.put("bizno", bizno);
            custInfo.put("custType", custType);
            custInfo.put("custCode", custCode);
            custInfo.put("custName", custName);
            custInfo.put("custAddress", custAddress);
            custInfo.put("companyCode", companyCode);
            custInfo.put("userId", userId);

            String token = jwtService.getToken("bizno", bizno);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");

            res.addCookie(cookie);
            return new ResponseEntity<>(custInfo, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/api/account/check")
    public ResponseEntity check(@CookieValue(value = "token", required = false) String token,
                                HttpServletResponse res){
         Claims claims = jwtService.getClaims(token);
         if (claims != null) {
             String bizno = (String) claims.get("bizno");
             Custinfo custinfo = custinfoRepository.findByBizno(bizno);

             Map<String, String> custInfo = null;
             if (custinfo != null) {
                 String custType = custinfo.getCusttype();
                 String custCode = custinfo.getCustcode();
                 String custName = custinfo.getCustname();
                 String custAddress = custinfo.getAddress();
                 String companyCode = custinfo.getCompcode();
                 String userId = custinfo.getId();

                 custInfo = new HashMap<>();
                 custInfo.put("bizno", bizno);
                 custInfo.put("custType", custType);
                 custInfo.put("custCode", custCode);
                 custInfo.put("custName", custName);
                 custInfo.put("custAddress", custAddress);
                 custInfo.put("companyCode", companyCode);
                 custInfo.put("userId", userId);
             }
             String newToken = jwtService.getToken("bizno", bizno);
             Cookie cookie = new Cookie("token", newToken);
             cookie.setHttpOnly(true);
             cookie.setPath("/");

             res.addCookie(cookie);

             return new ResponseEntity<>(custInfo, HttpStatus.OK);
         }

         return new ResponseEntity(null, HttpStatus.OK);
    }

    @GetMapping("/api/account/custInfo")
    public Custinfo getCustInfo(@RequestParam String userId){
        Custinfo custinfo = custinfoRepository.findByIdNative(userId);
        return custinfo;
    }

    // 사용자 사업자 번호를 마스킹 하는 함수
    private String castingBizno(String bizno) {
        String castBizno = null;

        if (bizno.length() != 10){
            return "error";
        }

        String bizno1 = bizno.substring(0, 3);
        String bizno2 = bizno.substring(3, 5);
        String bizno3 = bizno.substring(5);
        String maskStr = "-";

        castBizno = bizno1 + maskStr + bizno2 + maskStr + bizno3;
        return castBizno;
    }
}
