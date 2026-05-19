package es.uv.garcosda.gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.secret:clave-secreta-muy-larga-para-el-proyecto-final-de-triaje-de-urgencias-2024}")
    private String jwtSecret;

    @Value("${jwt.admin.username:admin}")
    private String adminUser;

    @Value("${jwt.admin.password:admin}")
    private String adminPass;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String user = credentials.get("user");
        String pass = credentials.get("pass");

        if (adminUser.equals(user) && adminPass.equals(pass)) {
            String token = generarToken(user, "ADMIN", 15);
            return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
        }
        return ResponseEntity.status(403).body(Map.of("error", "Credenciales invalidas"));
    }

    @PostMapping("/api-key")
    public ResponseEntity<Map<String, String>> apiKey(@RequestBody Map<String, String> request) {
        String deviceId = request.get("deviceId");
        String role = request.get("role");

        if (deviceId == null || role == null || (!role.equals("CAMA") && !role.equals("SENSOR"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "deviceId y role (CAMA/SENSOR) requeridos"));
        }

        String token = generarToken(deviceId, role, 525600);
        return ResponseEntity.ok(Map.of("token", token, "role", role));
    }

    private String generarToken(String sub, String role, long minutos) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(sub)
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(minutos, ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }
}
