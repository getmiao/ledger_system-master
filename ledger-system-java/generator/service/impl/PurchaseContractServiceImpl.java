package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.PurchaseContractMapper;
import com.ledger.model.dto.purchaseContract.PurchaseContractQueryRequest;
import com.ledger.model.entity.PurchaseContract;
import com.ledger.model.entity.PurchaseContractFavour;
import com.ledger.model.entity.PurchaseContractThumb;
import com.ledger.model.entity.User;
import com.ledger.model.vo.PurchaseContractVO;
import com.ledger.model.vo.UserVO;
import com.ledger.service.PurchaseContractService;
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
 * 采购合同服务实现
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class PurchaseContractServiceImpl extends ServiceImpl<PurchaseContractMapper, PurchaseContract> implements PurchaseContractService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param purchaseContract
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validPurchaseContract(PurchaseContract purchaseContract, boolean add) {
        ThrowUtils.throwIf(purchaseContract == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = purchaseContract.getTitle();
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
     * @param purchaseContractQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<PurchaseContract> getQueryWrapper(PurchaseContractQueryRequest purchaseContractQueryRequest) {
        QueryWrapper<PurchaseContract> queryWrapper = new QueryWrapper<>();
        if (purchaseContractQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = purchaseContractQueryRequest.getId();
        Long notId = purchaseContractQueryRequest.getNotId();
        String title = purchaseContractQueryRequest.getTitle();
        String content = purchaseContractQueryRequest.getContent();
        String searchText = purchaseContractQueryRequest.getSearchText();
        String sortField = purchaseContractQueryRequest.getSortField();
        String sortOrder = purchaseContractQueryRequest.getSortOrder();
        List<String> tagList = purchaseContractQueryRequest.getTags();
        Long userId = purchaseContractQueryRequest.getUserId();
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
     * 获取采购合同封装
     *
     * @param purchaseContract
     * @param request
     * @return
     */
    @Override
    public PurchaseContractVO getPurchaseContractVO(PurchaseContract purchaseContract, HttpServletRequest request) {
        // 对象转封装类
        PurchaseContractVO purchaseContractVO = PurchaseContractVO.objToVo(purchaseContract);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = purchaseContract.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        purchaseContractVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long purchaseContractId = purchaseContract.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<PurchaseContractThumb> purchaseContractThumbQueryWrapper = new QueryWrapper<>();
            purchaseContractThumbQueryWrapper.in("purchaseContractId", purchaseContractId);
            purchaseContractThumbQueryWrapper.eq("userId", loginUser.getId());
            PurchaseContractThumb purchaseContractThumb = purchaseContractThumbMapper.selectOne(purchaseContractThumbQueryWrapper);
            purchaseContractVO.setHasThumb(purchaseContractThumb != null);
            // 获取收藏
            QueryWrapper<PurchaseContractFavour> purchaseContractFavourQueryWrapper = new QueryWrapper<>();
            purchaseContractFavourQueryWrapper.in("purchaseContractId", purchaseContractId);
            purchaseContractFavourQueryWrapper.eq("userId", loginUser.getId());
            PurchaseContractFavour purchaseContractFavour = purchaseContractFavourMapper.selectOne(purchaseContractFavourQueryWrapper);
            purchaseContractVO.setHasFavour(purchaseContractFavour != null);
        }
        // endregion

        return purchaseContractVO;
    }

    /**
     * 分页获取采购合同封装
     *
     * @param purchaseContractPage
     * @param request
     * @return
     */
    @Override
    public Page<PurchaseContractVO> getPurchaseContractVOPage(Page<PurchaseContract> purchaseContractPage, HttpServletRequest request) {
        List<PurchaseContract> purchaseContractList = purchaseContractPage.getRecords();
        Page<PurchaseContractVO> purchaseContractVOPage = new Page<>(purchaseContractPage.getCurrent(), purchaseContractPage.getSize(), purchaseContractPage.getTotal());
        if (CollUtil.isEmpty(purchaseContractList)) {
            return purchaseContractVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PurchaseContractVO> purchaseContractVOList = purchaseContractList.stream().map(purchaseContract -> {
            return PurchaseContractVO.objToVo(purchaseContract);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = purchaseContractList.stream().map(PurchaseContract::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> purchaseContractIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> purchaseContractIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> purchaseContractIdSet = purchaseContractList.stream().map(PurchaseContract::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<PurchaseContractThumb> purchaseContractThumbQueryWrapper = new QueryWrapper<>();
            purchaseContractThumbQueryWrapper.in("purchaseContractId", purchaseContractIdSet);
            purchaseContractThumbQueryWrapper.eq("userId", loginUser.getId());
            List<PurchaseContractThumb> purchaseContractPurchaseContractThumbList = purchaseContractThumbMapper.selectList(purchaseContractThumbQueryWrapper);
            purchaseContractPurchaseContractThumbList.forEach(purchaseContractPurchaseContractThumb -> purchaseContractIdHasThumbMap.put(purchaseContractPurchaseContractThumb.getPurchaseContractId(), true));
            // 获取收藏
            QueryWrapper<PurchaseContractFavour> purchaseContractFavourQueryWrapper = new QueryWrapper<>();
            purchaseContractFavourQueryWrapper.in("purchaseContractId", purchaseContractIdSet);
            purchaseContractFavourQueryWrapper.eq("userId", loginUser.getId());
            List<PurchaseContractFavour> purchaseContractFavourList = purchaseContractFavourMapper.selectList(purchaseContractFavourQueryWrapper);
            purchaseContractFavourList.forEach(purchaseContractFavour -> purchaseContractIdHasFavourMap.put(purchaseContractFavour.getPurchaseContractId(), true));
        }
        // 填充信息
        purchaseContractVOList.forEach(purchaseContractVO -> {
            Long userId = purchaseContractVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            purchaseContractVO.setUser(userService.getUserVO(user));
            purchaseContractVO.setHasThumb(purchaseContractIdHasThumbMap.getOrDefault(purchaseContractVO.getId(), false));
            purchaseContractVO.setHasFavour(purchaseContractIdHasFavourMap.getOrDefault(purchaseContractVO.getId(), false));
        });
        // endregion

        purchaseContractVOPage.setRecords(purchaseContractVOList);
        return purchaseContractVOPage;
    }

}
