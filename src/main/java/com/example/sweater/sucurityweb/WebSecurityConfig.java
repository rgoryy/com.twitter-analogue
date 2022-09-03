package com.example.sweater.sucurityweb;

import net.bytebuddy.build.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{ //пересмотреть https://spring.io/guides/gs/securing-web/
    @Autowired
    private DataSource dataSource;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()//включаем авторизацию
                    .antMatchers("/", "/registration").permitAll()//для этого пути разрешен полный доступ
                    .anyRequest().authenticated()//для всех остальных нужна авторизация
                .and()
                    .formLogin()
                    .loginPage("/login")//указываем, что loginPage находится на меппинге "/login"
                    .permitAll()//разрешаем этим пользоваться всем
                .and()
                    .logout()
                    .permitAll()
                .and()
                    .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
                .usersByUsernameQuery("select username, password, active from usr where username = ?")
                .authoritiesByUsernameQuery("select u.username, ur.roles from  usr u inner join user_role ur on u.id = ur.user_id where u.username = ?");
    }
}

/*public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .antMatchers("/", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user =
                User.withDefaultPasswordEncoder()
                        .username("u")
                        .password("l")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user);
    }
}*/

