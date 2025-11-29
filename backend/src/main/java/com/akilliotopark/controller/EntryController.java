package com.akilliotopark.controller;

import com.akilliotopark.dto.EntryVerifyRequest;
import com.akilliotopark.service.AsyncLogService;
import com.akilliotopark.service.QrTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/entry")
@RequiredArgsConstructor
public class EntryController {

    private final QrTokenService qrTokenService;
    private final AsyncLogService logService;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyEntry(@RequestBody EntryVerifyRequest request) {
        String token = request.getQr();
        String plate = request.getPlate();

        boolean valid = qrTokenService.validateToken(token, plate);
        if (valid) {
            logService.saveLog(
                    "EntryController",
                    "ENTRY_SUCCESS",
                    "Bariyer Açıldı. Plaka: " + plate,
                    request
            );
        } else {
            logService.saveLog(
                    "EntryController",
                    "SECURITY_ALERT",
                    "Yetkisiz Giriş Denemesi! Plaka: " + plate,
                    request
            );
        }
        Map<String, Object> response = new HashMap<>();
        response.put("authorized", valid);
        response.put("message", valid ? "Giriş onaylandı ✅" : "Geçersiz veya süresi dolmuş QR ⛔");

        return ResponseEntity.ok(response);
    }
}