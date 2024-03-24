package com.unidy.backend.repositories;

import com.unidy.backend.domains.entity.VolunteerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerCertificateRepository extends JpaRepository<VolunteerCertificate,Integer> {

}
