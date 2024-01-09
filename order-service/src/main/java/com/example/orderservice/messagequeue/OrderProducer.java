package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class OrderProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    List<Field> fields = Arrays.asList(new Field("string", true, "order_id"),
            new Field("string", true, "user_id"),
            new Field("string", true, "product_id"),
            new Field("int32", true, "qty"),
            new Field("int32", true, "unit_price"),
            new Field("int32", true, "total_price"));
    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("orders")
            .build();

    @Autowired
    public OrderProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /* 주문 서비스가 들어왔을때, ObjectMapper로 JSON 포맷으로 변경.  */
    public OrderDto send(String topic, OrderDto orederDto) {
        Payload payload = Payload.builder()
                .order_id(orederDto.getOrderId())
                .user_id(orederDto.getUserId())
                .product_id(orederDto.getProductId())
                .qty(orederDto.getQty())
                .unit_price(orederDto.getUnitPrice())
                .total_price(orederDto.getTotalPrice())
                .build();

        KafkaOrederDto kafkaOrederDto = new KafkaOrederDto(schema, payload);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(kafkaOrederDto);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Order Producer sent Data from the Order microservice: " + kafkaOrederDto);

        return orederDto;
    }
}
