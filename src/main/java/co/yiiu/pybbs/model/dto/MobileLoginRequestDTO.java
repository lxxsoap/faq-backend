package co.yiiu.pybbs.model.dto;

public class MobileLoginRequestDTO {
    private String mobile;
    private String code;

    // Getter 和 Setter 方法
    public String getMobile() {
        return mobile;
    }
    public String getCode() {
        return code;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
}
