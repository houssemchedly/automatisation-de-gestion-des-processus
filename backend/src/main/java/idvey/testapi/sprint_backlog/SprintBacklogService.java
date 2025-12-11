package idvey.testapi.sprint_backlog;

import idvey.testapi.common.PageResponse;
import idvey.testapi.productBacklog.ProductBacklog;
import idvey.testapi.productBacklog.ProductBacklogRepository;
import idvey.testapi.sprint.Sprint;
import idvey.testapi.sprint.SprintRepository;
import idvey.testapi.user_story.UserStory;
import idvey.testapi.user_story.UserStoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintBacklogService {

    private final SprintBacklogRepository sprintBacklogRepository;
    private final SprintRepository sprintRepository;
    private final UserStoryRepository userStoryRepository;
    private final ProductBacklogRepository productBacklogRepository;
    private final SprintBacklogMapper sprintBacklogMapper;

    public Integer save(SprintBacklogRequest request) {
        SprintBacklog sprintBacklog = SprintBacklog.builder()
                .build();

        if (request.getProductBacklogId() != null) {
            ProductBacklog productBacklog = productBacklogRepository.findById(request.getProductBacklogId())
                    .orElseThrow(() -> new EntityNotFoundException("Product Backlog not found with ID: " + request.getProductBacklogId()));
            sprintBacklog.setProductBacklog(productBacklog);
        }

        SprintBacklog savedSprintBacklog = sprintBacklogRepository.save(sprintBacklog);

        if (request.getUserStoryIds() != null && !request.getUserStoryIds().isEmpty()) {
            addUserStoriesToBacklog(savedSprintBacklog.getId(), request.getUserStoryIds());
        }

        return savedSprintBacklog.getId();
    }

    @Transactional(readOnly = true)
    public SprintBacklogResponse findById(Integer sprintBacklogId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findByIdWithUserStories(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        return getSprintBacklogResponseWithMetrics(sprintBacklog);
    }

    @Transactional(readOnly = true)
    public List<SprintBacklogResponse> findByProductBacklogId(Integer productBacklogId) {
        List<SprintBacklog> sprintBacklogs = sprintBacklogRepository.findByProductBacklogId(productBacklogId);
        return sprintBacklogs.stream()
                .map(this::getSprintBacklogResponseWithMetrics)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SprintBacklogResponse> findAllSprintBacklogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<SprintBacklog> sprintBacklogs = sprintBacklogRepository.findAllWithDetails(pageable);

        List<SprintBacklogResponse> sprintBacklogResponses = sprintBacklogs.stream()
                .map(this::getSprintBacklogResponseWithMetrics)
                .toList();

        return new PageResponse<>(sprintBacklogResponses,
                sprintBacklogs.getNumber(),
                sprintBacklogs.getSize(),
                sprintBacklogs.getTotalElements(),
                sprintBacklogs.getTotalPages(),
                sprintBacklogs.isFirst(),
                sprintBacklogs.isLast());
    }

    public void addUserStoryToBacklog(Integer sprintBacklogId, Integer userStoryId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findById(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        UserStory userStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new EntityNotFoundException("User Story not found with ID: " + userStoryId));

        if (userStory.getSprintBacklog() != null) {
            throw new IllegalStateException("User Story is already assigned to another Sprint Backlog");
        }

        userStory.setSprintBacklog(sprintBacklog);
        userStoryRepository.save(userStory);
    }

    public void addUserStoriesToBacklog(Integer sprintBacklogId, List<Integer> userStoryIds) {
        for (Integer userStoryId : userStoryIds) {
            addUserStoryToBacklog(sprintBacklogId, userStoryId);
        }
    }

    public void removeUserStoryFromBacklog(Integer sprintBacklogId, Integer userStoryId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findById(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        UserStory userStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new EntityNotFoundException("User Story not found with ID: " + userStoryId));

        if (userStory.getSprintBacklog() == null || !userStory.getSprintBacklog().getId().equals(sprintBacklogId)) {
            throw new IllegalStateException("User Story is not in this Sprint Backlog");
        }

        userStory.setSprintBacklog(null);
        userStoryRepository.save(userStory);
    }

    public void delete(Integer sprintBacklogId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findById(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        if (sprintBacklog.getUserStories() != null) {
            for (UserStory userStory : sprintBacklog.getUserStories()) {
                userStory.setSprintBacklog(null);
            }
            userStoryRepository.saveAll(sprintBacklog.getUserStories());
        }

        sprintBacklogRepository.delete(sprintBacklog);
    }

    public void addSprintToBacklog(Integer sprintBacklogId, Integer sprintId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findById(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + sprintId));

        if (sprint.getSprintBacklog() != null) {
            throw new IllegalStateException("Sprint is already assigned to another Sprint Backlog");
        }

        sprint.setSprintBacklog(sprintBacklog);
        sprintRepository.save(sprint);
    }

    public void removeSprintFromBacklog(Integer sprintBacklogId, Integer sprintId) {
        SprintBacklog sprintBacklog = sprintBacklogRepository.findById(sprintBacklogId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint Backlog not found with ID: " + sprintBacklogId));

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new EntityNotFoundException("Sprint not found with ID: " + sprintId));

        if (sprint.getSprintBacklog() == null || !sprint.getSprintBacklog().getId().equals(sprintBacklogId)) {
            throw new IllegalStateException("Sprint is not in this Sprint Backlog");
        }

        sprint.setSprintBacklog(null);
        sprintRepository.save(sprint);
    }

    private SprintBacklogResponse getSprintBacklogResponseWithMetrics(SprintBacklog sprintBacklog) {
        Long totalUserStories = sprintBacklogRepository.countUserStoriesBySprintBacklogId(sprintBacklog.getId());
        Long completedUserStories = sprintBacklogRepository.countCompletedUserStoriesBySprintBacklogId(sprintBacklog.getId());
        Integer totalStoryPoints = sprintBacklogRepository.sumStoryPointsBySprintBacklogId(sprintBacklog.getId());
        Integer completedStoryPoints = sprintBacklogRepository.sumCompletedStoryPointsBySprintBacklogId(sprintBacklog.getId());

        return sprintBacklogMapper.toSprintBacklogResponseWithMetrics(sprintBacklog, totalUserStories, completedUserStories, totalStoryPoints, completedStoryPoints);
    }
}
