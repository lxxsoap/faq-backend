package co.yiiu.pybbs.controller.api;

import co.yiiu.pybbs.exception.ApiAssert;
import co.yiiu.pybbs.model.Collect;
import co.yiiu.pybbs.model.Tag;
import co.yiiu.pybbs.model.Topic;
import co.yiiu.pybbs.model.User;
import co.yiiu.pybbs.model.dto.TopicCreateRequestDTO;
import co.yiiu.pybbs.model.dto.TopicUpdateRequestDTO;
import co.yiiu.pybbs.model.dto.TopicSolvedRequestDTO;
import co.yiiu.pybbs.model.vo.CommentsByTopic;
import co.yiiu.pybbs.model.vo.QuestionDetailVO;
import co.yiiu.pybbs.model.vo.TopicDetailVO;
import co.yiiu.pybbs.service.*;
import co.yiiu.pybbs.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Api(tags = "问题")
@RestController
@RequestMapping("/api/topic")
public class TopicApiController extends BaseApiController {

    @Resource
    private ITopicService topicService;
    @Resource
    private ITagService tagService;
    @Resource
    private ICommentService commentService;
    @Resource
    private IUserService userService;
    @Resource
    private ICollectService collectService;

    // 查看所有话题
    @ApiOperation(value = "问题列表")
    @GetMapping("/list")
    public Result list(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "tab", defaultValue = "all") String tab,
            @RequestParam(value = "tag", required = false) String tagName,
            @RequestParam(value = "title", required = false) String title) {

        MyPage<Map<String, Object>> page;

        // 1. 标签查询
        if (!StringUtils.isEmpty(tagName)) {
            Tag tag = tagService.selectByName(tagName);
            if (tag == null) {
                return error("标签不存在");
            }
            page = tagService.selectTopicByTagId(tag.getId(), pageNo);
            // 查询每个话题的所有标签
            tagService.selectTagsByTopicId(page);

            // 如果同时有标题搜索，在标签结果中过滤
            if (!StringUtils.isEmpty(title)) {
                List<Map<String, Object>> filteredRecords = page.getRecords().stream()
                        .filter(topic -> ((String) topic.get("title")).toLowerCase()
                                .contains(title.toLowerCase()))
                        .collect(Collectors.toList());
                page.setRecords(filteredRecords);
            }
        }
        // 2. 仅标题搜索
        else if (!StringUtils.isEmpty(title)) {
            page = topicService.selectByTitle(title, pageNo, tab);
            // 查询每个话题的所有标签
            tagService.selectTagsByTopicId(page);
        }
        // 3. 无条件查询
        else {
            page = topicService.selectAll(pageNo, tab);
            // 查询每个话题的所有标签
            tagService.selectTagsByTopicId(page);
        }

        return success(page);
    }

    // 话题详情
    @ApiOperation(value = "问题详情")
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        // 查询话题详情
        Topic topic = topicService.selectById(id);
        // 查询话题关联的标签
        List<Tag> tags = tagService.selectByTopicId(id);
        // 查询话题的评论
        User user = getApiUser(false);
        List<CommentsByTopic> comments;
        if (user != null) {
            comments = commentService.selectByTopicIdAndLiked(id,user);
        }else{
            comments = commentService.selectByTopicId(id);
        }
        // 查询话题的作者信息
        User topicUser = userService.selectById(topic.getUserId());
        // 查询话题有多少收藏
        List<Collect> collects = collectService.selectByTopicId(id);
        // 如果自己登录了，查询自己是否收藏过这个话题
        if (user != null) {
            Collect collect = collectService.selectByTopicIdAndUserId(id, user.getId());
            if (collect != null) {
                map.put("collected", true);
            }else{
                map.put("collected", false);
            }
        }
        // 话题浏览量+1
        String ip = IpUtil.getIpAddr(request);
        ip = ip.replace(":", "_").replace(".", "_");
        topic = topicService.updateViewCount(topic, ip);
        topic.setContent(
                SensitiveWordUtil.replaceSensitiveWord(topic.getContent(), "*", SensitiveWordUtil.MinMatchType));

        /**
         * faq-backend返回参数
         */
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        // topic转换成questionDetailVO
        questionDetailVO.setUserName(topicUser.getUsername());
        questionDetailVO.setId(topic.getId());
        questionDetailVO.setTitle(topic.getTitle());
        questionDetailVO.setContent(topic.getContent());
        questionDetailVO.setCommentCount(topic.getCommentCount());
        questionDetailVO.setCollectCount(topic.getCollectCount());
        questionDetailVO.setInTime(topic.getInTime());
        questionDetailVO.setModifyTime(topic.getModifyTime());
        questionDetailVO.setUserId(topic.getUserId());
        questionDetailVO.setTop(topic.getTop());
        questionDetailVO.setGood(topic.getGood());
        questionDetailVO.setTags(tags);
        questionDetailVO.setComments(comments);
        questionDetailVO.setTopicUser(topicUser);
        questionDetailVO.setCollects(collects);
        return success(questionDetailVO);
    }

    // 保存话题
    @ApiOperation(value = "创建问题")
    @PostMapping
    public Result create(@RequestBody TopicCreateRequestDTO dto) {
        User user = getApiUser();
        ApiAssert.isTrue(user.getActive(), "你的帐号还没有激活，请去个人设置页面激活帐号");
        String title = dto.getTitle();
        String content = dto.getContent();
        String tags = dto.getTags();
        title = Jsoup.clean(title, Whitelist.basic());
        ApiAssert.notEmpty(title, "请输入标题");
        ApiAssert.isNull(topicService.selectByTitle(title), "话题标题重复");
        String[] strings = StringUtils.commaDelimitedListToStringArray(tags);
        Set<String> set = StringUtil.removeEmpty(strings);
        ApiAssert.notTrue(set.isEmpty() || set.size() > 3, "请输入标签且标签最多3个");
        // 保存话题 TODO:tag标签关联实现
        // 再次将tag转成逗号隔开的字符串
        tags = StringUtils.collectionToCommaDelimitedString(set);
        Topic topic = topicService.insert(title, content, tags, user);
        topic.setContent(
                SensitiveWordUtil.replaceSensitiveWord(topic.getContent(), "*", SensitiveWordUtil.MinMatchType));
        return success(topic);
    }

    // 更新话题
    @ApiOperation(value = "更新问题")
    @PutMapping(value = "/{id}")
    public Result edit(@RequestBody TopicUpdateRequestDTO dto) {
        User user = getApiUser();
        String title = dto.getTitle();
        String content = dto.getContent();
        ApiAssert.notEmpty(title, "请输入标题");

        // 更新话题
        Topic topic = topicService.selectById(dto.getId());
        ApiAssert.isTrue(topic.getUserId().equals(user.getId()), "谁给你的权限修改别人的话题的？");

        topic.setTitle(Jsoup.clean(title, Whitelist.none().addTags("video")));
        topic.setContent(content);
        topic.setModifyTime(new Date());
        topicService.update(topic, null);

        // 处理敏感词
        topic.setContent(
                SensitiveWordUtil.replaceSensitiveWord(topic.getContent(), "*", SensitiveWordUtil.MinMatchType));

        // 查询话题关联的标签
        List<Tag> tags = tagService.selectByTopicId(topic.getId());

        // 构建返回的VO对象
        TopicDetailVO topicDetailVO = new TopicDetailVO();
        topicDetailVO.setTopic(topic);
        topicDetailVO.setTags(tags);

        return success(topicDetailVO);
    }

    // 删除话题
    @ApiOperation(value = "删除问题")
    @DeleteMapping("{id}")
    public Result delete(@PathVariable Integer id) {
        User user = getApiUser();
        Topic topic = topicService.selectById(id);
        ApiAssert.isTrue(topic.getUserId().equals(user.getId()), "谁给你的权限删除别人的话题的？");
        topicService.delete(topic);
        return success();
    }

    @GetMapping("/{id}/vote")
    public Result vote(@PathVariable Integer id) {
        User user = getApiUser();
        Topic topic = topicService.selectById(id);
        ApiAssert.notNull(topic, "这个话题可能已经被删除了");
        ApiAssert.notTrue(topic.getUserId().equals(user.getId()), "给自己话题点赞，脸皮真厚！！");
        int voteCount = topicService.vote(topic, getApiUser());
        return success(voteCount);
    }

    @ApiOperation(value = "修改话题解决状态")
    @PutMapping("/solved")
    public Result updateSolved(@RequestBody TopicSolvedRequestDTO dto) {
        User user = getApiUser();
        
        // 查询话题
        Topic topic = topicService.selectById(dto.getId());
        if (topic == null) {
            return error("话题不存在");
        }

        // 只有话题作者可以修改解决状态
        ApiAssert.isTrue(topic.getUserId().equals(user.getId()), "只有话题作者才能修改解决状态");

        // 更新解决状态
        topic.setSolved(dto.getSolved());
        topicService.update(topic, null);

        // 查询话题关联的标签
        List<Tag> tags = tagService.selectByTopicId(topic.getId());

        // 构建返回的VO对象
        TopicDetailVO topicDetailVO = new TopicDetailVO();
        topicDetailVO.setTopic(topic);
        topicDetailVO.setTags(tags);

        return success(topicDetailVO);
    }
}
