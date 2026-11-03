package arafath65.minime.gymmanagementui.model;

public class ManagerApprovalRequest {

    private final String username;
    private final String password;

    public ManagerApprovalRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}