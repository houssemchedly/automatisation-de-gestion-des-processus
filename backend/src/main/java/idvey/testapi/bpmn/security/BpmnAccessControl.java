package idvey.testapi.bpmn.security;

import idvey.testapi.bpmn.BpmnModel;
import idvey.testapi.bpmn.repository.BpmnModelRepository;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BpmnAccessControl {

    private final BpmnModelRepository bpmnModelRepository;
    private final UserRepository userRepository;

    /**
     * Check if user has permission to modify BPMN model
     * Only Scrum Master or creator can modify
     */
    public boolean canModifyBpmnModel(Long bpmnModelId, Integer userId) {
        BpmnModel model = bpmnModelRepository.findById(bpmnModelId).orElse(null);
        if (model == null) {
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        // Creator can always modify their DRAFT models
        if (model.getCreatedBy().getId().equals(userId) && model.getStatus() == BpmnModel.BpmnStatus.DRAFT) {
            return true;
        }

        // Check if user is Scrum Master
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SCRUM_MASTER"));
    }

    /**
     * Check if user has permission to deploy BPMN model
     * Only Scrum Master can deploy
     */
    public boolean canDeployBpmnModel(Long bpmnModelId, Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SCRUM_MASTER"));
    }

    /**
     * Check if user has permission to view BPMN model
     * All authenticated users can view active models
     */
    public boolean canViewBpmnModel(Long bpmnModelId, Integer userId) {
        BpmnModel model = bpmnModelRepository.findById(bpmnModelId).orElse(null);
        if (model == null) {
            return false;
        }

        // Anyone can view active/deployed models
        if (model.getStatus() == BpmnModel.BpmnStatus.ACTIVE ||
                model.getStatus() == BpmnModel.BpmnStatus.DEPLOYED) {
            return true;
        }

        // Only creator can view draft/archived models
        return model.getCreatedBy().getId().equals(userId);
    }

    /**
     * Check if user has permission to delete BPMN model
     * Only creator of DRAFT models can delete
     */
    public boolean canDeleteBpmnModel(Long bpmnModelId, Integer userId) {
        BpmnModel model = bpmnModelRepository.findById(bpmnModelId).orElse(null);
        if (model == null) {
            return false;
        }

        return model.getCreatedBy().getId().equals(userId) &&
                model.getStatus() == BpmnModel.BpmnStatus.DRAFT;
    }

    /**
     * Check if user is Scrum Master
     */
    public boolean isUserScrumMaster(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("SCRUM_MASTER"));
    }
}
