package co.yiiu.pybbs.mapper;

import co.yiiu.pybbs.model.Topic;
import co.yiiu.pybbs.util.MyPage;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * Created by tomoya.
 * Copyright (c) 2018, All Rights Reserved.
 * https://atjiu.github.io
 */
public interface TopicMapper extends BaseMapper<Topic> {

    MyPage<Map<String, Object>> selectAll(MyPage<Map<String, Object>> iPage, @Param("tab") String tab);

    MyPage<Map<String, Object>> selectByTag(MyPage<Map<String, Object>> iPage, @Param("tag") String tag);

    MyPage<Map<String, Object>> selectByUserId(MyPage<Map<String, Object>> iPage, @Param("userId") Integer userId);

    MyPage<Map<String, Object>> selectAllForAdmin(MyPage<Map<String, Object>> iPage,
            @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("username") String username);

    int countToday();

    MyPage<Map<String, Object>> search(MyPage<Map<String, Object>> iPage, @Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM topic WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Integer userId);

    @Select("SELECT COALESCE(SUM(LENGTH(up_ids) - LENGTH(REPLACE(up_ids, ',', '')) + 1), 0) " +
            "FROM topic " +
            "WHERE user_id = #{userId} " +
            "AND up_ids IS NOT NULL " +
            "AND up_ids != ''")
    int countUpByUserId(@Param("userId") Integer userId);
}
