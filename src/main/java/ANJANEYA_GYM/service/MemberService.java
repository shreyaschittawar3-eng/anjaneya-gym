package ANJANEYA_GYM.service;

import ANJANEYA_GYM.dto.LoginRequest;
import ANJANEYA_GYM.dto.RegistrationRequest;
import ANJANEYA_GYM.entity.Member;
import ANJANEYA_GYM.repository.MemberRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final OtpService otpService;


    public MemberService(
            MemberRepository memberRepository,
            OtpService otpService) {

        this.memberRepository = memberRepository;
        this.otpService = otpService;
    }


    // ==========================================
    // REGISTER
    // ==========================================

    public String register(RegistrationRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        Optional<Member> existingMember =
                memberRepository.findByEmail(email);


        // EMAIL ALREADY EXISTS
        if (existingMember.isPresent()) {

            Member member = existingMember.get();


            // Already verified
            if (member.isEmailVerified()) {

                return "EMAIL_ALREADY_REGISTERED";
            }


            // Registered but OTP not verified
            member.setName(request.getName());
            member.setPhone(request.getPhone());
            member.setPassword(request.getPassword());
            member.setMembershipPlan(
                    request.getMembershipPlan()
            );
            member.setPreferredTiming(
                    request.getPreferredTiming()
            );

            memberRepository.save(member);

            otpService.sendOtp(email);

            return "OTP_SENT";
        }


        // ==========================================
        // NEW MEMBER
        // ==========================================

        Member member = new Member(
                request.getName(),
                request.getPhone(),
                email,
                request.getPassword(),
                request.getMembershipPlan(),
                request.getPreferredTiming()
        );


        memberRepository.save(member);


        // SEND OTP
        otpService.sendOtp(email);


        return "OTP_SENT";
    }


    // ==========================================
    // VERIFY EMAIL
    // ==========================================

    public boolean verifyEmail(
            String email,
            String otp) {

        email = email.trim().toLowerCase();


        boolean verified =
                otpService.verifyOtp(
                        email,
                        otp
                );


        if (!verified) {
            return false;
        }


        Optional<Member> optionalMember =
                memberRepository.findByEmail(email);


        if (optionalMember.isEmpty()) {
            return false;
        }


        Member member =
                optionalMember.get();


        member.setEmailVerified(true);


        memberRepository.save(member);


        return true;
    }


    // ==========================================
    // LOGIN
    // ==========================================

    public String login(LoginRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();


        Optional<Member> optionalMember =
                memberRepository.findByEmail(email);


        // EMAIL NOT FOUND
        if (optionalMember.isEmpty()) {

            return "EMAIL_NOT_REGISTERED";
        }


        Member member =
                optionalMember.get();


        // EMAIL NOT VERIFIED
        if (!member.isEmailVerified()) {

            return "EMAIL_NOT_VERIFIED";
        }


        // WRONG PASSWORD
        if (!member.getPassword()
                .equals(request.getPassword())) {

            return "INVALID_PASSWORD";
        }


        return "LOGIN_SUCCESS";
    }


    // ==========================================
    // GET PROFILE
    // ==========================================

    public Optional<Member> getMemberByEmail(
            String email) {

        return memberRepository.findByEmail(
                email.trim().toLowerCase()
        );
    }
}