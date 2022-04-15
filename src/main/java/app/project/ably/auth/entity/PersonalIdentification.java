package app.project.ably.auth.entity;

import app.project.ably.common.entity.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
@Table(name = "personal_identification")
public class PersonalIdentification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String verificationCode;

    private String phoneNumber;

    private Boolean certificated;

    private LocalDateTime expirationDate;

    private LocalDateTime certificationDate;

    public void certificate(Boolean certificated, LocalDateTime certificationDate) {
        this.certificated = certificated;
        this.certificationDate = certificationDate;
    }
}
