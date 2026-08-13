package ANJANEYA_GYM.service;

import ANJANEYA_GYM.entity.Payment;
import ANJANEYA_GYM.repository.PaymentRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;


    public PaymentService(PaymentRepository paymentRepository) {

        this.paymentRepository = paymentRepository;
    }


    // =========================================================
    // SAVE SUCCESSFUL RAZORPAY PAYMENT
    // =========================================================

    public Payment saveSuccessfulPayment(
            String email,
            String plan,
            Double amount,
            String paymentMethod,
            String razorpayPaymentId,
            String razorpayOrderId,
            String receiptNumber) {


        Payment payment = new Payment();


        payment.setEmail(email);

        payment.setPlan(plan);

        payment.setAmount(amount);

        payment.setPaymentMethod(paymentMethod);


        // Payment has been verified by Razorpay

        payment.setPaymentStatus(
                "PAYMENT_SUCCESS"
        );


        payment.setPaymentDate(
                LocalDateTime.now()
        );


        // =====================================================
        // RAZORPAY DETAILS
        // =====================================================

        payment.setRazorpayPaymentId(
                razorpayPaymentId
        );


        payment.setRazorpayOrderId(
                razorpayOrderId
        );


        payment.setReceiptNumber(
                receiptNumber
        );


        return paymentRepository.save(payment);
    }


    // =========================================================
    // GET ALL PAYMENTS BY EMAIL
    // =========================================================

    public List<Payment> getPaymentsByEmail(
            String email) {

        return paymentRepository
                .findByEmailOrderByPaymentDateDesc(email);
    }


    // =========================================================
    // GET LATEST PAYMENT
    // =========================================================

    public Payment getLatestPayment(
            String email) {

        return paymentRepository
                .findFirstByEmailOrderByPaymentDateDesc(email)
                .orElse(null);
    }
}