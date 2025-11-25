package com.niu.core.mapper.basic;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.basic.Invoice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发票表 Mapper
 */
@Mapper
public interface InvoiceMapper extends MPJBaseMapper<Invoice> {
}
