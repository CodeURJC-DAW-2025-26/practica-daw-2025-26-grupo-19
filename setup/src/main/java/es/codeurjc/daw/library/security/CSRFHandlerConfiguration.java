package es.codeurjc.daw.library.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CSRFHandlerConfiguration implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CSRFHandlerInterceptor());
	}
}

class CSRFHandlerInterceptor implements HandlerInterceptor {

	@Override
	public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler,
			final ModelAndView modelAndView) throws Exception {

		if (modelAndView != null) {
			// Safe way to obtain the token in Spring Security 6+
			CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            
			if (token != null) {
				// Inject it as "csrfToken" so it matches the HTML templates
				modelAndView.addObject("csrfToken", token.getToken());
			} else {
                // As a precaution, if there is no token (public pages without session), send an empty string
                modelAndView.addObject("csrfToken", "");
            }
		}
	}
}