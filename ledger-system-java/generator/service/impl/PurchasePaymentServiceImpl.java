package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.PurchasePaymentMapper;
import com.ledger.model.dto.purchasePayment.PurchasePaymentQueryRequest;
import com.ledger.model.entity.PurchasePayment;
import com.ledger.model.entity.PurchasePaymentFavour;
import com.ledger.model.entity.PurchasePaymentThumb;
import com.ledger.model.entity.User;
import com.ledger.model.vo.PurchasePaymentVO;
import com.ledger.model.vo.UserVO;
import com.ledger.service.PurchasePaymentService;
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
 * 采购回款服务实现
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class PurchasePaymentServiceImpl extends ServiceImpl<PurchasePaymentMapper, PurchasePayment> implements PurchasePaymentService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param purchasePayment
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validPurchasePayment(PurchasePayment purchasePayment, boolean add) {
        ThrowUtils.throwIf(purchasePayment == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = purchasePayment.getTitle();
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
     * @param purchasePaymentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PurchasePayment> getQueryWrapper(PurchasePaymentQueryRequest purchasePaymentQueryRequest) {
        QueryWrapper<PurchasePayment> queryWrapper = new QueryWrapper<>();
        if (purchasePaymentQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = purchasePaymentQueryRequest.getId();
        Long notId = purchasePaymentQueryRequest.getNotId();
        String title = purchasePaymentQueryRequest.getTitle();
        String content = purchasePaymentQueryRequest.getContent();
        String searchText = purchasePaymentQueryRequest.getSearchText();
        String sortField = purchasePaymentQueryRequest.getSortField();
        String sortOrder = purchasePaymentQueryRequest.getSortOrder();
        List<String> tagList = purchasePaymentQueryRequest.getTags();
        Long userId = purchasePaymentQueryRequest.getUserId();
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
     * 获取采购回款封装
     *
     * @param purchasePayment
     * @param request
     * @return
     */
    @Override
    public PurchasePaymentVO getPurchasePaymentVO(PurchasePayment purchasePayment, HttpServletRequest request) {
        // 对象转封装类
        PurchasePaymentVO purchasePaymentVO = PurchasePaymentVO.objToVo(purchasePayment);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = purchasePayment.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        purchasePaymentVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long purchasePaymentId = purchasePayment.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<PurchasePaymentThumb> purchasePaymentThumbQueryWrapper = new QueryWrapper<>();
            purchasePaymentThumbQueryWrapper.in("purchasePaymentId", purchasePaymentId);
            purchasePaymentThumbQueryWrapper.eq("userId", loginUser.getId());
            PurchasePaymentThumb purchasePaymentThumb = purchasePaymentThumbMapper.selectOne(purchasePaymentThumbQueryWrapper);
            purchasePaymentVO.setHasThumb(purchasePaymentThumb != null);
            // 获取收藏
            QueryWrapper<PurchasePaymentFavour> purchasePaymentFavourQueryWrapper = new QueryWrapper<>();
            purchasePaymentFavourQueryWrapper.in("purchasePaymentId", purchasePaymentId);
            purchasePaymentFavourQueryWrapper.eq("userId", loginUser.getId());
            PurchasePaymentFavour purchasePaymentFavour = purchasePaymentFavourMapper.selectOne(purchasePaymentFavourQueryWrapper);
            purchasePaymentVO.setHasFavour(purchasePaymentFavour != null);
        }
        // endregion

        return purchasePaymentVO;
    }

    /**
     * 分页获取采购回款封装
     *
     * @param purchasePaymentPage
     * @param request
     * @return
     */
    @Override
    public Page<PurchasePaymentVO> getPurchasePaymentVOPage(Page<PurchasePayment> purchasePaymentPage, HttpServletRequest request) {
        List<PurchasePayment> purchasePaymentList = purchasePaymentPage.getRecords();
        Page<PurchasePaymentVO> purchasePaymentVOPage = new Page<>(purchasePaymentPage.getCurrent(), purchasePaymentPage.getSize(), purchasePaymentPage.getTotal());
        if (CollUtil.isEmpty(purchasePaymentList)) {
            return purchasePaymentVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PurchasePaymentVO> purchasePaymentVOList = purchasePaymentList.stream().map(purchasePayment -> {
            return PurchasePaymentVO.objToVo(purchasePayment);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = purchasePaymentList.stream().map(PurchasePayment::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> purchasePaymentIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> purchasePaymentIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> purchasePaymentIdSet = purchasePaymentList.stream().map(PurchasePayment::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<PurchasePaymentThumb> purchasePaymentThumbQueryWrapper = new QueryWrapper<>();
            purchasePaymentThumbQueryWrapper.in("purchasePaymentId", purchasePaymentIdSet);
            purchasePaymentThumbQueryWrapper.eq("userId", loginUser.getId());
            List<PurchasePaymentThumb> purchasePaymentPurchasePaymentThumbList = purchasePaymentThumbMapper.selectList(purchasePaymentThumbQueryWrapper);
            purchasePaymentPurchasePaymentThumbList.forEach(purchasePaymentPurchasePaymentThumb -> purchasePaymentIdHasThumbMap.put(purchasePaymentPurchasePaymentThumb.getPurchasePaymentId(), true));
            // 获取收藏
            QueryWrapper<PurchasePaymentFavour> purchasePaymentFavourQueryWrapper = new QueryWrapper<>();
            purchasePaymentFavourQueryWrapper.in("purchasePaymentId", purchasePaymentIdSet);
            purchasePaymentFavourQueryWrapper.eq("userId", loginUser.getId());
            List<PurchasePaymentFavour> purchasePaymentFavourList = purchasePaymentFavourMapper.selectList(purchasePaymentFavourQueryWrapper);
            purchasePaymentFavourList.forEach(purchasePaymentFavour -> purchasePaymentIdHasFavourMap.put(purchasePaymentFavour.getPurchasePaymentId(), true));
        }
        // 填充信息
        purchasePaymentVOList.forEach(purchasePaymentVO -> {
            Long userId = purchasePaymentVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            purchasePaymentVO.setUser(userService.getUserVO(user));
            purchasePaymentVO.setHasThumb(purchasePaymentIdHasThumbMap.getOrDefault(purchasePaymentVO.getId(), false));
            purchasePaymentVO.setHasFavour(purchasePaymentIdHasFavourMap.getOrDefault(purchasePaymentVO.getId(), false));
        });
        // endregion

        purchasePaymentVOPage.setRecords(purchasePaymentVOList);
        return purchasePaymentVOPage;
    }

}
