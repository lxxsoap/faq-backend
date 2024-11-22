package co.yiiu.pybbs.model.dto;

public class TopicUpdateRequestDTO {
    private Integer id;
    private String title;
    private String content;

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
}
