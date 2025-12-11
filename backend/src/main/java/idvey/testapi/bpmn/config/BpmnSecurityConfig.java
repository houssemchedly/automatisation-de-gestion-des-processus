package idvey.testapi.bpmn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class BpmnSecurityConfig {


        // Configuration for method-level security with @PreAuthorize annotations


}
