package es.codeurjc.daw.library.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Team;
import es.codeurjc.daw.library.model.Role;
import es.codeurjc.daw.library.repository.TeamRepository;

@Service
public class RepositoryUserDetailsService implements UserDetailsService {

	@Autowired
	private TeamRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Team user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<GrantedAuthority> roles = new ArrayList<>();
		for (Role role : user.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
		}

		return new org.springframework.security.core.userdetails.User(user.getUsername(), 
				user.getEncodedPassword(), roles);

	}
}
