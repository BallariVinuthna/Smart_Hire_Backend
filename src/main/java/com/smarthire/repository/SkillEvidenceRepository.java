package com.smarthire.repository;

import com.smarthire.entity.SkillEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SkillEvidenceRepository extends JpaRepository<SkillEvidence, Long> {
    List<SkillEvidence> findByStudentSkillId(Long studentSkillId);
    List<SkillEvidence> findByStudentSkillStudentId(Long studentId);
}
