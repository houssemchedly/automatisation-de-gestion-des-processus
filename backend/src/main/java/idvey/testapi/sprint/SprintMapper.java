package idvey.testapi.sprint;

import idvey.testapi.sprint_backlog.SprintBacklog;
import idvey.testapi.user_story.UserStory;
import idvey.testapi.user_story.storystatut;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class SprintMapper {

    public Sprint toSprint(SprintRequest request) {
        return Sprint.builder()
                .nom(request.getNom())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .objectif(request.getObjectif())
                .statut(sprintstatut.A_FAIRE)
                .build();
    }

    public SprintResponse toSprintResponse(Sprint sprint) {
        var builder = SprintResponse.builder()
                .id(sprint.getId())
                .nom(sprint.getNom())
                .dateDebut(sprint.getDateDebut())
                .dateFin(sprint.getDateFin())
                .statut(sprint.getStatut())
                .objectif(sprint.getObjectif())
                .durationInDays(calculateDurationInDays(sprint.getDateDebut(), sprint.getDateFin()))
                .daysRemaining(calculateDaysRemaining(sprint.getDateFin()))
                .isActive(isSprintActive(sprint))
                .isOverdue(isSprintOverdue(sprint));

        // Add project information
        if (sprint.getProjet() != null) {
            builder.projetId(sprint.getProjet().getId())
                    .projetNom(sprint.getProjet().getNom());
        }

        // Add sprint backlog information
        if (sprint.getSprintBacklog() != null) {
            SprintBacklog backlog = sprint.getSprintBacklog();
            builder.sprintBacklogId(backlog.getId());

            if (backlog.getUserStories() != null && !backlog.getUserStories().isEmpty()) {
                int totalStories = backlog.getUserStories().size();
                int completedStories = (int) backlog.getUserStories().stream()
                        .filter(story -> story.getStatut() == storystatut.TERMINE)
                        .count();

                int totalPoints = backlog.getUserStories().stream()
                        .mapToInt(UserStory::getPoints)
                        .sum();

                int completedPoints = backlog.getUserStories().stream()
                        .filter(story -> story.getStatut() == storystatut.TERMINE)
                        .mapToInt(UserStory::getPoints)
                        .sum();

                builder.totalUserStories(totalStories)
                        .completedUserStories(completedStories)
                        .completionPercentage(totalStories > 0 ? (double) completedStories / totalStories * 100 : 0.0)
                        .totalStoryPoints(totalPoints)
                        .completedStoryPoints(completedPoints)
                        .storyPointsCompletionPercentage(totalPoints > 0 ? (double) completedPoints / totalPoints * 100 : 0.0);
            } else {
                builder.totalUserStories(0)
                        .completedUserStories(0)
                        .completionPercentage(0.0)
                        .totalStoryPoints(0)
                        .completedStoryPoints(0)
                        .storyPointsCompletionPercentage(0.0);
            }
        }

        return builder.build();
    }

    private Integer calculateDurationInDays(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut != null && dateFin != null) {
            return (int) ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
        }
        return 0;
    }

    private Integer calculateDaysRemaining(LocalDate dateFin) {
        if (dateFin != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), dateFin);
            return (int) Math.max(0, days);
        }
        return 0;
    }

    private Boolean isSprintActive(Sprint sprint) {
        LocalDate now = LocalDate.now();
        return sprint.getDateDebut() != null && sprint.getDateFin() != null &&
                !now.isBefore(sprint.getDateDebut()) && !now.isAfter(sprint.getDateFin()) &&
                sprint.getStatut() == sprintstatut.EN_COURS;
    }

    private Boolean isSprintOverdue(Sprint sprint) {
        return sprint.getDateFin() != null &&
                LocalDate.now().isAfter(sprint.getDateFin()) &&
                sprint.getStatut() != sprintstatut.TERMINE;
    }
}
