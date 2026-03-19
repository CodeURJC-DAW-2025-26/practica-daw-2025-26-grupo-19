package es.codeurjc.daw.library.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.codeurjc.daw.library.security.jwt.JwtRequestFilter;
import es.codeurjc.daw.library.security.jwt.JwtTokenProvider;
import es.codeurjc.daw.library.security.jwt.UnauthorizedHandlerJwt;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	RepositoryUserDetailsService userDetailsService;

	@Autowired
	private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}


	@Bean
	@Order(1) 
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		
		http.authenticationProvider(authenticationProvider());
		
		http
			.securityMatcher("/api/**") 
			.exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
		
		http
			.authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(HttpMethod.POST,"/api/v1/**").permitAll()
					.requestMatchers(HttpMethod.DELETE,"/api/v1/**").permitAll()
					.requestMatchers(HttpMethod.PUT,"/api/v1/**").permitAll()
					.anyRequest().permitAll() 
			);
		
		http.formLogin(formLogin -> formLogin.disable());

		http.csrf(csrf -> csrf.disable());

		http.httpBasic(httpBasic -> httpBasic.disable());

		http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(new JwtRequestFilter(userDetailsService, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// ==========================================
	// CONFIGURACIÓN DE SEGURIDAD WEB (COOKIES/SESIÓN)
	// ==========================================
	@Bean
	@Order(2) // Orden 2: Capturará cualquier petición que no sea de la API
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

		http.authenticationProvider(authenticationProvider());

		http
				.authorizeHttpRequests(authorize -> authorize
						// PÁGINAS PÚBLICAS
						.requestMatchers("/").permitAll()
						.requestMatchers("/images/**").permitAll()
						.requestMatchers("/error").permitAll()      
    					.requestMatchers("/torneo/**").permitAll()
						.requestMatchers("/equipo/**").permitAll()
						.requestMatchers("/assets/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers("/css/**").permitAll()
						.requestMatchers("/js/**").permitAll()
						.requestMatchers("/forgot-password/**").permitAll()
						.requestMatchers("/register").permitAll()
						.requestMatchers("/reset-password/**").permitAll()
						.requestMatchers("/jugador/**").permitAll()
						.requestMatchers("/equipo/*").permitAll()
						
						// PÁGINAS PRIVADAS
						.requestMatchers("/admin-dashboard").hasAnyRole("ADMIN")
						.requestMatchers("/admin/**").hasAnyRole("ADMIN")
						.requestMatchers("/torneo/inscribir").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/equipo/jugador/nuevo").hasAnyRole("USER", "ADMIN")	
						.requestMatchers("/equipo/jugador/*/borrar").hasAnyRole("USER", "ADMIN")						
						.requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN")
						.anyRequest().authenticated()
				)
				.formLogin(formLogin -> formLogin
						.loginPage("/login")
						.failureUrl("/loginerror")
						.defaultSuccessUrl("/", true)
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.permitAll()
				);

		return http.build();
	}
}