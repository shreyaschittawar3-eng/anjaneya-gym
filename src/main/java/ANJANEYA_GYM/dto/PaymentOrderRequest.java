package ANJANEYA_GYM.dto;

public class PaymentOrderRequest {

    private String email;
    private String plan;
    private Double amount;

    // =========================
    // GET EMAIL
    // =========================

    public String getEmail() {
        return email;
    }

    // =========================
    // SET EMAIL
    // =========================

    public void setEmail(String email) {
        this.email = email;
    }

    // =========================
    // GET PLAN
    // =========================

    public String getPlan() {
        return plan;
    }

    // =========================
    // SET PLAN
    // =========================

    public void setPlan(String plan) {
        this.plan = plan;
    }

    // =========================
    // GET AMOUNT
    // =========================

    public Double getAmount() {
        return amount;
    }

    // =========================
    // SET AMOUNT
    // =========================

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}