package co.yiiu.pybbs.model.dto;

public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;

    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
}
