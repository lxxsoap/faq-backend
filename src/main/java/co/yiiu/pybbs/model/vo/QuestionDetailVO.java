package co.yiiu.pybbs.model.vo;

import co.yiiu.pybbs.model.Collect;
import co.yiiu.pybbs.model.Tag;
import co.yiiu.pybbs.model.Topic;
import co.yiiu.pybbs.model.User;

import java.util.List;

/**
 * <p>@description 问题详情VO </p >
 *
 * @author <a href=" ">Shaw</a >
 * @version v1.1.0
 * @since 2024-08-19 JDK11+
 */

public class QuestionDetailVO extends Topic {

    private String userName;

    private String nickName;
    private List<Tag> tags;

    private List<CommentsByTopic> comments;
    private   User topicUser;
    private List<Collect> collects;

    //get set
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    public List<CommentsByTopic> getComments() {
        return comments;
    }
    public void setComments(List<CommentsByTopic> comments) {
        this.comments = comments;
    }
    public User getTopicUser() {
        return topicUser;
    }
    public void setTopicUser(User topicUser) {
        this.topicUser = topicUser;
    }
    public List<Collect> getCollects() {
        return collects;
    }
    public void setCollects(List<Collect> collects) {
        this.collects = collects;
    }


}
