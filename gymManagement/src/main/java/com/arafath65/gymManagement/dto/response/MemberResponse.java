package com.arafath65.gymManagement.dto.response;

import java.time.LocalDate;

public class MemberResponse {

    private Integer memberId;

    private Integer tenantId;
    private Integer branchId;

    private String tenantNo;
    private String gymName;

    private String branchNo;
    private String branchName;

    private String memberNo;

    // =====================================================
    // PERSONAL
    // =====================================================

    private String firstName;
    private String lastName;
    private String fullName;

    private String gender;
    private LocalDate dateOfBirth;

    private String nic;

    // =====================================================
    // CONTACT
    // =====================================================

    private String mobile;
    private String email;

    private String address;
    private String city;

    // =====================================================
    // EMERGENCY CONTACT
    // =====================================================

    private String emergencyContactName;
    private String emergencyContactMobile;
    private String emergencyContactRelationship;

    // =====================================================
    // HEALTH
    // =====================================================

    private String occupation;
    private String bloodGroup;
    private String medicalConditions;
    private String goal;

    // =====================================================
    // NOTES
    // =====================================================

    private String notes;

    // =====================================================
    // PROFILE
    // =====================================================

    private String profileImage;

    // =====================================================
    // GYM
    // =====================================================

    private LocalDate joinDate;

    private Boolean status;


    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }


    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }


    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }


    public String getTenantNo() {
        return tenantNo;
    }

    public void setTenantNo(String tenantNo) {
        this.tenantNo = tenantNo;
    }


    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }


    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }


    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }


    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }


    // =====================================================
    // PERSONAL GETTERS / SETTERS
    // =====================================================

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


    // =====================================================
    // CONTACT GETTERS / SETTERS
    // =====================================================

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    // =====================================================
    // EMERGENCY GETTERS / SETTERS
    // =====================================================

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


    // =====================================================
    // HEALTH GETTERS / SETTERS
    // =====================================================

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


    // =====================================================
    // NOTES
    // =====================================================

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    // =====================================================
    // PROFILE
    // =====================================================

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }


    // =====================================================
    // GYM
    // =====================================================

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}