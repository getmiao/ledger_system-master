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
 * 采购合同表
 *
 * @TableName purchase_contract
 */
@TableName(value = "purchase_contract")
@Data
public class PurchaseContract implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 销售合同id
     */
    @TableField(value = "sales_id")
    private Long salesId;

    /**
     * 分包单位
     */
    @TableField(value = "subcontracting_unit")
    private String subcontractingUnit;

    /**
     * 项目负责人
     */
    @TableField(value = "director")
    private String director;

    /**
     * 联系方式
     */
    @TableField(value = "phone")
    private String phone;

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
     * 合同结算金额
     */
    @TableField(value = "settlement_amount")
    private BigDecimal settlementAmount;

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
     * 已收发票金额
     */
    @TableField(value = "received_invoice_amount")
    private BigDecimal receivedInvoiceAmount;

    /**
     * 实际营收
     */
    @TableField(value = "actual_revenue")
    private BigDecimal actualRevenue;

    /**
     * 采购方式
     */
    @TableField(value = "purchase_method")
    private String purchaseMethod;

    /**
     * 税率
     */
    @TableField(value = "tax_rate")
    private BigDecimal taxRate;

    /**
     * 实际成本
     */
    @TableField(value = "actual_cost")
    private BigDecimal actualCost;

    /**
     * 采购招标开始时间
     */
    @TableField(value = "purchase_start")
    private Date purchaseStart;

    /**
     * 采购招标结束时间
     */
    @TableField(value = "purchase_end")
    private Date purchaseEnd;

    /**
     * 支委会决策时间
     */
    @TableField(value = "committee_date")
    private Date committeeDate;

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
        PurchaseContract other = (PurchaseContract) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getSalesId() == null ? other.getSalesId() == null : this.getSalesId().equals(other.getSalesId()))
                && (this.getSubcontractingUnit() == null ? other.getSubcontractingUnit() == null : this.getSubcontractingUnit().equals(other.getSubcontractingUnit()))
                && (this.getDirector() == null ? other.getDirector() == null : this.getDirector().equals(other.getDirector()))
                && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
                && (this.getContractName() == null ? other.getContractName() == null : this.getContractName().equals(other.getContractName()))
                && (this.getContractAmount() == null ? other.getContractAmount() == null : this.getContractAmount().equals(other.getContractAmount()))
                && (this.getSettlementAmount() == null ? other.getSettlementAmount() == null : this.getSettlementAmount().equals(other.getSettlementAmount()))
                && (this.getContractDate() == null ? other.getContractDate() == null : this.getContractDate().equals(other.getContractDate()))
                && (this.getContractPeriod() == null ? other.getContractPeriod() == null : this.getContractPeriod().equals(other.getContractPeriod()))
                && (this.getPurchaseMethod() == null ? other.getPurchaseMethod() == null : this.getPurchaseMethod().equals(other.getPurchaseMethod()))
                && (this.getPurchaseStart() == null ? other.getPurchaseStart() == null : this.getPurchaseStart().equals(other.getPurchaseStart()))
                && (this.getPurchaseEnd() == null ? other.getPurchaseEnd() == null : this.getPurchaseEnd().equals(other.getPurchaseEnd()))
                && (this.getCommitteeDate() == null ? other.getCommitteeDate() == null : this.getCommitteeDate().equals(other.getCommitteeDate()))
                && (this.getCreateId() == null ? other.getCreateId() == null : this.getCreateId().equals(other.getCreateId()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSalesId() == null) ? 0 : getSalesId().hashCode());
        result = prime * result + ((getSubcontractingUnit() == null) ? 0 : getSubcontractingUnit().hashCode());
        result = prime * result + ((getDirector() == null) ? 0 : getDirector().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getContractName() == null) ? 0 : getContractName().hashCode());
        result = prime * result + ((getContractAmount() == null) ? 0 : getContractAmount().hashCode());
        result = prime * result + ((getSettlementAmount() == null) ? 0 : getSettlementAmount().hashCode());
        result = prime * result + ((getContractDate() == null) ? 0 : getContractDate().hashCode());
        result = prime * result + ((getContractPeriod() == null) ? 0 : getContractPeriod().hashCode());
        result = prime * result + ((getPurchaseMethod() == null) ? 0 : getPurchaseMethod().hashCode());
        result = prime * result + ((getPurchaseStart() == null) ? 0 : getPurchaseStart().hashCode());
        result = prime * result + ((getPurchaseEnd() == null) ? 0 : getPurchaseEnd().hashCode());
        result = prime * result + ((getCommitteeDate() == null) ? 0 : getCommitteeDate().hashCode());
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
        sb.append(", salesId=").append(salesId);
        sb.append(", subcontractingUnit=").append(subcontractingUnit);
        sb.append(", director=").append(director);
        sb.append(", phone=").append(phone);
        sb.append(", contractName=").append(contractName);
        sb.append(", contractAmount=").append(contractAmount);
        sb.append(", settlementAmount=").append(settlementAmount);
        sb.append(", contractDate=").append(contractDate);
        sb.append(", contractPeriod=").append(contractPeriod);
        sb.append(", purchaseMethod=").append(purchaseMethod);
        sb.append(", purchaseStart=").append(purchaseStart);
        sb.append(", purchaseEnd=").append(purchaseEnd);
        sb.append(", committeeDate=").append(committeeDate);
        sb.append(", createId=").append(createId);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}