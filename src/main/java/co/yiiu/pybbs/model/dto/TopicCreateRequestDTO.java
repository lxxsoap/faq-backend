package co.yiiu.pybbs.model.dto;

public class TopicCreateRequestDTO {
    private String title;
    private String content;

    // Getter 和 Setter 方法
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
