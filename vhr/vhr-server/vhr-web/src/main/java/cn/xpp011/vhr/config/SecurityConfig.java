package cn.xpp011.vhr.config;

import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.model.ResponseBean;
import cn.xpp011.vhr.service.HrService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;

    //自定义授权处理器
    @Autowired
    CustomAccessDecisionManager decisionManager;

    //自定义url过滤器
    @Autowired
    CustomFilterISMS filterISMS;

    //密码编码
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //配置前往登录页请求不用走Security
        //原因:  当我们前往登录url时/toLogin 需要判断当前路径需要什么角色  由于当前还未登录 同时/toLogin在数据库中未匹配到
        //则默认需要登录才能访问登录ur/toLogin  则重定向到登录页面请求/toLogin  自此导致死循环
        web.ignoring().antMatchers("/toLogin");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                //将自定义的url过滤器和授权处理器设置到Security
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(decisionManager);
                        object.setSecurityMetadataSource(filterISMS);
                        return object;
                    }
                })
        .and()
        .formLogin().loginPage("/toLogin").loginProcessingUrl("doLogin")
             //登录成功处理器
            .successHandler(myAuthenticationSuccessHandler())
             //登录失败处理器
            .failureHandler(myAuthenticationFailureHandler()).permitAll()
        .and()
        .logout().logoutUrl("/Logout").logoutSuccessHandler(new LogoutSuccessHandler() {//退出成功处理
            @Override
            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                Hr hr = (Hr) authentication.getPrincipal();
                ResponseBean ok = ResponseBean.ok("用户"+hr.getName()+"退出成功");
                String s = new ObjectMapper().writeValueAsString(ok);
                out.write(s);

                out.flush();
                out.close();
            }
        }).permitAll()
        .and()
        .csrf().disable()
        //异常处理器
        .exceptionHandling()
        //自定义授权入口 EntryPoint入口
        //也就是确保在访问页面时未登录（未授权的状态）直接去抛异常并返回json  而不是重定向到登录请求(/toLogin)
        .authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();

                //设置状态码401未登录的状态码
                response.setStatus(401);
                ResponseBean error = ResponseBean.error("访问失败!");

                //InsufficientAuthenticationException不足授权的异常
                if (authException instanceof InsufficientAuthenticationException){
                    error.setMsg("未授权!");
                }
                String s = new ObjectMapper().writeValueAsString(error);

                out.write(s);

                out.flush();
                out.close();
            }
        });

        http.addFilterAt(securityJsonLoginConfig(),UsernamePasswordAuthenticationFilter.class);
    }

    //SecurityJson登录的配置类
    @Bean
    SecurityJsonLoginConfig securityJsonLoginConfig() throws Exception {
        SecurityJsonLoginConfig filter = new SecurityJsonLoginConfig();
        //将自定义设置类设置登录成功处理器
        filter.setAuthenticationSuccessHandler(myAuthenticationSuccessHandler());
        //将自定义设置类设置登录失败处理器
        filter.setAuthenticationFailureHandler(myAuthenticationFailureHandler());
        //设置权限管理
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }


    //登录成功的处理器
    AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new AuthenticationSuccessHandler() {//登录成功处理
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                //getPrincipal  获取当前用户信息
                Hr hr = (Hr) authentication.getPrincipal();
                //返回前端时清除password
                hr.setPassword(null);
                ResponseBean ok = ResponseBean.ok("登录成功",hr);

                String s = new ObjectMapper().writeValueAsString(ok);

                out.write(s);

                out.flush();
                out.close();
            }
        };
    }

    //登录失败的处理器
    AuthenticationFailureHandler myAuthenticationFailureHandler(){
        return new AuthenticationFailureHandler() {//登录失败处理
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                //getPrincipal  获取当前用户信息
                ResponseBean error = ResponseBean.error("登录失败");

                if (exception instanceof BadCredentialsException){
                    error.setMsg("用户名或者密码错误");
                }else if (exception instanceof UsernameNotFoundException){
                    error.setMsg("用户名不存在");
                }else if (exception instanceof AccountExpiredException){
                    error.setMsg("账户被锁定");
                }else if (exception instanceof LockedException){
                    error.setMsg("账户被锁定");
                }else if (exception instanceof DisabledException){
                    error.setMsg("账户不可用");
                }else if (exception instanceof CredentialsExpiredException){
                    error.setMsg("证书过期");
                }

                String s = new ObjectMapper().writeValueAsString(error);

                out.write(s);

                out.flush();
                out.close();
            }
        };
    }
}
