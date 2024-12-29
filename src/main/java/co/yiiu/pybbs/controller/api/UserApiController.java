package co.yiiu.pybbs.controller.api;

import co.yiiu.pybbs.model.OAuthUser;
import co.yiiu.pybbs.model.Tag;
import co.yiiu.pybbs.model.User;
import co.yiiu.pybbs.service.*;
import co.yiiu.pybbs.util.MyPage;
import co.yiiu.pybbs.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Api(tags = "用户")
@RestController
@RequestMapping("/api/user")
public class UserApiController extends BaseApiController {

    @Resource
    private IUserService userService;
    @Resource
    private ITopicService topicService;
    @Resource
    private ICommentService commentService;
    @Resource
    private ICollectService collectService;
    @Resource
    private IOAuthUserService oAuthUserService;
    @Resource
    private ITagService tagService;

    // 用户的个人信息
    @ApiOperation(value = "用户个人信息")
    @GetMapping("/userInfo")
    public Result profile() {
        User user = getApiUser();

        // 查询oauth登录的用户信息
        List<OAuthUser> oAuthUsers = oAuthUserService.selectByUserId(user.getId());
        // 查询用户的话题
        MyPage<Map<String, Object>> topics = topicService.selectByUserId(user.getId(), 1, 10);
        // 查询用户参与的评论
        MyPage<Map<String, Object>> comments = commentService.selectByUserId(user.getId(), 1, 10);
        // 查询用户收藏的话题数
        Integer collectCount = collectService.countByUserId(user.getId());

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("oAuthUsers", oAuthUsers);
        map.put("topics", topics);
        map.put("comments", comments);
        map.put("collectCount", collectCount);
        return success(map);

    }

    // 用户发布的话题
    @ApiOperation(value = "用户发布的问题")
    @GetMapping("/topics")
    public Result topics(@RequestParam(defaultValue = "1") Integer pageNo) {
        // 查询用户个人信息
        User user = getApiUser();

        // 查询用户的话题
        MyPage<Map<String, Object>> topics = topicService.selectByUserId(user.getId(), pageNo, null);

        // 为每个话题添加标签信息
        List<Map<String, Object>> records = topics.getRecords();
        for (Map<String, Object> topic : records) {
            Integer topicId = (Integer) topic.get("id");
            List<Tag> tags = tagService.selectByTopicId(topicId);
            topic.put("tags", tags);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("topics", topics);
        return success(map);
    }

    // 用户评论列表
    @ApiOperation(value = "用户评论列表")
    @GetMapping("/comments")
    public Result comments(@RequestParam(defaultValue = "1") Integer pageNo) {
        // 查询用户个人信息
        User user = getApiUser();
        // 查询用户参与的评论
        MyPage<Map<String, Object>> comments = commentService.selectByUserId(user.getId(), pageNo, null);
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("comments", comments);
        return success(map);
    }

    // 用户收藏的话题
    @ApiOperation(value = "用户收藏的问题")
    @GetMapping("/collects")
    public Result collects(@RequestParam(defaultValue = "1") Integer pageNo) {
        // 查询用户个人信息
        User user = getApiUser();

        // 查询用户收藏的话题
        MyPage<Map<String, Object>> collects = collectService.selectByUserId(user.getId(), pageNo, null);

        // 为每个收藏的话题添加标签信息
        List<Map<String, Object>> records = collects.getRecords();
        for (Map<String, Object> collect : records) {
            Integer topicId = (Integer) collect.get("topic_id");
            List<Tag> tags = tagService.selectByTopicId(topicId);
            collect.put("tags", tags);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("collects", collects);
        return success(map);
    }

    @ApiOperation(value = "获取用户公开信息")
    @GetMapping("/public/{userId}")
    public Result getUserInfo(@PathVariable Integer userId) {
        Map<String, Object> map = userService.getPublicUser(userId);
        if (map == null) {
            return error("用户不存在");
        }
        return success(map);
    }
}
