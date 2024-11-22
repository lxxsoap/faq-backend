package co.yiiu.pybbs.model.dto;

public class CommentCreateRequestDTO {
    private String content;
    private Integer topicId;
    private Integer commentId;

    // getter
public String getContent() {
    return content;
}
public Integer getTopicId() {
    return topicId;
}
public Integer getCommentId() {
    return commentId;
}
    
        // setter
public void setContent(String content) {
    this.content = content;
}
public void setTopicId(Integer topicId) {
    this.topicId = topicId; 
}

public void setCommentId(Integer commentId) {
    this.commentId = commentId;
}

}




