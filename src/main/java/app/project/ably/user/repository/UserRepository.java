package app.project.ably.user.repository;

import app.project.ably.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByRegNo(String regNo);
}
