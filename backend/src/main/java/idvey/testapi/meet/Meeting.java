package idvey.testapi.meet;


import idvey.testapi.projet.Projet;
import idvey.testapi.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Meeting {

    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private String titre;
    private String description;
    private meetype type;
    private LocalDate date;
    private LocalDateTime debut;
    private LocalDateTime fin;
    // Online meeting specific fields
    private String meetingUrl;
    private String meetingPassword;
    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "meeting_participants",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants;
}
