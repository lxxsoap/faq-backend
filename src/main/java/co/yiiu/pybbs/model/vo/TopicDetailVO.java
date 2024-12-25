package co.yiiu.pybbs.model.vo;

import co.yiiu.pybbs.model.Tag;
import co.yiiu.pybbs.model.Topic;


import java.util.List;


public class TopicDetailVO {
    private Topic topic;
    private List<Tag> tags;

    //get set
    public Topic getTopic() {
        return topic;
    }
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
