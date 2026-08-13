package ANJANEYA_GYM.controller;

import ANJANEYA_GYM.dto.LoginRequest;
import ANJANEYA_GYM.dto.RegistrationRequest;
import ANJANEYA_GYM.entity.Member;
import ANJANEYA_GYM.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {


    private final MemberService memberService;


    public MemberController(
            MemberService memberService) {

        this.memberService = memberService;
    }


    // ==========================================
    // REGISTER
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegistrationRequest request) {


        String result =
                memberService.register(request);


        if (result.equals(
                "EMAIL_ALREADY_REGISTERED")) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "EMAIL_ALREADY_REGISTERED"
                    );
        }


        return ResponseEntity.ok(
                "OTP_SENT"
        );
    }


    // ==========================================
    // VERIFY OTP
    // ==========================================

    @PostMapping("/verify")
    public ResponseEntity<String> verify(
            @RequestParam String email,
            @RequestParam String otp) {


        boolean verified =
                memberService.verifyEmail(
                        email,
                        otp
                );


        if (!verified) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        "INVALID_OR_EXPIRED_OTP"
                    );
        }


        return ResponseEntity.ok(
                "EMAIL_VERIFIED"
        );
    }


    // ==========================================
    // LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {


        String result =
                memberService.login(request);


        switch (result) {

            case "EMAIL_NOT_REGISTERED":

                return ResponseEntity
                        .badRequest()
                        .body(
                            "EMAIL_NOT_REGISTERED"
                        );


            case "EMAIL_NOT_VERIFIED":

                return ResponseEntity
                        .badRequest()
                        .body(
                            "EMAIL_NOT_VERIFIED"
                        );


            case "INVALID_PASSWORD":

                return ResponseEntity
                        .badRequest()
                        .body(
                            "INVALID_PASSWORD"
                        );


            case "LOGIN_SUCCESS":

                return ResponseEntity.ok(
                        "LOGIN_SUCCESS"
                );


            default:

                return ResponseEntity
                        .badRequest()
                        .body(
                            "LOGIN_FAILED"
                        );
        }
    }


    // ==========================================
    // PROFILE
    // ==========================================

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestParam String email) {


        Optional<Member> member =
                memberService.getMemberByEmail(
                        email
                );


        if (member.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        return ResponseEntity.ok(
                member.get()
        );
    }
}