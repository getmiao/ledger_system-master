-- 为采购合同表添加实际成本字段
ALTER TABLE purchase_contract ADD COLUMN actual_cost DECIMAL(15,2) DEFAULT 0.00 COMMENT '实际成本';

-- 为采购合同表添加实际营收字段
ALTER TABLE purchase_contract ADD COLUMN actual_revenue DECIMAL(15,2) DEFAULT 0.00 COMMENT '实际营收';

-- 更新现有数据的实际成本字段（使用累计付款金额作为实际成本）
UPDATE purchase_contract pc 
SET actual_cost = (
    SELECT COALESCE(SUM(pp.account), 0) 
    FROM purchase_payment pp 
    WHERE pp.purchase_id = pc.id
); 