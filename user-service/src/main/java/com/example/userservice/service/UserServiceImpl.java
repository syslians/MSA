package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.jpa.UserRepository;
import com.example.userservice.vo.ResponseOrder;
import com.example.userservice.vo.ResponseUser;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    Environment env;
    RestTemplate restTemplate;
    OrderServiceClient orderServiceClient;

    CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true,
                true,
                true,
                true,
                new ArrayList<>()
                );
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           RestTemplate restTemplate,
                           Environment env,
                           OrderServiceClient orderServiceClient,
                           CircuitBreakerFactory circuitBreakerFactory) {
         this.userRepository = userRepository;
         this.passwordEncoder = passwordEncoder;
         this.restTemplate = restTemplate;
         this.env = env;
         this.orderServiceClient = orderServiceClient;
         this.circuitBreakerFactory = circuitBreakerFactory;
    }

    // 사용자 생성 메서드 구현
    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString()); // 사용자 식별자(UUID) 생성 및 할당
        ModelMapper mapper = new ModelMapper(); // ModelMapper를 사용하여 DTO를 Entity로 변환
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); // ModelMapper 설정: 엄격한 매칭 전략(STRICT) 사용
        UserEntity userEntity = mapper.map(userDto, UserEntity.class); // UserDto를 UserEntity로 변환

        userEntity.setEncryptedPwd(passwordEncoder.encode(userDto.getPwd())); // 보안을 위해 암호화된 비밀번호 할당

        userRepository.save(userEntity); // UserRepository를 사용하여 UserEntity를 데이터베이스에 저장

        UserDto returnUserDto = mapper.map(userEntity, UserDto.class); /* userEntity -> UserDto 변환 */

        return returnUserDto; /* UserDto 반환 */
    }

    @Override
    public UserDto getUserById(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("user not found");
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

//        List<ResponseOrder> orders = new ArrayList<>();
//        String orderUrl = String.format(env.getProperty("order_service.url"), userId);
//         ResponseEntity<List<ResponseOrder>> orderListResponse = restTemplate.exchange(orderUrl,
//                 HttpMethod.GET,
//                 null,
//                 new ParameterizedTypeReference<List<ResponseOrder>>() {
//        });
//
//        List<ResponseOrder> orderList = orderListResponse.getBody();

        /* Using Feign Client */
//        List<ResponseOrder> orderList = null;
//        try {
//            orderList = orderServiceClient.getOrders(userId);
//        } catch (FeignException e) {
//            log.error(e.getMessage());
//        }

        /* Error Decoder */
//        List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

        /* CircuitBreaker getOrders가 정상적으로 값을 받아온다면 해당 값 이용.이외 예외는 빈 배열 반환 */
        log.info("Before call orders microservice");
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orderList = circuitBreaker.run(() -> orderServiceClient.getOrders(userId),
                    throwable -> new ArrayList<>());
        log.info("After call orders microservice");


        userDto.setOrders(orderList);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }
}

