package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.ScheduleJobs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleJobsRepository extends JpaRepository<ScheduleJobs,Integer> {
}
