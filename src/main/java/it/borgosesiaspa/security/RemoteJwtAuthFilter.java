package it.borgosesiaspa.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RemoteJwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RemoteJwtAuthFilter.class);
    // private static final String AUTH_URL =
    // "http://localhost:2005/Apps/WebObjects/borgosesia.woa/wa/UserAction/authenticate";
    @Value("${remote.auth.url}")
    private String authUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> authResponse = restTemplate.exchange(
                    authUrl, HttpMethod.GET, entity, String.class);

            logger.info("Response: " + authResponse.getBody());

            JsonNode root = objectMapper.readTree(authResponse.getBody());

            logger.info("error: " + root.path("error").asBoolean());
            if (!root.path("error").asBoolean() && root.path("utente").path("attivo").asBoolean()) {
                String username = root.path("utente").path("username").asText();
                List<SimpleGrantedAuthority> authorities = Collections.emptyList();

                if (root.path("utente").has("extra") &&
                        root.path("utente").get("extra").has("permessi")) {

                    JsonNode permessiNode = root.path("utente").path("extra").path("permessi");
                    logger.info("permessiNode: " + permessiNode);
                    if (permessiNode.isArray()) {
                        List<String> permessi = objectMapper.convertValue(
                                permessiNode,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));

                        authorities = permessi.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                        logger.info("authorities: " + authorities);
                    }
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                System.out.println("authToken: " + authToken);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                logger.info("Utente autenticato: {}", username);
            }

        } catch (Exception e) {
            logger.warn("Errore nella validazione remota del token", e);
        }

        filterChain.doFilter(request, response);
    }
}