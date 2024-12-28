package co.yiiu.pybbs.model.dto;

public class UpdateProfileRequestDTO {
    private String nickName;
    private String website;
    private String bio;
    private Boolean emailNotification;
    private String career;
    private String tags;

    // Getter 和 Setter 方法
    public String getNickName() {
        return nickName;
    }
    public String getWebsite() {
        return website;
    }
    public String getBio() {
        return bio;
    }
    public Boolean getEmailNotification() {
        return emailNotification;
    }
    public String getCareer() {
        return career;
    }
    public String getTags() {
        return tags;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setEmailNotification(Boolean emailNotification) {
        this.emailNotification = emailNotification;
    }
    public void setCareer(String career) {
        this.career = career;
    }
    public void setTags(String tags) {
        this.tags = tags;
    }
}
