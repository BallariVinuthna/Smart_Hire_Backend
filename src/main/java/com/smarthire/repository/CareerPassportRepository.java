package com.smarthire.repository;

import com.smarthire.entity.CareerPassport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CareerPassportRepository extends JpaRepository<CareerPassport, Long> {
    Optional<CareerPassport> findByStudentId(Long studentId);
    Optional<CareerPassport> findByPassportId(String passportId);
}
