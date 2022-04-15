package app.project.ably.auth.repository;

import app.project.ably.auth.entity.PersonalIdentification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalIdentificationRepository extends JpaRepository<PersonalIdentification, Long> {

    Optional<PersonalIdentification> findByPhoneNumber(String phoneNumber);
}
