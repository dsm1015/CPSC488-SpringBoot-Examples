
package edu.sru.cpsc.webshopping.secure;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Resource
	private UserDetailsServiceImpl userDetailsService;
	
    @Autowired
    private PasswordEncoder passwordEncoder;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}
	
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {

		http
			//.addFilterBefore(customAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class)
			.authorizeHttpRequests(authz -> authz
				/* begin - users visiting these URLs do not need authenticated because of .permitAll() */
				.requestMatchers(
					mvc.pattern("/")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/index")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/newUser")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/adduser")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/add-user-signup")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/emailverification")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/verify2FA")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/verify2FAPage")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/verify")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/error")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/userSecrets")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/forgotUser/*")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/findUser")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/answerQuestion")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/resetPassword")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/missionStatement")
				).permitAll()
				.requestMatchers(
					mvc.pattern("/FAQ")
				).permitAll()

				.requestMatchers(
					antMatcher("/resources/**")
				).permitAll()
				.requestMatchers(
					antMatcher("/static/**")
				).permitAll()
				.requestMatchers(
					antMatcher("/styles/**")
				).permitAll()
				.requestMatchers(
					antMatcher("/js/**")
				).permitAll()
				.requestMatchers(
					antMatcher("/images/**")
				).permitAll()
				.requestMatchers(
					antMatcher("/listingImages/**")
				).permitAll()
				.requestMatchers(
					antMatcher("data:realCaptcha/**")
				).permitAll()

				.anyRequest().authenticated() /* outside of permitAll() matches, authenticate any request */
			)
			.authenticationProvider(authenticationProvider())
			.formLogin(form -> form
				.loginPage("/login") /* link to login page */
				.successHandler(loginSuccessHandler)
				.permitAll()
			)
			.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/do_the_logout"))
                .logoutSuccessUrl("/index")
            );
		
		return http.build();
	}
}