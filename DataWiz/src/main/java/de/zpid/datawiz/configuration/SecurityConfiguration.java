package de.zpid.datawiz.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("userLoginService")
  UserDetailsService userDetailsService;

  @Autowired
  public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/", "/home", "/register", "/login").permitAll().antMatchers("/admin/**")
        .access("hasRole('ADMIN')").antMatchers("/panel/**", "/user/**").access("hasRole('USER') or hasRole('ADMIN')")
        .and().formLogin().defaultSuccessUrl("/panel").loginPage("/login").usernameParameter("email")
        .passwordParameter("password").and().csrf().and().exceptionHandling().accessDeniedPage("/Access_Denied");
  }
}
