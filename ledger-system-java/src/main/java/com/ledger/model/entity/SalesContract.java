package com.ledger.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 销售合同表
 * 
 * @TableName sales_contract
 */
@TableName(value = "sales_contract")
@Data
public class SalesContract implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 0:集团内 1:集团外
     */
    @TableField(value = "project_source")
    private Integer projectSource;

    /**
     * 0:在建 1:完工整改 2:竣工验收 3:竣工结算 4:质保期内 5:完结
     */
    @TableField(value = "project_status")
    private Integer projectStatus;

    /**
     * 项目负责人
     */
    @TableField(value = "project_director")
    private String projectDirector;

    /**
     * 客户名称
     */
    @TableField(value = "customer")
    private String customer;

    /**
     * 项目名称
     */
    @TableField(value = "project_name")
    private String projectName;

    /**
     * 合同名称
     */
    @TableField(value = "contract_name")
    private String contractName;

    /**
     * 合同金额
     */
    @TableField(value = "contract_amount")
    private BigDecimal contractAmount;

    /**
     * 合同类型 0:建设施工 1:货物采购 2:技术服务
     */
    @TableField(value = "contract_type")
    private Integer contractType;

    /**
     * 招标方式 0:公开招标 1:竞争性磋商 2:询价比 3:直接委托 4:其他
     */
    @TableField(value = "bidding_method")
    private String biddingMethod;

    /**
     * 合同结算金额
     */
    @TableField(value = "settlement_amount")
    private BigDecimal settlementAmount;

    /**
     * 合同税率
     */
    @TableField(value = "tax_rate")
    private BigDecimal taxRate;

    /**
     * 合同签订日期
     */
    @TableField(value = "contract_date")
    private Date contractDate;

    /**
     * 合同工期
     */
    @TableField(value = "contract_period")
    private String contractPeriod;

    /**
     * 是否删除
     */
    @TableField(value = "isDelete")
    private Integer isDelete;

    /**
     * 创建人id
     */
    @TableField(value = "create_id")
    private Long createId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Timestamp updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SalesContract other = (SalesContract) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getYear() == null ? other.getYear() == null : this.getYear().equals(other.getYear()))
                && (this.getProjectSource() == null ? other.getProjectSource() == null
                        : this.getProjectSource().equals(other.getProjectSource()))
                && (this.getProjectStatus() == null ? other.getProjectStatus() == null
                        : this.getProjectStatus().equals(other.getProjectStatus()))
                && (this.getProjectDirector() == null ? other.getProjectDirector() == null
                        : this.getProjectDirector().equals(other.getProjectDirector()))
                && (this.getCustomer() == null ? other.getCustomer() == null
                        : this.getCustomer().equals(other.getCustomer()))
                && (this.getProjectName() == null ? other.getProjectName() == null
                        : this.getProjectName().equals(other.getProjectName()))
                && (this.getContractName() == null ? other.getContractName() == null
                        : this.getContractName().equals(other.getContractName()))
                && (this.getContractAmount() == null ? other.getContractAmount() == null
                        : this.getContractAmount().equals(other.getContractAmount()))
                && (this.getContractType() == null ? other.getContractType() == null
                        : this.getContractType().equals(other.getContractType()))
                && (this.getBiddingMethod() == null ? other.getBiddingMethod() == null
                        : this.getBiddingMethod().equals(other.getBiddingMethod()))
                && (this.getSettlementAmount() == null ? other.getSettlementAmount() == null
                        : this.getSettlementAmount().equals(other.getSettlementAmount()))
                && (this.getTaxRate() == null ? other.getTaxRate() == null
                        : this.getTaxRate().equals(other.getTaxRate()))
                && (this.getContractDate() == null ? other.getContractDate() == null
                        : this.getContractDate().equals(other.getContractDate()))
                && (this.getContractPeriod() == null ? other.getContractPeriod() == null
                        : this.getContractPeriod().equals(other.getContractPeriod()))
                && (this.getIsDelete() == null ? other.getIsDelete() == null
                        : this.getIsDelete().equals(other.getIsDelete()))
                && (this.getCreateId() == null ? other.getCreateId() == null
                        : this.getCreateId().equals(other.getCreateId()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null
                        : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null
                        : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getYear() == null) ? 0 : getYear().hashCode());
        result = prime * result + ((getProjectSource() == null) ? 0 : getProjectSource().hashCode());
        result = prime * result + ((getProjectStatus() == null) ? 0 : getProjectStatus().hashCode());
        result = prime * result + ((getProjectDirector() == null) ? 0 : getProjectDirector().hashCode());
        result = prime * result + ((getCustomer() == null) ? 0 : getCustomer().hashCode());
        result = prime * result + ((getProjectName() == null) ? 0 : getProjectName().hashCode());
        result = prime * result + ((getContractName() == null) ? 0 : getContractName().hashCode());
        result = prime * result + ((getContractAmount() == null) ? 0 : getContractAmount().hashCode());
        result = prime * result + ((getContractType() == null) ? 0 : getContractType().hashCode());
        result = prime * result + ((getBiddingMethod() == null) ? 0 : getBiddingMethod().hashCode());
        result = prime * result + ((getSettlementAmount() == null) ? 0 : getSettlementAmount().hashCode());
        result = prime * result + ((getTaxRate() == null) ? 0 : getTaxRate().hashCode());
        result = prime * result + ((getContractDate() == null) ? 0 : getContractDate().hashCode());
        result = prime * result + ((getContractPeriod() == null) ? 0 : getContractPeriod().hashCode());
        result = prime * result + ((getIsDelete() == null) ? 0 : getIsDelete().hashCode());
        result = prime * result + ((getCreateId() == null) ? 0 : getCreateId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", year=").append(year);
        sb.append(", projectSource=").append(projectSource);
        sb.append(", projectStatus=").append(projectStatus);
        sb.append(", projectDirector=").append(projectDirector);
        sb.append(", customer=").append(customer);
        sb.append(", projectName=").append(projectName);
        sb.append(", contractName=").append(contractName);
        sb.append(", contractAmount=").append(contractAmount);
        sb.append(", contractType=").append(contractType);
        sb.append(", biddingMethod=").append(biddingMethod);
        sb.append(", settlementAmount=").append(settlementAmount);
        sb.append(", taxRate=").append(taxRate);
        sb.append(", contractDate=").append(contractDate);
        sb.append(", contractPeriod=").append(contractPeriod);
        sb.append(", isDelete=").append(isDelete);
        sb.append(", createId=").append(createId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}