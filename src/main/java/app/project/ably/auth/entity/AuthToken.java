package app.project.ably.auth.entity;

import app.project.ably.common.entity.BaseTimeEntity;
import app.project.ably.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "auth_token")
public class AuthToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String accessToken;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpirationDate;

    public void updateToken(String accessToken, String refreshToken, LocalDateTime refreshTokenExpirationDate) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
    }
}
