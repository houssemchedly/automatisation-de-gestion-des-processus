package idvey.testapi.meet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    // Find meetings by type
    Page<Meeting> findByType(meetype type, Pageable pageable);

    // Find meetings by project
    Page<Meeting> findByProjetId(Integer projetId, Pageable pageable);

    // Find meetings by participant
    @Query("SELECT m FROM Meeting m JOIN m.participants p WHERE p.id = :userId")
    Page<Meeting> findByParticipantId(@Param("userId") Integer userId, Pageable pageable);

    // Find meetings by date range
    @Query("SELECT m FROM Meeting m WHERE m.date BETWEEN :startDate AND :endDate")
    Page<Meeting> findByDateBetween(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    Pageable pageable);

    // Find upcoming meetings
    @Query("SELECT m FROM Meeting m WHERE m.debut > :now ORDER BY m.debut ASC")
    List<Meeting> findUpcomingMeetings(@Param("now") LocalDateTime now);

    // Find today's meetings
    @Query("SELECT m FROM Meeting m WHERE m.date = :today ORDER BY m.debut ASC")
    List<Meeting> findTodaysMeetings(@Param("today") LocalDate today);

    // Find meetings by title containing
    @Query("SELECT m FROM Meeting m WHERE LOWER(m.titre) LIKE LOWER(CONCAT('%', :titre, '%'))")
    Page<Meeting> findByTitreContainingIgnoreCase(@Param("titre") String titre, Pageable pageable);

    // Find past meetings
    @Query("SELECT m FROM Meeting m WHERE m.fin < :now ORDER BY m.debut DESC")
    Page<Meeting> findPastMeetings(@Param("now") LocalDateTime now, Pageable pageable);

    // Find ongoing meetings
    @Query("SELECT m FROM Meeting m WHERE m.debut <= :now AND m.fin >= :now")
    List<Meeting> findOngoingMeetings(@Param("now") LocalDateTime now);

    // Check for conflicting meetings for a user
    @Query("SELECT COUNT(m) > 0 FROM Meeting m JOIN m.participants p WHERE p.id = :userId " +
            "AND ((m.debut <= :debut AND m.fin > :debut) OR (m.debut < :fin AND m.fin >= :fin) " +
            "OR (m.debut >= :debut AND m.fin <= :fin)) AND (:excludeMeetingId IS NULL OR m.id != :excludeMeetingId)")
    boolean hasConflictingMeeting(@Param("userId") Integer userId,
                                  @Param("debut") LocalDateTime debut,
                                  @Param("fin") LocalDateTime fin,
                                  @Param("excludeMeetingId") Integer excludeMeetingId);
}
