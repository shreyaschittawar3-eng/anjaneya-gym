package ANJANEYA_GYM.controller;

import ANJANEYA_GYM.dto.PaymentOrderRequest;
import ANJANEYA_GYM.entity.Payment;
import ANJANEYA_GYM.service.PaymentService;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/razorpay")
@CrossOrigin
public class RazorpayController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final PaymentService paymentService;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RazorpayController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    // =========================================================
    // CREATE RAZORPAY ORDER
    // =========================================================

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody PaymentOrderRequest request) {

        try {

            // -------------------------------------------------
            // GET CORRECT PRICE FROM BACKEND
            // -------------------------------------------------

            double amount = getCorrectAmount(
                    request.getPlan()
            );

            // Convert Rupees to Paise
            int amountInPaise =
                    (int) Math.round(amount * 100);


            // -------------------------------------------------
            // CREATE RAZORPAY CLIENT
            // -------------------------------------------------

            RazorpayClient razorpay =
                    new RazorpayClient(
                            keyId,
                            keySecret
                    );


            // -------------------------------------------------
            // CREATE RAZORPAY ORDER REQUEST
            // -------------------------------------------------

            JSONObject orderRequest =
                    new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            orderRequest.put(
                    "receipt",
                    "gym_" + System.currentTimeMillis()
            );


            // -------------------------------------------------
            // RAZORPAY NOTES
            // -------------------------------------------------

            JSONObject notes =
                    new JSONObject();

            notes.put(
                    "email",
                    request.getEmail()
            );

            notes.put(
                    "plan",
                    request.getPlan()
            );

            orderRequest.put(
                    "notes",
                    notes
            );


            // -------------------------------------------------
            // CREATE RAZORPAY ORDER
            // -------------------------------------------------

            Order order =
                    razorpay.orders.create(
                            orderRequest
                    );


            // -------------------------------------------------
            // CREATE RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "keyId",
                    keyId
            );

            response.put(
                    "orderId",
                    order.get("id").toString()
            );

            response.put(
                    "amount",
                    amountInPaise
            );

            response.put(
                    "plan",
                    request.getPlan()
            );

            response.put(
                    "email",
                    request.getEmail()
            );


            // -------------------------------------------------
            // SEND RESPONSE TO FRONTEND
            // -------------------------------------------------

            return ResponseEntity.ok(response);

        }

        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Unable to create Razorpay order: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // VERIFY RAZORPAY PAYMENT
    // =========================================================

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(
            @RequestBody String requestBody) {

        try {

            // -------------------------------------------------
            // CONVERT REQUEST TO JSON
            // -------------------------------------------------

            JSONObject paymentData =
                    new JSONObject(
                            requestBody
                    );


            // -------------------------------------------------
            // GET RAZORPAY DETAILS
            // -------------------------------------------------

            String razorpayOrderId =
                    paymentData.getString(
                            "razorpay_order_id"
                    );

            String razorpayPaymentId =
                    paymentData.getString(
                            "razorpay_payment_id"
                    );

            String razorpaySignature =
                    paymentData.getString(
                            "razorpay_signature"
                    );


            // -------------------------------------------------
            // VERIFY SIGNATURE
            // -------------------------------------------------

            String verificationData =
                    razorpayOrderId
                            + "|"
                            + razorpayPaymentId;


            boolean verified =
                    Utils.verifySignature(
                            verificationData,
                            razorpaySignature,
                            keySecret
                    );


            // -------------------------------------------------
            // PAYMENT NOT VERIFIED
            // -------------------------------------------------

            if (!verified) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                "Payment verification failed"
                        );
            }


            // -------------------------------------------------
            // GET MEMBER INFORMATION
            // -------------------------------------------------

            String email =
                    paymentData.getString(
                            "email"
                    );

            String plan =
                    paymentData.getString(
                            "plan"
                    );


            String paymentMethod =
                    paymentData.optString(
                            "paymentMethod",
                            "Razorpay"
                    );


            // -------------------------------------------------
            // GET CORRECT BACKEND AMOUNT
            // -------------------------------------------------

            double correctAmount =
                    getCorrectAmount(plan);


            // -------------------------------------------------
            // GENERATE RECEIPT NUMBER
            // -------------------------------------------------

            String receiptNumber =
                    "AGYM-"
                            + System.currentTimeMillis();


            // -------------------------------------------------
            // SAVE SUCCESSFUL PAYMENT
            // -------------------------------------------------

            Payment payment =
                    paymentService.saveSuccessfulPayment(

                            email,

                            plan,

                            correctAmount,

                            paymentMethod,

                            razorpayPaymentId,

                            razorpayOrderId,

                            receiptNumber
                    );


            // -------------------------------------------------
            // CREATE RESPONSE
            // -------------------------------------------------

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "status",
                    "SUCCESS"
            );

            response.put(
                    "message",
                    "Payment verified and saved successfully"
            );

            response.put(
                    "paymentId",
                    razorpayPaymentId
            );

            response.put(
                    "orderId",
                    razorpayOrderId
            );

            response.put(
                    "receiptNumber",
                    receiptNumber
            );


            // -------------------------------------------------
            // PAYMENT DETAILS
            // -------------------------------------------------

            Map<String, Object> paymentObject =
                    new HashMap<>();

            paymentObject.put(
                    "id",
                    payment.getId()
            );

            paymentObject.put(
                    "email",
                    payment.getEmail()
            );

            paymentObject.put(
                    "plan",
                    payment.getPlan()
            );

            paymentObject.put(
                    "amount",
                    payment.getAmount()
            );

            paymentObject.put(
                    "paymentMethod",
                    payment.getPaymentMethod()
            );

            paymentObject.put(
                    "paymentStatus",
                    payment.getPaymentStatus()
            );

            paymentObject.put(
                    "paymentDate",
                    payment.getPaymentDate()
                            .toString()
            );

            paymentObject.put(
                    "razorpayPaymentId",
                    payment.getRazorpayPaymentId()
            );

            paymentObject.put(
                    "razorpayOrderId",
                    payment.getRazorpayOrderId()
            );

            paymentObject.put(
                    "receiptNumber",
                    payment.getReceiptNumber()
            );


            response.put(
                    "payment",
                    paymentObject
            );


            // -------------------------------------------------
            // SEND RESPONSE
            // -------------------------------------------------

            return ResponseEntity.ok(response);

        }

        catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Razorpay Error: "
                                    + e.getMessage()
                    );
        }
    }


    // =========================================================
    // GYM PLAN PRICE VALIDATION
    // =========================================================

    private double getCorrectAmount(
            String plan) {

        switch (plan) {

            case "Strength Admission":
                return 200;

            case "Strength Monthly":
                return 800;

            case "Strength 3 Months":
                return 1850;

            case "Strength 6 Months":
                return 4000;

            case "Cardio Admission":
                return 400;

            case "Cardio Monthly":
                return 1000;

            case "Cardio 3 Months":
                return 2600;

            case "Cardio 6 Months":
                return 5000;

            case "Special Training":
                return 2500;

            default:

                throw new IllegalArgumentException(
                        "Invalid gym membership plan"
                );
        }
    }
}