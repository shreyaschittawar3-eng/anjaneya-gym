package ANJANEYA_GYM.repository;

import ANJANEYA_GYM.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByEmailOrderByPaymentDateDesc(String email);

    Optional<Payment> findFirstByEmailOrderByPaymentDateDesc(String email);
}