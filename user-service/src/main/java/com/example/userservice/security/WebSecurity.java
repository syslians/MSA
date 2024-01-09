package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Autowired
    public WebSecurity(Environment env,UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env = env;
    }

    // HTTP 보안 설정 권한에 관련된 부분
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF(Cross-Site Request Forgery) 보안 비활성화
        http.csrf().disable();

        // "/users/**" 엔드포인트에 대한 접근 권한 설정
        http.authorizeRequests()
                .antMatchers("/**")
                .permitAll()
//                .hasIpAddress("192.168.0.63")
                .and()
                .addFilter(getAuthenticationFilter());

        // X-Frame-Options 비활성화
        http.headers().frameOptions().disable();
    }

    // 커스텀 인증 필터 반환
    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    // 인증에 관련된 부분 select pwd from users where email=?
    // db_pwd(encrypted) = input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
