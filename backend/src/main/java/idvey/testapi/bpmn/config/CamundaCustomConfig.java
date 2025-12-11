package idvey.testapi.bpmn.config;

import idvey.testapi.bpmn.security.BpmnProcessListener;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.impl.cfg.AbstractProcessEnginePlugin;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CamundaCustomConfig extends AbstractProcessEnginePlugin {

    private final BpmnProcessListener bpmnProcessListener;

    /**
     * Configure custom process listeners and other Camunda customizations
     */
    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // Task listeners will be registered via annotations in BPMN models
        // This is a placeholder for future Camunda customizations
    }
}
