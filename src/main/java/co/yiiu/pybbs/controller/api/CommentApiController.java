package co.yiiu.pybbs.controller.api;

import co.yiiu.pybbs.exception.ApiAssert;
import co.yiiu.pybbs.model.Comment;
import co.yiiu.pybbs.model.Topic;
import co.yiiu.pybbs.model.User;
import co.yiiu.pybbs.model.dto.CommentCreateRequestDTO;
import co.yiiu.pybbs.model.dto.CommentUpdateRequestDTO;
import co.yiiu.pybbs.service.ICommentService;
import co.yiiu.pybbs.service.ISystemConfigService;
import co.yiiu.pybbs.service.ITopicService;
import co.yiiu.pybbs.util.Result;
import co.yiiu.pybbs.util.SensitiveWordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Api(tags = "评论")
@RestController
@RequestMapping("/api/comment")
public class CommentApiController extends BaseApiController {

    @Resource
    private ICommentService commentService;
    @Resource
    private ITopicService topicService;
    @Resource
    private ISystemConfigService systemConfigService;

    // 创建评论
    @ApiOperation(value = "创建评论")
    @PostMapping
    public Result create(@RequestBody CommentCreateRequestDTO dto) {
        User user = getApiUser();
        ApiAssert.isTrue(user.getActive(), "你的帐号还没有激活，请去个人设置页面激活帐号");
        String content = dto.getContent();
        Integer topicId = dto.getTopicId();
        Integer commentId = dto.getCommentId();

        ApiAssert.notEmpty(content, "请输入评论内容");
        ApiAssert.notNull(topicId, "话题ID呢？");
        Topic topic = topicService.selectById(topicId);
        ApiAssert.notNull(topic, "话题不存在");

        // 组装comment对象
        Comment comment = new Comment();
        comment.setCommentId(commentId);
        comment.setStyle(systemConfigService.selectAllConfig().get("content_style"));
        comment.setContent(content);
        comment.setInTime(new Date());
        comment.setTopicId(topic.getId());
        comment.setUserId(user.getId());
        comment = commentService.insert(comment, topic, user);
        // 过滤评论内容
        comment.setContent(
                SensitiveWordUtil.replaceSensitiveWord(comment.getContent(), "*", SensitiveWordUtil.MinMatchType));
        return success(comment);
    }

    // 更新评论
    @ApiOperation(value = "更新评论")
    // 更新操作不用判断用户是否激活过，如果没有激活的用户是没有办法评论的，所以更新操作不做帐号是否激活判断
    @PutMapping("/{id}")
    public Result update(@PathVariable Integer id, @RequestBody CommentUpdateRequestDTO dto) {
        User user = getApiUser();
        String content = dto.getContent();
        ApiAssert.notNull(id, "评论id为空");
        ApiAssert.notEmpty(content, "请输入评论内容");
        Comment comment = commentService.selectById(id);
        ApiAssert.notNull(comment, "评论不存在");
        ApiAssert.isTrue(comment.getUserId().equals(user.getId()), "无权编辑此评论");
        comment.setContent(content);
        commentService.update(comment);
        comment.setContent(
                SensitiveWordUtil.replaceSensitiveWord(comment.getContent(), "*", SensitiveWordUtil.MinMatchType));
        return success(comment);
    }

    // 删除评论
    @ApiOperation(value = "删除评论")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        User user = getApiUser();
        Comment comment = commentService.selectById(id);
        ApiAssert.notNull(comment, "评论不存在");
        ApiAssert.isTrue(comment.getUserId().equals(user.getId()), "无权删除此评论");
        commentService.delete(comment);
        return success();
    }

    // 点赞评论
    @ApiOperation(value = "点赞评论")
    @GetMapping("/{id}/vote")
    public Result vote(@PathVariable Integer id) {
        User user = getApiUser();
        Comment comment = commentService.selectById(id);
        ApiAssert.notNull(comment, "评论不存在");
       // ApiAssert.notTrue(comment.getUserId().equals(user.getId()), "不能给自己的评论点赞");
        int voteCount = commentService.vote(comment, user);
        return success(voteCount);
    }
}
