package idvey.testapi.user_story;


import idvey.testapi.tache.TacheMapper;
import org.springframework.stereotype.Service;

@Service
public class UserStoryMapper {

    private final TacheMapper tacheMapper;

    public UserStoryMapper(TacheMapper tacheMapper) {
        this.tacheMapper = tacheMapper;
    }

    public UserStory toUserStory(UserStoryRequest request) {
        return UserStory.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .priorite(request.getPriorite())
                .points(request.getPoints())
                .statut(request.getStatut() != null ? request.getStatut() : storystatut.A_FAIRE)
                .build();
    }

    public UserStoryResponse toUserStoryResponse(UserStory userStory) {
        int nombreTaches = userStory.getTaches() != null ? userStory.getTaches().size() : 0;
        int nombreTachesTerminees = userStory.getTaches() != null ?
                (int) userStory.getTaches().stream()
                        .filter(tache -> tache.getStatut() == idvey.testapi.tache.tachestatut.TERMINE)
                        .count() : 0;

        double pourcentageCompletion = nombreTaches > 0 ?
                (double) nombreTachesTerminees / nombreTaches * 100 : 0;

        return UserStoryResponse.builder()
                .id(userStory.getId())
                .titre(userStory.getTitre())
                .description(userStory.getDescription())
                .priorite(userStory.getPriorite())
                .points(userStory.getPoints())
                .statut(userStory.getStatut())
                .sprintBacklogId(userStory.getSprintBacklog() != null ?
                        userStory.getSprintBacklog().getId() : null)
                .taches(userStory.getTaches() != null ?
                        userStory.getTaches().stream()
                                .map(tacheMapper::toTacheResponse)
                                .toList() : null)
                .nombreTaches(nombreTaches)
                .nombreTachesTerminees(nombreTachesTerminees)
                .pourcentageCompletion(Math.round(pourcentageCompletion * 100.0) / 100.0)
                .build();
    }
}

