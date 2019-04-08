package io.pivotal.ecosystem.mssqlserver.broker;

import io.pivotal.ecosystem.mssqlserver.broker.connector.SqlServerServiceInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/**").hasRole("ADMIN")
                .antMatchers("/error/**").permitAll()
                .antMatchers("/**").hasRole("ADMIN")
                .and()
                .httpBasic();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(Environment env) {
        return new InMemoryUserDetailsManager(adminUser(env));
    }

    private UserDetails adminUser(Environment env) {
        return User
                .withUsername(env.getProperty(SqlServerServiceInfo.SPRING_SECURITY_USER_KEY))
                .password("{noop}" + env.getProperty(SqlServerServiceInfo.SPRING_SECURITY_PW_KEY))
                .roles("ADMIN")
                .build();
    }
}