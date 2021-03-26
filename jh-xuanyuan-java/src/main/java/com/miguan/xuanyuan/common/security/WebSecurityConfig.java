package com.miguan.xuanyuan.common.security;

import com.miguan.xuanyuan.common.config.SecurityAllowAccessConfig;
import com.miguan.xuanyuan.common.security.filter.JwtAuthenticationProvider;
import com.miguan.xuanyuan.common.security.filter.JwtAuthenticationTokenFilter;
import com.miguan.xuanyuan.common.security.filter.MyUsernamePasswordAuthenticationFilter;
import com.miguan.xuanyuan.common.security.handler.*;
import com.miguan.xuanyuan.common.security.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限配置中心
 */
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)//是否支持web
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;//未登陆时返回 JSON 格式的数据给前端（否则为 html）

    @Resource
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;//自定义的登录成功处理器

    @Resource
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler; //自定义的登录失败处理器

    @Resource
    private MyLogoutSuccessHandler myLogoutSuccessHandler; //依赖注入自定义的注销成功的处理器

    @Resource
    private MyAccessDeniedHandler myAccessDeniedHandler;//注册没有权限的处理器

    @Resource
    private JwtUserDetailsService jwtUserDetailsService; //自定义user

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // 拦截token JWT 拦截器

    @Resource
    private JwtAuthenticationProvider jwtAuthenticationProvider; // 自定义登录

    @Value("${allowedOrigin}")
    private String allowedOrigin;

    @Autowired
    private SecurityAllowAccessConfig securityAllowAccessConfig;

    @Value("${spring.profiles.active}")
    private String springEnv;

    @Value("${loginCheck}")
    private String loginCheck;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //这里可启用我们自己的登陆验证逻辑,用户密码加密 放到jwtAuthenticationProvider中
        //auth.userDetailsService(jwtUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(jwtAuthenticationProvider);
    }

    private String[] allowedOriginList = new String[]{
            "xuanyuan.98du.com",
            "app.xymoby.98du.com",
            "app.xymoby.com",
            "localdev.xuanyuan.98du.com",
            "localdev.app.xymoby.98du.com",
            "localdev.app.xymoby.com",
    };


    private CorsConfigurationSource corsConfigurationSource() {

        String allowedOrigin = this.allowedOrigin;
        CorsConfigurationSource source =   new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedMethod("*");    //允许的请求方法，PSOT、GET等
        corsConfiguration.addAllowedHeader("*");
        if (springEnv.equals("prod")) {
            //同源配置，*表示任何请求都视为同源，若需指定ip和端口可以改为如“localhost：8080”，多个以“，”分隔；
            corsConfiguration.addAllowedOrigin(allowedOrigin);
            corsConfiguration.setAllowCredentials(true);
        } else {
            corsConfiguration.addAllowedOrigin("*");
        }

        ((UrlBasedCorsConfigurationSource) source).registerCorsConfiguration("/**",corsConfiguration); //配置允许跨域访问的url
        return source;
    }

    /**
     * 配置spring security的控制逻辑
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> allowAccessList = securityAllowAccessConfig.getAllowAccessList();
        String[] arrUrl = allowAccessList.toArray(new String[allowAccessList.size()]);

        // 新加入(cors) CSRF
//        http.cors().and().csrf().disable();
        http.cors().configurationSource(corsConfigurationSource()).and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 使用 JWT，关闭session

        if(!Boolean.valueOf(loginCheck)) {
            //不需要登录校验
            return;
        }
        //用户未登录
        http.httpBasic().authenticationEntryPoint(myAuthenticationEntryPoint);

        http.authorizeRequests()
                /** 设置任何用户可以访问的路径 **/
                .antMatchers(arrUrl).permitAll()
                /** 解决跨域 **/
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/front/**").access("hasAnyRole('ADMIN','WHITE')")
                .anyRequest()
//                .authenticated()
                .permitAll()
                .and()
                .formLogin()
//                .loginPage("/login.html")                      //登陆页面
//                .loginProcessingUrl("/api/front/login") //登陆请求处理接口
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/api/front/logout")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .permitAll();

        // 无权访问
        http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);

        //在执行MyUsernamePasswordAuthenticationFilter之前执行jwtAuthenticationTokenFilter
        http.addFilterBefore(jwtAuthenticationTokenFilter, MyUsernamePasswordAuthenticationFilter.class);

        //用重写的Filter替换掉原有的UsernamePasswordAuthenticationFilter
        http.addFilterAt(customAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 禁用缓存
        http.headers().cacheControl();
    }


    /**
     *  JSON登陆（注册登录的bean）
     */
    @Bean
    MyUsernamePasswordAuthenticationFilter customAuthenticationFilter() throws Exception {
        MyUsernamePasswordAuthenticationFilter filter = new MyUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(myAuthenticationFailureHandler);
        filter.setFilterProcessesUrl("/api/front/login");
        //这句很关键，重用WebSecurityConfigurerAdapter配置的AuthenticationManager，不然要自己组装AuthenticationManager
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}
