package xy.bumbing.jwtapp.api.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xy.bumbing.jwtapp.api.security.CustomAccessDeniedHandler;
import xy.bumbing.jwtapp.api.security.CustomAuthenticationEntryPoint;
import xy.bumbing.jwtapp.api.security.JwtUsernamePasswordAuthenticationFilter;
import xy.bumbing.jwtapp.api.security.service.AuthService;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final AuthService authService;
    private final AuthProperties authProperties;

 
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity
                .cors().and()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다고 선언
                .and()
                .exceptionHandling().authenticationEntryPoint(this.customAuthenticationEntryPoint).accessDeniedHandler(this.customAccessDeniedHandler)
                .and()
                .addFilterAt(new JwtUsernamePasswordAuthenticationFilter(authProperties.getLogin().getUrl(),
                        authProperties.getLogin().getMethod(),
                        authProperties.getLogin().getUsernameParameter(),
                        authProperties.getLogin().getPasswordParameter(),
                        authProperties.getLogin().getRefreshTokenParameter(),
                        this.authService), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/user/login", "/api/user/sigunup", "/api/user/signin/**").permitAll()
                .antMatchers("/api/user/logout").authenticated()
                .antMatchers("/api/user/test/guest").permitAll()
                .antMatchers("/api/user/test/admin").hasRole("ADMIN")
                .antMatchers("/api/user/test/user").hasRole("USER");

    }
}
