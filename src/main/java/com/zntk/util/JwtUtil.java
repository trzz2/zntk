package com.zntk.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JWT utility.
 *
 * A token has three parts: header.payload.signature.
 * The signature is calculated by header + payload + secret.
 */
@Component
public class JwtUtil {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire-minutes}")
    private Long expireMinutes;

    public String generateToken(Long userId, String username, Integer role) {
        try {
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            long now = Instant.now().getEpochSecond();
            long expireAt = now + expireMinutes * 60;

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("userId", userId);
            payload.put("username", username);
            payload.put("role", role);
            payload.put("jti", UUID.randomUUID().toString());
            payload.put("iat", now);
            payload.put("exp", expireAt);

            String encodedHeader = base64UrlEncode(objectMapper.writeValueAsBytes(header));
            String encodedPayload = base64UrlEncode(objectMapper.writeValueAsBytes(payload));
            String content = encodedHeader + "." + encodedPayload;
            String signature = sign(content);

            return content + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Token generate failed");
        }
    }

    public Map<String, Object> parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new RuntimeException("Token format error");
            }

            String content = parts[0] + "." + parts[1];
            String expectedSignature = sign(content);
            if (!expectedSignature.equals(parts[2])) {
                throw new RuntimeException("Token signature error");
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            Map<String, Object> payload = objectMapper.readValue(
                    payloadBytes,
                    new TypeReference<Map<String, Object>>() {
                    }
            );

            long expireAt = Long.parseLong(payload.get("exp").toString());
            if (Instant.now().getEpochSecond() > expireAt) {
                throw new RuntimeException("Token expired");
            }

            return payload;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Token parse failed");
        }
    }

    public long getRemainingSeconds(Map<String, Object> payload) {
        long expireAt = Long.parseLong(payload.get("exp").toString());
        return Math.max(0, expireAt - Instant.now().getEpochSecond());
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM
        );
        mac.init(secretKeySpec);
        return base64UrlEncode(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
