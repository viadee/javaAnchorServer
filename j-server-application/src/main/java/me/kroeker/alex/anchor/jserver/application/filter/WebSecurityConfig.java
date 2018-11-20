package me.kroeker.alex.anchor.jserver.application.filter;

import me.kroeker.alex.anchor.jserver.api.exceptions.DataAccessException;
import me.kroeker.alex.anchor.jserver.business.AnchorBO;
import me.kroeker.alex.anchor.jserver.model.AnchorConfigDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AnchorBO anchorBO;

    public WebSecurityConfig(@Autowired AnchorBO anchorBO) {
        this.anchorBO = anchorBO;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and().csrf().disable();
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(HttpServletRequest httpServletRequest) throws DataAccessException {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        List<String> anchorConfig = this.anchorBO.getAnchorConfigs().stream().map(AnchorConfigDescription::getConfigName).collect(Collectors.toList());
        anchorConfig.addAll(Arrays.asList("Authorization", "Cache-Control", "Content-Type",
                "Model-Id", "Frame-Id"));
        corsConfiguration.setAllowedHeaders(anchorConfig);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

}
