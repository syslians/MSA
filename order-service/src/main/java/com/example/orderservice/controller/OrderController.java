package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/order-service")
public class OrderController {

    // 환경 변수(Environment)와 OrderService 의존성 주입
    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;

    @Autowired
    public OrderController(OrderService orderService, Environment env, KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.orderService = orderService;
        this.env = env;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    // 서비스 상태 확인을 위한 엔드포인트
    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in OrderService on PORT %s",
                env.getProperty("local.server.port"));
    }

    // 주문 생성 엔드포인트
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        log.info("Before add order data");
        // JPA 관련 작업.ModelMapper를 사용하여 RequestOrder를 OrderDto로 변환
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        // 주문 서비스에 주문 생성 요청 JPA
        OrderDto createOrder = orderService.createOrder(orderDto);
        // OrderDto를 ResponseOrder로 변환
        ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class);

        /* 이 주문 kafka에 전달 */
//        orderDto.setOrderId(UUID.randomUUID().toString());
//        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        /* Kafka에 메시지 전달 */
        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);


        log.info("After add order data");
//        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class);
        // 성공 메시지 반환 (상태코드 201번 및 responseOrder 객체)
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);

    }

    // 사용자의 주문 목록 조회 엔드포인트
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) throws Exception{
        log.info("Before retrieve order data");
        // 주문 서비스를 통해 사용자의 주문 목록 조회
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        // 조회 결과를 ResponseOrder 리스트로 변환
        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

//        try {
//            Thread.sleep(1000);
//            throw new Exception("장애 발생");
//        } catch (InterruptedException ex) {
//            log.warn(ex.getMessage());
//        }

        log.info("After retrieve order data");
        // 성공 메시지 반환 (상태코드 200번 및 result 리스트)
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
