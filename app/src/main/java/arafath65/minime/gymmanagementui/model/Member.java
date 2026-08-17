package arafath65.minime.gymmanagementui.model;

public class Member {

    private int memberId;

    private String memberNo;
    private String fullName;
    private String mobile;

    private String membershipName;
    private String membershipStatus;

    private String branchName;
    private String profileImage;


    // =====================================================
    // CONSTRUCTOR - API RESPONSE
    // =====================================================

    public Member(
            MemberResponse response) {

        // -------------------------------------------------
        // MEMBER ID
        // -------------------------------------------------

        if (response.getMemberId() != null) {

            this.memberId =
                    response.getMemberId();

        } else {

            this.memberId = 0;
        }


        // -------------------------------------------------
        // MEMBER NUMBER
        // -------------------------------------------------

        this.memberNo =
                response.getMemberNo();


        // -------------------------------------------------
        // NAME
        // -------------------------------------------------

        this.fullName =
                response.getFullName();


        // -------------------------------------------------
        // MOBILE
        // -------------------------------------------------

        this.mobile =
                response.getMobile();


        // -------------------------------------------------
        // MEMBERSHIP
        // -------------------------------------------------
        // Membership will be connected later
        // from the subscription module.

        this.membershipName =
                "-";


        // -------------------------------------------------
        // STATUS
        // -------------------------------------------------

        this.membershipStatus =
                Boolean.TRUE.equals(
                        response.getStatus()
                )
                        ? "ACTIVE"
                        : "INACTIVE";


        // -------------------------------------------------
        // BRANCH
        // -------------------------------------------------

        this.branchName =
                response.getBranchName();


        // -------------------------------------------------
        // PROFILE IMAGE
        // -------------------------------------------------

        this.profileImage =
                response.getProfileImage();
    }


    // =====================================================
    // GETTERS
    // =====================================================

    public int getMemberId() {

        return memberId;
    }


    public String getMemberNo() {

        return memberNo;
    }


    public String getFullName() {

        return fullName;
    }


    public String getMobile() {

        return mobile;
    }


    public String getMembershipName() {

        return membershipName;
    }


    public String getMembershipStatus() {

        return membershipStatus;
    }


    public String getBranchName() {

        return branchName;
    }


    public String getProfileImage() {

        return profileImage;
    }
}