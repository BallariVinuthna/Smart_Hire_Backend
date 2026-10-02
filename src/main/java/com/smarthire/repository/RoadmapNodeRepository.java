package com.smarthire.repository;

import com.smarthire.entity.RoadmapNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoadmapNodeRepository extends JpaRepository<RoadmapNode, Long> {
    List<RoadmapNode> findByRoadmapIdOrderByStepOrderAsc(Long roadmapId);
}
