package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PUBLIC PAGES
						.requestMatchers("/").permitAll()
						.requestMatchers("/images/**").permitAll()
						.requestMatchers("/error").permitAll()      // <-- AÑADE ESTO
    					.requestMatchers("/torneo/**").permitAll()
						.requestMatchers("/equipo/**").permitAll()
						.requestMatchers("/books/**").permitAll()
						.requestMatchers("/assets/**").permitAll() // Allow access to static resources
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/forgot-password/**").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/reset-password/**").permitAll()
						.requestMatchers("/jugador/**").permitAll()
						
						// PRIVATE PAGES
						.requestMatchers("/newbook").hasAnyRole("USER")
						.requestMatchers("/admin-dashboard").hasAnyRole("ADMIN")
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/torneo/inscribir").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/equipo/jugador/nuevo").hasAnyRole("USER", "ADMIN")	
						.requestMatchers("/equipo/jugador/*/borrar").hasAnyRole("USER", "ADMIN")						
						.requestMatchers("/editbook").hasAnyRole("USER")
						.requestMatchers("/equipo/*").permitAll()
						.requestMatchers("/editbook/*").hasAnyRole("USER")
						.requestMatchers("/removebook/*").hasAnyRole("ADMIN")
						.requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN"))
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						// .usernameParameter("email")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll());

		return http.build();
	}
}
