package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.SalesPaymentMapper;
import com.ledger.model.dto.salesPayment.SalesPaymentQueryRequest;
import com.ledger.model.entity.SalesPayment;
import com.ledger.model.entity.SalesPaymentFavour;
import com.ledger.model.entity.SalesPaymentThumb;
import com.ledger.model.entity.User;
import com.ledger.model.vo.SalesPaymentVO;
import com.ledger.model.vo.UserVO;
import com.ledger.service.SalesPaymentService;
import com.ledger.service.UserService;
import com.ledger.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 销售回款服务实现
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class SalesPaymentServiceImpl extends ServiceImpl<SalesPaymentMapper, SalesPayment> implements SalesPaymentService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param salesPayment
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validSalesPayment(SalesPayment salesPayment, boolean add) {
        ThrowUtils.throwIf(salesPayment == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = salesPayment.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param salesPaymentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SalesPayment> getQueryWrapper(SalesPaymentQueryRequest salesPaymentQueryRequest) {
        QueryWrapper<SalesPayment> queryWrapper = new QueryWrapper<>();
        if (salesPaymentQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = salesPaymentQueryRequest.getId();
        Long notId = salesPaymentQueryRequest.getNotId();
        String title = salesPaymentQueryRequest.getTitle();
        String content = salesPaymentQueryRequest.getContent();
        String searchText = salesPaymentQueryRequest.getSearchText();
        String sortField = salesPaymentQueryRequest.getSortField();
        String sortOrder = salesPaymentQueryRequest.getSortOrder();
        List<String> tagList = salesPaymentQueryRequest.getTags();
        Long userId = salesPaymentQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取销售回款封装
     *
     * @param salesPayment
     * @param request
     * @return
     */
    @Override
    public SalesPaymentVO getSalesPaymentVO(SalesPayment salesPayment, HttpServletRequest request) {
        // 对象转封装类
        SalesPaymentVO salesPaymentVO = SalesPaymentVO.objToVo(salesPayment);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = salesPayment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        salesPaymentVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long salesPaymentId = salesPayment.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<SalesPaymentThumb> salesPaymentThumbQueryWrapper = new QueryWrapper<>();
            salesPaymentThumbQueryWrapper.in("salesPaymentId", salesPaymentId);
            salesPaymentThumbQueryWrapper.eq("userId", loginUser.getId());
            SalesPaymentThumb salesPaymentThumb = salesPaymentThumbMapper.selectOne(salesPaymentThumbQueryWrapper);
            salesPaymentVO.setHasThumb(salesPaymentThumb != null);
            // 获取收藏
            QueryWrapper<SalesPaymentFavour> salesPaymentFavourQueryWrapper = new QueryWrapper<>();
            salesPaymentFavourQueryWrapper.in("salesPaymentId", salesPaymentId);
            salesPaymentFavourQueryWrapper.eq("userId", loginUser.getId());
            SalesPaymentFavour salesPaymentFavour = salesPaymentFavourMapper.selectOne(salesPaymentFavourQueryWrapper);
            salesPaymentVO.setHasFavour(salesPaymentFavour != null);
        }
        // endregion

        return salesPaymentVO;
    }

    /**
     * 分页获取销售回款封装
     *
     * @param salesPaymentPage
     * @param request
     * @return
     */
    @Override
    public Page<SalesPaymentVO> getSalesPaymentVOPage(Page<SalesPayment> salesPaymentPage, HttpServletRequest request) {
        List<SalesPayment> salesPaymentList = salesPaymentPage.getRecords();
        Page<SalesPaymentVO> salesPaymentVOPage = new Page<>(salesPaymentPage.getCurrent(), salesPaymentPage.getSize(), salesPaymentPage.getTotal());
        if (CollUtil.isEmpty(salesPaymentList)) {
            return salesPaymentVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SalesPaymentVO> salesPaymentVOList = salesPaymentList.stream().map(salesPayment -> {
            return SalesPaymentVO.objToVo(salesPayment);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = salesPaymentList.stream().map(SalesPayment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> salesPaymentIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> salesPaymentIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> salesPaymentIdSet = salesPaymentList.stream().map(SalesPayment::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<SalesPaymentThumb> salesPaymentThumbQueryWrapper = new QueryWrapper<>();
            salesPaymentThumbQueryWrapper.in("salesPaymentId", salesPaymentIdSet);
            salesPaymentThumbQueryWrapper.eq("userId", loginUser.getId());
            List<SalesPaymentThumb> salesPaymentSalesPaymentThumbList = salesPaymentThumbMapper.selectList(salesPaymentThumbQueryWrapper);
            salesPaymentSalesPaymentThumbList.forEach(salesPaymentSalesPaymentThumb -> salesPaymentIdHasThumbMap.put(salesPaymentSalesPaymentThumb.getSalesPaymentId(), true));
            // 获取收藏
            QueryWrapper<SalesPaymentFavour> salesPaymentFavourQueryWrapper = new QueryWrapper<>();
            salesPaymentFavourQueryWrapper.in("salesPaymentId", salesPaymentIdSet);
            salesPaymentFavourQueryWrapper.eq("userId", loginUser.getId());
            List<SalesPaymentFavour> salesPaymentFavourList = salesPaymentFavourMapper.selectList(salesPaymentFavourQueryWrapper);
            salesPaymentFavourList.forEach(salesPaymentFavour -> salesPaymentIdHasFavourMap.put(salesPaymentFavour.getSalesPaymentId(), true));
        }
        // 填充信息
        salesPaymentVOList.forEach(salesPaymentVO -> {
            Long userId = salesPaymentVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            salesPaymentVO.setUser(userService.getUserVO(user));
            salesPaymentVO.setHasThumb(salesPaymentIdHasThumbMap.getOrDefault(salesPaymentVO.getId(), false));
            salesPaymentVO.setHasFavour(salesPaymentIdHasFavourMap.getOrDefault(salesPaymentVO.getId(), false));
        });
        // endregion

        salesPaymentVOPage.setRecords(salesPaymentVOList);
        return salesPaymentVOPage;
    }

}
