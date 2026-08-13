package ANJANEYA_GYM.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
    }
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String membershipPlan;

    private String preferredTiming;

    private boolean emailVerified;

    private boolean membershipActive;


    public Member() {
    }


    public Member(
            String name,
            String phone,
            String email,
            String password,
            String membershipPlan,
            String preferredTiming) {

        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.membershipPlan = membershipPlan;
        this.preferredTiming = preferredTiming;

        this.emailVerified = false;
        this.membershipActive = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }


    public String getPreferredTiming() {
        return preferredTiming;
    }

    public void setPreferredTiming(String preferredTiming) {
        this.preferredTiming = preferredTiming;
    }


    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


    public boolean isMembershipActive() {
        return membershipActive;
    }

    public void setMembershipActive(boolean membershipActive) {
        this.membershipActive = membershipActive;
    }
}