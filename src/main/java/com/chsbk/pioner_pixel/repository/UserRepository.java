
package com.chsbk.pioner_pixel.repository;

import com.chsbk.pioner_pixel.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u left join u.emails e left join u.phones p " +
            "where (:name is null or lower(u.name) like lower(concat(:name, '%'))) " +
            "and (:email is null or e.email = :email) " +
            "and (:phone is null or p.phone = :phone) " +
            "and (:dob is null or u.dateOfBirth > :dob)")

    Page<User> search(@Param("name") String name,
                      @Param("email") String email,
                      @Param("phone") String phone,
                      @Param("dob") LocalDate dob,
                      Pageable pageable);

    Optional<User> findByEmails_Email(String email);

    Optional<User> findByPhones_Phone(String phone);
}
