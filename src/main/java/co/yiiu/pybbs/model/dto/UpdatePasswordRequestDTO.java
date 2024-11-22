package co.yiiu.pybbs.model.dto;

public class UpdatePasswordRequestDTO {
    private String oldPassword;
    private String newPassword;

    // Getter 和 Setter 方法
    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
