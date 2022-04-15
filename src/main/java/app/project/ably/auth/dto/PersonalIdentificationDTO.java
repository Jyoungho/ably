package app.project.ably.auth.dto;

import app.project.ably.auth.entity.PersonalIdentification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder @AllArgsConstructor @NoArgsConstructor
public class PersonalIdentificationDTO {

    private String verificationCode;

    private String phoneNumber;

    private Boolean certificated;

    private LocalDateTime expirationDate;

    public static PersonalIdentificationDTO from(PersonalIdentification personalIdentification) {
        return PersonalIdentificationDTO.builder()
                .verificationCode(personalIdentification.getVerificationCode())
                .phoneNumber(personalIdentification.getPhoneNumber())
                .certificated(personalIdentification.getCertificated())
                .expirationDate(personalIdentification.getExpirationDate())
                .build();
    }
}
