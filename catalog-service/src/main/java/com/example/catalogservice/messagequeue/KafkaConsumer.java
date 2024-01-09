package com.example.catalogservice.messagequeue;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
   CatalogRepository repository;

   @Autowired
   public KafkaConsumer(CatalogRepository repository) {
       this.repository = repository;
   }

   /* 명시해준 topic에 데이터가 전달이되면 이 메서드 실행.재고 수정. */
   @KafkaListener(topics = "example-catalog-topic")
   public void updateqty(String kakaMessage) {
        log.info("Kafka Message ->" + kakaMessage);

       Map<Object, Object> map = new HashMap<>();
       ObjectMapper mapper = new ObjectMapper();
       try {
           map = mapper.readValue(kakaMessage, new TypeReference<Map<Object, Object>>() {});
       } catch (JsonProcessingException ex) {
           ex.printStackTrace();
       }

       CatalogEntity entity = repository.findByProductId((String)map.get("productId"));

       if(entity != null) {
           entity.setStock(entity.getStock() - (Integer)map.get("qty"));
           repository.save(entity);
       }
   }
}
