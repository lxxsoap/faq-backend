package co.yiiu.pybbs.model.vo;

import co.yiiu.pybbs.model.Comment;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public class CommentsByTopic extends Comment implements Serializable {
    private static final long serialVersionUID = 8082073760910701836L;
    // 话题下面的评论列表单个对象的数据结构

    private String username;

    private String nickName;
    private String avatar;
    // 评论的层级，直接评论话题的，layer即为0，如果回复了评论的，则当前回复的layer为评论对象的layer+1
    private Integer layer;
    // 评论的点赞数
    private Integer likeCount;
    // 当前用户是否点赞
    private Boolean liked; 

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    private LinkedHashMap<Integer, List<CommentsByTopic>> children;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public LinkedHashMap<Integer, List<CommentsByTopic>> getChildren() {
        return children;
    }

    public void setChildren(LinkedHashMap<Integer, List<CommentsByTopic>> children) {
        this.children = children;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
