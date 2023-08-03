package aml.summeruser.config;

import aml.summeruser.util.JwtUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        List<String> apiEndpoints = Arrays.asList("/register","/login", "/logout", "/refreshtoken", "/swagger-ui.html", "/v2/api-docs", "/webjars", "/swagger-resources", "/user-info");
        Predicate<HttpServletRequest> isApiSecured = r -> apiEndpoints.stream()
                .noneMatch(uri -> r.getRequestURI().contains(uri));
        if (isApiSecured.test(request)) {
            try {
                if (!hasAuthorizationHeader(request)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token");
                    return;
                }
                String accessToken = getAccessToken(request);
                if (!jwtUtil.validateToken(accessToken)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is invalid");
                    return;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }


    private boolean hasAuthorizationHeader(HttpServletRequest request) {
        String token = getAccessToken(request);
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        return true;
    }

    // get token from header request
    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
