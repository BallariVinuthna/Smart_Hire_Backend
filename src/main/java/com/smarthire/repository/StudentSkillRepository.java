package com.smarthire.repository;

import com.smarthire.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {
    List<StudentSkill> findByStudentId(Long studentId);
    Optional<StudentSkill> findByStudentIdAndSkillId(Long studentId, Long skillId);
    Optional<StudentSkill> findByStudentIdAndSkillNameIgnoreCase(Long studentId, String skillName);
}
