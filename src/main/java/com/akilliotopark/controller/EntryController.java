package com.akilliotopark.controller;

import com.akilliotopark.service.QrTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/entry")
@RequiredArgsConstructor
public class EntryController {

    private final QrTokenService qrTokenService;

    /**
     * ESP32 giriş doğrulama endpoint'i
     * Body: { "qr": "token_string", "plate": "34ABC123" }
     */
    @PostMapping("/verify")
    public Map<String, Object> verifyEntry(@RequestBody Map<String, String> body) {
        String token = body.get("qr");
        String plate = body.get("plate");

        boolean valid = qrTokenService.validateToken(token, plate);

        Map<String, Object> response = new HashMap<>();
        response.put("authorized", valid);
        response.put("message", valid ? "Giriş onaylandı ✅" : "Geçersiz veya süresi dolmuş QR ⛔");

        return response;
    }
}
