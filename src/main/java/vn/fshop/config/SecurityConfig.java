package vn.fshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public UserDetailsService userDetailsService() {
        // Create an empty user details service to prevent auto-configuration
        // This disables the automatic password generation
        return new InMemoryUserDetailsManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/home", "/index", "/login", "/login/**", "/register", "/register/**",
                               "/shop", "/shop/**", "/product-detail", "/product-detail/**",
                               "/category", "/category/**", "/about", "/contact", "/my-account",
                               "/css/**", "/js/**", "/images/**", "/fonts/**", "/static/**").permitAll()
                .requestMatchers("/cart", "/add-item", "/remove-item-cart", "/clear-cart", "/cart-count").permitAll()
                .requestMatchers("/checkout", "/checkout/**", "/order-success").permitAll()
                .requestMatchers("/api/products/**", "/api/shop/**", "/api/cart/**", "/api/login", "/api/register", "/api/check-username", "/api/check-email").permitAll()
                .requestMatchers("/admin/**").permitAll()
                .requestMatchers("/account/**").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable()) // Disable Spring Security's form login
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/add-item", "/remove-item-cart", "/clear-cart", "/cart-count", "/login", "/admin/**", "/logout", "/api/**", "/checkout", "/checkout/**")
            );

        return http.build();
    }
}
