package miraeinfo.scmSystem.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.crypto.Data;
import java.security.Key;
import java.util.Date;

@Service
public class SecurityService {
    private static final String SECERET_KEY = "DFDFKJFK4jkldfjdslkjflajt5l4;kj6l;3j5lj543l5;j34l5jl3;4j53l4;k6jl56l35rn4l5n;4k35j4";

    public String createToken (String subject, long expTime){

        SignatureAlgorithm  signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] seceretKeyBytes = DatatypeConverter.parseBase64Binary(SECERET_KEY);
        Key signingKey = new SecretKeySpec(seceretKeyBytes, signatureAlgorithm.getJcaName());

        return Jwts.builder()
                .setSubject(subject)
                .signWith(signingKey, signatureAlgorithm)
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                .compact();
    }

    public String getSubject (String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECERET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
