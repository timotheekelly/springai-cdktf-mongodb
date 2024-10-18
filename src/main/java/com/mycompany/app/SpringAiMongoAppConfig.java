package com.mycompany.app;

import com.hashicorp.cdktf.App;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringAiMongoAppConfig {

    @Bean
    public App cdktfApp() {
        return new App();  // Create the App instance required for MainStack
    }

    @Bean
    public MainStack mainStack(App cdktfApp) {
        return new MainStack(cdktfApp, "terraform-springai");  // Pass both arguments
    }
}
