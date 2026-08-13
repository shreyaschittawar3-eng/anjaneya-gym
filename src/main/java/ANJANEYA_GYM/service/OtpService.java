package ANJANEYA_GYM.service;

import ANJANEYA_GYM.entity.OtpVerification;
import ANJANEYA_GYM.repository.OtpVerificationRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final OtpVerificationRepository otpRepository;

    public OtpService(
            JavaMailSender mailSender,
            OtpVerificationRepository otpRepository) {

        this.mailSender = mailSender;
        this.otpRepository = otpRepository;
    }


    // =========================================================
    // SEND OTP
    // =========================================================

    @Transactional
    public void sendOtp(String email) {

        // Generate 6-digit OTP
        Random random = new Random();

        String otp = String.format(
                "%06d",
                random.nextInt(1000000)
        );


        // OTP valid for 5 minutes
        LocalDateTime expiryTime =
                LocalDateTime.now().plusMinutes(5);


        // Remove previous OTP for this email
        otpRepository.deleteByEmail(email);


        // Create new OTP record
        OtpVerification verification =
                new OtpVerification(
                        email,
                        otp,
                        expiryTime
                );


        // Save OTP in database
        otpRepository.save(verification);


        // Create email
        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setTo(email);

        message.setSubject(
                "Anjaneya Gym - Email Verification OTP"
        );


        message.setText(
                "Welcome to Anjaneya Gym!\n\n" +

                "Your OTP for email verification is: "
                + otp + "\n\n" +

                "This OTP is valid for 5 minutes.\n\n" +

                "Please do not share this OTP with anyone."
        );


        // Send email
        mailSender.send(message);
    }


    // =========================================================
    // VERIFY OTP
    // =========================================================

    @Transactional
    public boolean verifyOtp(
            String email,
            String otp) {


        // Find OTP by email
        OtpVerification verification =
                otpRepository.findByEmail(email)
                        .orElse(null);


        // OTP not found
        if (verification == null) {

            return false;
        }


        // Check OTP expiry
        if (LocalDateTime.now()
                .isAfter(verification.getExpiryTime())) {


            // Delete expired OTP
            otpRepository.deleteByEmail(email);

            return false;
        }


        // Check OTP value
        if (!verification.getOtp().equals(otp)) {

            return false;
        }


        // OTP is correct
        // Delete OTP so it cannot be reused
        otpRepository.deleteByEmail(email);


        return true;
    }
}