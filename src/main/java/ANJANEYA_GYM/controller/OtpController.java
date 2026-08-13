package ANJANEYA_GYM.controller;

import ANJANEYA_GYM.service.OtpService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // =========================================
    // SEND OTP
    // =========================================

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(
            @RequestParam String email) {

        try {

            otpService.sendOtp(email);

            return ResponseEntity.ok("OTP sent successfully");

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("Failed to send OTP: " + e.getMessage());
        }
    }

    // =========================================
    // VERIFY OTP
    // =========================================

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        boolean verified = otpService.verifyOtp(email, otp);

        if (verified) {

            return ResponseEntity.ok(
                    "OTP verified successfully");

        } else {

            return ResponseEntity
                    .badRequest()
                    .body("Invalid or expired OTP");
        }
    }
}