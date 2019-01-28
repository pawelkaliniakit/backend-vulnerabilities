package com.backendvulnerabilities.business.repository;

import com.backendvulnerabilities.business.entity.Attendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendeeRepository extends JpaRepository<Attendee, Integer> {
}
