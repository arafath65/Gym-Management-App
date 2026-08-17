package com.arafath65.gymManagement.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_tenant_member_no",
                        columnNames = {
                                "tenant_id",
                                "member_no"
                        }
                )
        }
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private int memberId;


    // =====================================================
    // TENANT
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;


    // =====================================================
    // BRANCH
    // =====================================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "branch_id",
            nullable = false
    )
    private Branch branch;


    // =====================================================
    // BASIC MEMBER INFORMATION
    // =====================================================

    @Column(
            name = "member_no",
            nullable = false,
            length = 30
    )
    private String memberNo;


    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;


    @Column(
            name = "last_name",
            length = 100
    )
    private String lastName;


    @Column(
            name = "full_name",
            nullable = false,
            length = 200
    )
    private String fullName;


    @Column(length = 20)
    private String gender;


    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;


    @Column(length = 30)
    private String nic;


    @Column(length = 30)
    private String mobile;


    // =====================================================
    // EMERGENCY CONTACT
    // =====================================================

    @Column(
            name = "emergency_contact_name",
            length = 150
    )
    private String emergencyContactName;


    @Column(
            name = "emergency_contact_mobile",
            length = 30
    )
    private String emergencyContactMobile;


    @Column(
            name = "emergency_contact_relationship",
            length = 50
    )
    private String emergencyContactRelationship;


    // =====================================================
    // CONTACT / PERSONAL
    // =====================================================

    @Column(length = 150)
    private String email;


    @Column(length = 100)
    private String occupation;


    // =====================================================
    // MEDICAL / FITNESS BASIC INFORMATION
    // =====================================================

    @Column(length = 10)
    private String bloodGroup;


    @Column(
            name = "medical_conditions",
            columnDefinition = "TEXT"
    )
    private String medicalConditions;


    @Column(
            length = 255
    )
    private String goal;


    @Column(
            columnDefinition = "TEXT"
    )
    private String notes;


    // =====================================================
    // ADDRESS
    // =====================================================

    @Column(length = 255)
    private String address;


    @Column(length = 100)
    private String city;


    // =====================================================
    // PROFILE IMAGE
    // =====================================================

    @Column(
            name = "profile_image",
            length = 500
    )
    private String profileImage;


    // =====================================================
    // JOIN DATE
    // =====================================================

    @Column(
            name = "join_date",
            nullable = false
    )
    private LocalDate joinDate;


    // =====================================================
    // STATUS
    // =====================================================

    @Column(
            nullable = false
    )
    private boolean status = true;


    // =====================================================
    // AUDIT
    // =====================================================

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;


    // =====================================================
    // LIFECYCLE
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (joinDate == null) {

            joinDate =
                    LocalDate.now();
        }
    }


    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }


    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }


    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }


    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(
            String emergencyContactName) {

        this.emergencyContactName =
                emergencyContactName;
    }


    public String getEmergencyContactMobile() {
        return emergencyContactMobile;
    }

    public void setEmergencyContactMobile(
            String emergencyContactMobile) {

        this.emergencyContactMobile =
                emergencyContactMobile;
    }


    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public void setEmergencyContactRelationship(
            String emergencyContactRelationship) {

        this.emergencyContactRelationship =
                emergencyContactRelationship;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }


    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }


    public String getMedicalConditions() {
        return medicalConditions;
    }

    public void setMedicalConditions(
            String medicalConditions) {

        this.medicalConditions =
                medicalConditions;
    }


    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }


    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}