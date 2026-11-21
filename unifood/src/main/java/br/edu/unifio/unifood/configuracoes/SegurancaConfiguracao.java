package br.edu.unifio.unifood.configuracoes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SegurancaConfiguracao {
    private static final String ADMIN = "ADMIN";
    private static final String VENDEDOR = "VENDEDOR";
    private static final String CLIENTE = "CLIENTE";

    @Bean
    public SecurityFilterChain filtroSeguranca (HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(rotas -> rotas
                .requestMatchers("/error").permitAll()

                .requestMatchers(HttpMethod.POST, "/categorias/**").hasRole(ADMIN)
                .requestMatchers(HttpMethod.PUT, "/categorias/**").hasRole(ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/categorias/**").hasRole(ADMIN)

                .requestMatchers(HttpMethod.GET, "/vendedores/*/painel").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.POST, "/vendedores/**").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.PUT, "/vendedores/**").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/vendedores/**").hasRole(ADMIN)

                .requestMatchers(HttpMethod.POST, "/produtos/**", "/horarios/**").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.PUT, "/produtos/**", "/horarios/**").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/produtos/**").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/produtos/**", "/horarios/**").hasAnyRole(VENDEDOR, ADMIN)

                .requestMatchers(HttpMethod.POST, "/avaliacoes/**").hasAnyRole(CLIENTE, ADMIN)
                .requestMatchers(HttpMethod.PUT, "/avaliacoes/**").hasAnyRole(CLIENTE, ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/avaliacoes/**").hasAnyRole(CLIENTE, ADMIN)

                .requestMatchers(HttpMethod.PATCH, "/encomendas/*/confirmar", "/encomendas/*/entregar").hasAnyRole(VENDEDOR, ADMIN)
                .requestMatchers(HttpMethod.PATCH, "/encomendas/*/cancelar").authenticated()
                .requestMatchers(HttpMethod.POST, "/encomendas/**").hasAnyRole(CLIENTE, ADMIN)
                .requestMatchers(HttpMethod.PUT, "/encomendas/**").hasAnyRole(CLIENTE, ADMIN)
                .requestMatchers(HttpMethod.DELETE, "/encomendas/**").hasRole(ADMIN)

                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(naoAutenticado()))
            .exceptionHandling(excecoes -> excecoes
                .authenticationEntryPoint(naoAutenticado())
                .accessDeniedHandler(acessoNegado()));

        return http.build();
    }

    @Bean
    public UserDetailsService usuarios (PasswordEncoder codificador) {
        return new InMemoryUserDetailsManager(
            User.withUsername("admin").password(codificador.encode("admin123")).roles(ADMIN).build(),
            User.withUsername("vendedor").password(codificador.encode("vendedor123")).roles(VENDEDOR).build(),
            User.withUsername("cliente").password(codificador.encode("cliente123")).roles(CLIENTE).build());
    }

    @Bean
    public PasswordEncoder codificadorSenha () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource configuracaoCors () {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:4200", "https://projeto-integrador-iv-five.vercel.app"));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource origem = new UrlBasedCorsConfigurationSource();
        origem.registerCorsConfiguration("/**", cors);
        return origem;
    }

    private AuthenticationEntryPoint naoAutenticado () {
        return (requisicao, resposta, excecao) -> {
            resposta.setHeader("WWW-Authenticate", "Basic realm=\"UniFood\"");
            escreverErro(requisicao, resposta, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    "Autenticação necessária: informe usuário e senha válidos (HTTP Basic)");
        };
    }

    private AccessDeniedHandler acessoNegado () {
        return (requisicao, resposta, excecao) -> escreverErro(requisicao, resposta,
                HttpServletResponse.SC_FORBIDDEN, "Forbidden",
                "Acesso negado: o seu perfil não tem permissão para esta operação");
    }

    private static void escreverErro (HttpServletRequest requisicao, HttpServletResponse resposta,
                                      int status, String erro, String mensagem) throws IOException {
        resposta.setStatus(status);
        resposta.setContentType("application/json");
        resposta.setCharacterEncoding("UTF-8");
        resposta.getWriter().write("{"
                + "\"dataHora\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + status + ","
                + "\"erro\":\"" + erro + "\","
                + "\"mensagem\":\"" + mensagem + "\","
                + "\"caminho\":\"" + requisicao.getRequestURI().replace("\"", "") + "\""
                + "}");
    }
}
