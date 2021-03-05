package com.lnssi.microserviceX.web.util.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.lnssi.microserviceX.model.security.Role;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
 
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
//    	 http.csrf().disable()
//    	 	.authorizeRequests().antMatchers("/**").permitAll();
 
    	
    	// Disable CSRF
        http.csrf().disable()
                // Only admin can perform HTTP delete operation
                .authorizeRequests()
                	//.antMatchers(HttpMethod.DELETE).hasRole(Role.ADMIN)
                // any authenticated user can perform all other operations
                	.antMatchers("/").hasAnyRole(Role.ADMIN)
                	.antMatchers("/swagger-ui/**").hasAnyRole(Role.ADMIN).and().httpBasic()
                // Permit all other request without authentication
                .and().authorizeRequests().anyRequest().permitAll()
                // We don't need sessions to be created.
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                                
      
    }
	
	@Override
	public UserDetailsService userDetailsService() {
		return userDetailsService;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
