package co.yiiu.pybbs.model.dto;

public class UpdateEmailRequestDTO {
    private String email;
    private String code;

    // Getter 和 Setter 方法
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
