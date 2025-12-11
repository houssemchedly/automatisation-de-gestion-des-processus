package idvey.testapi.bpmn.service;

import idvey.testapi.bpmn.BpmnModel;
import idvey.testapi.bpmn.dto.BpmnModelRequest;
import idvey.testapi.bpmn.dto.BpmnModelResponse;
import idvey.testapi.bpmn.repository.BpmnModelRepository;
import idvey.testapi.exception.OperationNotPermittedException;
import idvey.testapi.user.User;
import idvey.testapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BpmnModelService {

    private final BpmnModelRepository bpmnModelRepository;
    private final UserRepository userRepository;
    private final RepositoryService camundaRepositoryService;
    private final BpmnModelMapper bpmnModelMapper;

    /**
     * Save a new BPMN model (Draft status)
     */
    public BpmnModelResponse saveBpmnModel(BpmnModelRequest request, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BpmnModel bpmnModel = BpmnModel.builder()
                .name(request.getName())
                .key(request.getKey())
                .description(request.getDescription())
                .bpmnXml(request.getBpmnXml())
                .diagram(request.getDiagram())
                .status(BpmnModel.BpmnStatus.DRAFT)
                .createdBy(user)
                .version(1L)
                .build();

        BpmnModel saved = bpmnModelRepository.save(bpmnModel);
        return bpmnModelMapper.toBpmnModelResponse(saved);
    }

    /**
     * Update existing BPMN model (only in DRAFT status)
     */
    public BpmnModelResponse updateBpmnModel(Long id, BpmnModelRequest request, Integer userId) {
        BpmnModel bpmnModel = bpmnModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));

        // Check authorization and status
        if (!bpmnModel.getCreatedBy().getId().equals(userId)) {
            throw new OperationNotPermittedException("You don't have permission to update this BPMN model");
        }

        if (bpmnModel.getStatus() != BpmnModel.BpmnStatus.DRAFT) {
            throw new OperationNotPermittedException("Only DRAFT models can be updated");
        }

        bpmnModel.setName(request.getName());
        bpmnModel.setDescription(request.getDescription());
        bpmnModel.setBpmnXml(request.getBpmnXml());
        bpmnModel.setDiagram(request.getDiagram());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        bpmnModel.setLastModifiedBy(user);

        BpmnModel updated = bpmnModelRepository.save(bpmnModel);
        return bpmnModelMapper.toBpmnModelResponse(updated);
    }

    /**
     * Deploy BPMN model to Camunda engine
     */
    public BpmnModelResponse deployBpmnModel(Long id, Integer userId) {
        BpmnModel bpmnModel = bpmnModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));

        // Check authorization
        if (!bpmnModel.getCreatedBy().getId().equals(userId)) {
            throw new OperationNotPermittedException("You don't have permission to deploy this BPMN model");
        }

        if (bpmnModel.getStatus() == BpmnModel.BpmnStatus.DEPLOYED ||
                bpmnModel.getStatus() == BpmnModel.BpmnStatus.ACTIVE) {
            throw new OperationNotPermittedException("Model is already deployed");
        }

        // Deploy to Camunda
        try {
            DeploymentBuilder deployment = camundaRepositoryService.createDeployment()
                    .addInputStream(bpmnModel.getKey() + ".bpmn",
                            new ByteArrayInputStream(bpmnModel.getBpmnXml().getBytes(StandardCharsets.UTF_8)))
                    .name(bpmnModel.getName());

            String deploymentId = deployment.deploy().getId();

            // Update model
            bpmnModel.setDeploymentId(deploymentId);
            bpmnModel.setStatus(BpmnModel.BpmnStatus.DEPLOYED);
            bpmnModel.setVersion(bpmnModel.getVersion() + 1);

            BpmnModel updated = bpmnModelRepository.save(bpmnModel);
            return bpmnModelMapper.toBpmnModelResponse(updated);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deploy BPMN model: " + e.getMessage(), e);
        }
    }

    /**
     * Get BPMN model by ID
     */
    public BpmnModelResponse getBpmnModelById(Long id) {
        BpmnModel bpmnModel = bpmnModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));
        return bpmnModelMapper.toBpmnModelResponse(bpmnModel);
    }

    /**
     * Get BPMN model by key
     */
    public BpmnModelResponse getBpmnModelByKey(String key) {
        BpmnModel bpmnModel = bpmnModelRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));
        return bpmnModelMapper.toBpmnModelResponse(bpmnModel);
    }

    /**
     * Get all active BPMN models
     */
    public List<BpmnModelResponse> getActiveBpmnModels() {
        List<BpmnModel> models = bpmnModelRepository.findByStatus(BpmnModel.BpmnStatus.ACTIVE);
        return models.stream()
                .map(bpmnModelMapper::toBpmnModelResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all BPMN models created by user
     */
    public List<BpmnModelResponse> getUserBpmnModels(Integer userId) {
        List<BpmnModel> models = bpmnModelRepository.findByCreatedByIdOrderByCreatedDateDesc(userId);
        return models.stream()
                .map(bpmnModelMapper::toBpmnModelResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete BPMN model (only DRAFT)
     */
    public void deleteBpmnModel(Long id, Integer userId) {
        BpmnModel bpmnModel = bpmnModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BPMN Model not found"));

        if (!bpmnModel.getCreatedBy().getId().equals(userId)) {
            throw new OperationNotPermittedException("You don't have permission to delete this BPMN model");
        }

        if (bpmnModel.getStatus() != BpmnModel.BpmnStatus.DRAFT) {
            throw new OperationNotPermittedException("Only DRAFT models can be deleted");
        }

        bpmnModelRepository.deleteById(id);
    }
}
