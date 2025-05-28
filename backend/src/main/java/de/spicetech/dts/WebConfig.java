package de.spicetech.dts;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
        .setViewName("forward:/index.html");
    registry.addViewController("/")
        .setViewName("forward:/index.html");
    // registry.addViewController("/dts/**")
    // .setViewName("forward:/index.html");
    registry.addViewController("/illustration/**")
        .setViewName("forward:/index.html");
    registry.addViewController("/link/**")
        .setViewName("forward:/index.html");
    registry.addViewController("/article/**")
        .setViewName("forward:/index.html");
  }

  @Configuration
  @Profile("dev")
  public static class DevCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
          .allowedOrigins("http://localhost:5173")
          .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
  }

}
