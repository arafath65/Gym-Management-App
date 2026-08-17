package arafath65.minime.gymmanagementui.model;

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
    private String dateOfBirth;

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

    private String joinDate;

    private Boolean status;


    // =====================================================
    // GETTERS
    // =====================================================

    public Integer getMemberId() {
        return memberId;
    }


    public Integer getTenantId() {
        return tenantId;
    }


    public Integer getBranchId() {
        return branchId;
    }


    public String getTenantNo() {
        return tenantNo;
    }


    public String getGymName() {
        return gymName;
    }


    public String getBranchNo() {
        return branchNo;
    }


    public String getBranchName() {
        return branchName;
    }


    public String getMemberNo() {
        return memberNo;
    }


    // =====================================================
    // PERSONAL
    // =====================================================

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getFullName() {
        return fullName;
    }


    public String getGender() {
        return gender;
    }


    public String getDateOfBirth() {
        return dateOfBirth;
    }


    public String getNic() {
        return nic;
    }


    // =====================================================
    // CONTACT
    // =====================================================

    public String getMobile() {
        return mobile;
    }


    public String getEmail() {
        return email;
    }


    public String getAddress() {
        return address;
    }


    public String getCity() {
        return city;
    }


    // =====================================================
    // EMERGENCY
    // =====================================================

    public String getEmergencyContactName() {
        return emergencyContactName;
    }


    public String getEmergencyContactMobile() {
        return emergencyContactMobile;
    }


    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }


    // =====================================================
    // HEALTH
    // =====================================================

    public String getOccupation() {
        return occupation;
    }


    public String getBloodGroup() {
        return bloodGroup;
    }


    public String getMedicalConditions() {
        return medicalConditions;
    }


    public String getGoal() {
        return goal;
    }


    // =====================================================
    // NOTES
    // =====================================================

    public String getNotes() {
        return notes;
    }


    // =====================================================
    // PROFILE
    // =====================================================

    public String getProfileImage() {
        return profileImage;
    }


    // =====================================================
    // GYM
    // =====================================================

    public String getJoinDate() {
        return joinDate;
    }


    public Boolean getStatus() {
        return status;
    }
}