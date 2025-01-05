package co.yiiu.pybbs.controller.api;

import co.yiiu.pybbs.exception.ApiAssert;
import co.yiiu.pybbs.model.Collect;
import co.yiiu.pybbs.model.User;
import co.yiiu.pybbs.service.ICollectService;
import co.yiiu.pybbs.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
@Api(tags = "收藏")
@RestController
@RequestMapping("/api/collect")
public class CollectApiController extends BaseApiController {

    @Resource
    private ICollectService collectService;

    // 收藏话题
    @ApiOperation(value = "查看收藏")
    @PostMapping("/{topicId}")
    public Result get(@PathVariable Integer topicId) {
        User user = getApiUser();
        Collect collect = collectService.selectByTopicIdAndUserId(topicId, user.getId());
        ApiAssert.isNull(collect, "该话题已被收藏");
        collectService.insert(topicId, user);
        return success();
    }

    // 取消收藏
    @ApiOperation(value = "取消收藏")
    @DeleteMapping("/{topicId}")
    public Result delete(@PathVariable Integer topicId) {
        User user = getApiUser();
        Collect collect = collectService.selectByTopicIdAndUserId(topicId, user.getId());
        ApiAssert.notNull(collect, "该话题未被收藏");
        collectService.delete(topicId, user.getId());
        return success();
    }
}
