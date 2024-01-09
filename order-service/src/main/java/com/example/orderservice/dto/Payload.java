package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

/* 실제 데이터 본문.마리아DB 컬럼과 일치 */
@Data
@Builder
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;
}
