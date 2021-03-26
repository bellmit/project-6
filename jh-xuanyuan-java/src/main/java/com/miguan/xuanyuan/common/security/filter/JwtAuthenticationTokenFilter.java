package com.miguan.xuanyuan.common.security.filter;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miguan.xuanyuan.common.config.SecurityAllowAccessConfig;
import com.miguan.xuanyuan.common.security.context.UserContext;
import com.miguan.xuanyuan.common.security.model.AuthenticationModel;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.util.CaptchaUtil;
import com.miguan.xuanyuan.common.util.ResultCode;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.common.security.service.JwtUserDetailsService;
import com.miguan.xuanyuan.common.security.utils.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

/**
 * 确保经过filter为一次请求
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final static String HEADER = "Authorization";
    private final static String BEARER = "Bearer ";

    @Value("${tokenCheck}")
    private String tokenCheck;


    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Resource
    private CaptchaUtil captchaUtil;

    @Value("${spring.profiles.active}")
    private String springEnv;


    @Autowired
    private SecurityAllowAccessConfig securityAllowAccessConfig;


    /**
     * 验证码校验失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String referer = (request.getHeader("Referer") == null ? "" : request.getHeader("Referer"));
        String uri = (request.getRequestURI() == null ? "" : request.getRequestURI());

        if(StringUtils.equals("/api/front/login", uri)
                && StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
//            if (springEnv.equals("prod")) {
//                String validateCode = request.getParameter("validateCode");
//                boolean validateCodeResult = captchaUtil.checkLoginValidateCode(request, validateCode);
//                if (!validateCodeResult) {
//                    getResponse(response, "验证码错误");
//                    return;
//                }
//            }

        }

        if(!Boolean.valueOf(tokenCheck)) {
            //不需要token校验配置
            chain.doFilter(request, response);
            return;
        }

        if (uri.endsWith("/swagger-ui.html") || uri.endsWith("/doc.html") || referer.endsWith("/swagger-ui.html") || referer.endsWith("/doc.html")) {
            //swagger的接口不需要做token校验
            chain.doFilter(request, response);
            return;
        }

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean boo = false;

        List<String> allowAccessList = securityAllowAccessConfig.getAllowAccessList();
        if (CollectionUtils.isNotEmpty(allowAccessList)) {
            for(int i = 0; i < allowAccessList.size(); i++){
                String urlPath = allowAccessList.get(i);
                if(antPathMatcher.match(urlPath, request.getRequestURI())){
                    boo = true;
                    break;
                }
            }
        }
//
//        for(int i = 0; i < arrUrl.length; i++){
//            if(antPathMatcher.match(arrUrl[i],request.getRequestURI())){
//                boo = true;
//                break;
//            }
//        }
        if(boo){
            chain.doFilter(request, response);
            return;
        }

        String authToken = null;

        //兼容在请求带token - 2021/03/24修改
        String tokenParam = request.getParameter("token");
        if (StringUtils.isNotEmpty(tokenParam)) {
            authToken = tokenParam;
        } else {
            String header = request.getHeader(HEADER);
            if (header == null || !header.startsWith(BEARER)) {
                getResponse(response,"token格式错误");
                return;
            }
            authToken = header.substring(BEARER.length());
        }

        if(JwtTokenUtil.isTokenExpired(authToken)){
            getResponse(response,"token过期！");
            return;
        }
        String username = jwtTokenUtil.getUsernameFromToken(authToken);
        if(StringUtils.isEmpty(username)){
            getResponse(response,"token错误！");
            return;
        }

        //把用户的信息填充到上下文中
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                UserContext.set((JwtUser) userDetails);
            }
        }
        chain.doFilter(request, response);
    }

    /**
     *  组装token验证失败的返回
     * @param res
     * @param msg
     * @return
     */
    private HttpServletResponse getResponse(HttpServletResponse res,String msg){
        ResultMap resultMap = ResultMap.error(ResultCode.FORBIDDEN.getCode(), msg);
        res.setContentType("Application/json;charset=UTF-8");
        Writer writer;
        try {
            writer = res.getWriter();
            writer.write(objectMapper.writeValueAsString(resultMap));
            writer.flush();
            writer.close();
        } catch (Exception o){
            o.printStackTrace();
        }
        return res;
    }

}
