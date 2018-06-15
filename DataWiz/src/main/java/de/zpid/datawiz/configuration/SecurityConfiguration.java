package de.zpid.datawiz.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
	@Qualifier("LoginService")
	private UserDetailsService userDetailsService;

	protected JdbcTemplate jdbcTemplate;

	@Autowired
	public SecurityConfiguration(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		http.addFilterBefore(filter, CsrfFilter.class);
		http.authorizeRequests().antMatchers("/", "/home", "/register", "/login").permitAll().antMatchers("/admin/**").access("hasRole('ADMIN')")
		    .antMatchers("/panel/**", "/user/**", "/project/**", "/dmp/**", "/access/**").access("hasRole('USER') or hasRole('ADMIN')").and().formLogin()
		    .defaultSuccessUrl("/panel").loginPage("/login").permitAll().usernameParameter("email").passwordParameter("password").and().rememberMe()
		    .rememberMeParameter("remember-me").tokenRepository(persistentTokenRepository()).tokenValiditySeconds(86400).and().csrf().and()
		    .exceptionHandling().accessDeniedPage("/Access_Denied").and().logout().deleteCookies("remember-me").and().sessionManagement()
		    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).invalidSessionUrl("/login?session=timeout");
	}

	@Override
	public void configure(WebSecurity web){
		web.ignoring().antMatchers("/resources/**");
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
		if(jdbcTemplate.getDataSource() != null)
			tokenRepositoryImpl.setDataSource(jdbcTemplate.getDataSource());
		return tokenRepositoryImpl;
	}
}
