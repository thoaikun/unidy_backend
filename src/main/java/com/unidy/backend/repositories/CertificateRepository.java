package com.unidy.backend.repositories;

import com.miragesql.miragesql.annotation.In;
import com.unidy.backend.domains.dto.responses.CertificateResponse;
import com.unidy.backend.domains.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate,Integer> {

    @Query("""
        SELECT new com.unidy.backend.domains.dto.responses.CertificateResponse(
            certificate.certificateId
            ,campaign.campaignId
            ,campaign.description
            ,organization.organizationId
            ,organization.organizationName
            ,certificate.file
        )
        FROM Certificate certificate
        INNER JOIN VolunteerCertificate volunteerCertificate
        ON certificate.certificateId = volunteerCertificate.certificateId
        INNER JOIN Campaign campaign
        ON campaign.campaignId = volunteerCertificate.campaignId
        INNER JOIN Organization organization
        ON campaign.owner = organization.userId
        INNER JOIN User user
        ON user.userId = volunteerCertificate.volunteerId
        WHERE user.userId = :userId AND campaign.campaignId = :campaignId
        """)
    List<CertificateResponse> findCertificate(Integer userId, Integer campaignId);

    Certificate findCertificateByFile(String file);

    @Query("""            
            SELECT new com.unidy.backend.domains.entity.Certificate(
                certificate.certificateId,
                certificate.file
            )
            FROM User user
            INNER JOIN
            VolunteerCertificate volunteercertificate
            ON user.userId = volunteercertificate.volunteerId
            INNER JOIN
            Certificate certificate
            ON volunteercertificate.certificateId = certificate.certificateId
            WHERE user.userId = :userId
            and volunteercertificate.campaignId = :campaignId
            """)
    Certificate findCertificateByUserId(Integer userId, Integer campaignId);
}
