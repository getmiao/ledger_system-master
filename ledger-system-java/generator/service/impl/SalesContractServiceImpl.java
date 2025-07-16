package com.ledger.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.ErrorCode;
import com.ledger.constant.CommonConstant;
import com.ledger.exception.ThrowUtils;
import com.ledger.mapper.SalesContractMapper;
import com.ledger.model.dto.salesContract.SalesContractQueryRequest;
import com.ledger.model.entity.SalesContract;
import com.ledger.model.entity.SalesContractFavour;
import com.ledger.model.entity.SalesContractThumb;
import com.ledger.model.entity.User;
import com.ledger.model.vo.SalesContractVO;
import com.ledger.model.vo.UserVO;
import com.ledger.service.SalesContractService;
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
 * 销售合同服务实现
 *
   * @author 9K
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class SalesContractServiceImpl extends ServiceImpl<SalesContractMapper, SalesContract> implements SalesContractService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param salesContract
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validSalesContract(SalesContract salesContract, boolean add) {
        ThrowUtils.throwIf(salesContract == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = salesContract.getTitle();
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
     * @param salesContractQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SalesContract> getQueryWrapper(SalesContractQueryRequest salesContractQueryRequest) {
        QueryWrapper<SalesContract> queryWrapper = new QueryWrapper<>();
        if (salesContractQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = salesContractQueryRequest.getId();
        Long notId = salesContractQueryRequest.getNotId();
        String title = salesContractQueryRequest.getTitle();
        String content = salesContractQueryRequest.getContent();
        String searchText = salesContractQueryRequest.getSearchText();
        String sortField = salesContractQueryRequest.getSortField();
        String sortOrder = salesContractQueryRequest.getSortOrder();
        List<String> tagList = salesContractQueryRequest.getTags();
        Long userId = salesContractQueryRequest.getUserId();
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
     * 获取销售合同封装
     *
     * @param salesContract
     * @param request
     * @return
     */
    @Override
    public SalesContractVO getSalesContractVO(SalesContract salesContract, HttpServletRequest request) {
        // 对象转封装类
        SalesContractVO salesContractVO = SalesContractVO.objToVo(salesContract);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = salesContract.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        salesContractVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long salesContractId = salesContract.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<SalesContractThumb> salesContractThumbQueryWrapper = new QueryWrapper<>();
            salesContractThumbQueryWrapper.in("salesContractId", salesContractId);
            salesContractThumbQueryWrapper.eq("userId", loginUser.getId());
            SalesContractThumb salesContractThumb = salesContractThumbMapper.selectOne(salesContractThumbQueryWrapper);
            salesContractVO.setHasThumb(salesContractThumb != null);
            // 获取收藏
            QueryWrapper<SalesContractFavour> salesContractFavourQueryWrapper = new QueryWrapper<>();
            salesContractFavourQueryWrapper.in("salesContractId", salesContractId);
            salesContractFavourQueryWrapper.eq("userId", loginUser.getId());
            SalesContractFavour salesContractFavour = salesContractFavourMapper.selectOne(salesContractFavourQueryWrapper);
            salesContractVO.setHasFavour(salesContractFavour != null);
        }
        // endregion

        return salesContractVO;
    }

    /**
     * 分页获取销售合同封装
     *
     * @param salesContractPage
     * @param request
     * @return
     */
    @Override
    public Page<SalesContractVO> getSalesContractVOPage(Page<SalesContract> salesContractPage, HttpServletRequest request) {
        List<SalesContract> salesContractList = salesContractPage.getRecords();
        Page<SalesContractVO> salesContractVOPage = new Page<>(salesContractPage.getCurrent(), salesContractPage.getSize(), salesContractPage.getTotal());
        if (CollUtil.isEmpty(salesContractList)) {
            return salesContractVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SalesContractVO> salesContractVOList = salesContractList.stream().map(salesContract -> {
            return SalesContractVO.objToVo(salesContract);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = salesContractList.stream().map(SalesContract::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> salesContractIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> salesContractIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> salesContractIdSet = salesContractList.stream().map(SalesContract::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<SalesContractThumb> salesContractThumbQueryWrapper = new QueryWrapper<>();
            salesContractThumbQueryWrapper.in("salesContractId", salesContractIdSet);
            salesContractThumbQueryWrapper.eq("userId", loginUser.getId());
            List<SalesContractThumb> salesContractSalesContractThumbList = salesContractThumbMapper.selectList(salesContractThumbQueryWrapper);
            salesContractSalesContractThumbList.forEach(salesContractSalesContractThumb -> salesContractIdHasThumbMap.put(salesContractSalesContractThumb.getSalesContractId(), true));
            // 获取收藏
            QueryWrapper<SalesContractFavour> salesContractFavourQueryWrapper = new QueryWrapper<>();
            salesContractFavourQueryWrapper.in("salesContractId", salesContractIdSet);
            salesContractFavourQueryWrapper.eq("userId", loginUser.getId());
            List<SalesContractFavour> salesContractFavourList = salesContractFavourMapper.selectList(salesContractFavourQueryWrapper);
            salesContractFavourList.forEach(salesContractFavour -> salesContractIdHasFavourMap.put(salesContractFavour.getSalesContractId(), true));
        }
        // 填充信息
        salesContractVOList.forEach(salesContractVO -> {
            Long userId = salesContractVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            salesContractVO.setUser(userService.getUserVO(user));
            salesContractVO.setHasThumb(salesContractIdHasThumbMap.getOrDefault(salesContractVO.getId(), false));
            salesContractVO.setHasFavour(salesContractIdHasFavourMap.getOrDefault(salesContractVO.getId(), false));
        });
        // endregion

        salesContractVOPage.setRecords(salesContractVOList);
        return salesContractVOPage;
    }

}
