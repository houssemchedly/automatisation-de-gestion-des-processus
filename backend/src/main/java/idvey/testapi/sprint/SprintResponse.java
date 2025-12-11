package idvey.testapi.sprint;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class SprintResponse {

    private Integer id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private sprintstatut statut;
    private String objectif;

    // Projet information
    private Integer projetId;
    private String projetNom;


    // Sprint Backlog information
    private Integer sprintBacklogId;
    private Integer totalUserStories;
    private Integer completedUserStories;
    private Double completionPercentage;
    private Integer totalStoryPoints;
    private Integer completedStoryPoints;
    private Double storyPointsCompletionPercentage;


    // Sprint duration and progress
    private Integer durationInDays;
    private Integer daysRemaining;
    private Boolean isActive;
    private Boolean isOverdue;
}
