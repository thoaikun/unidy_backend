package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.responses.TransactionResponse;
import com.unidy.backend.domains.dto.responses.VolunteerCampaignResponse;
import com.unidy.backend.domains.entity.Organization;
import com.unidy.backend.repositories.OrganizationRepository;
import com.unidy.backend.repositories.TransactionRepository;
import com.unidy.backend.services.servicesInterface.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationServiceIplm implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final TransactionRepository transactionRepository;
    public ResponseEntity<?> getProfileOrganization(int organizationId){
        try {
            Optional<Organization> organization = organizationRepository.findByUserId(organizationId);
            return ResponseEntity.ok().body(organization);
        } catch (Exception exception){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Something Error"));
        }
    }

    @Override
    public ResponseEntity<?> getListVolunteer() {
        return null;
    }

    public ResponseEntity<?> getListVolunteerNotApproved(int organizationId){
//        try {
//            List<VolunteerCampaignResponse> listVolunteerNotApproved = organizationRepository.getListVolunteerNotApproved();
//            return ResponseEntity.ok().body(listVolunteerNotApproved);
//        } catch (Exception e){
//            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
//        }
        return null;
    }

    public ResponseEntity<?> approveVolunteer(int volunteerId){
        return null;
    }
    public ResponseEntity<?> getListVolunteerApproved(int organizationId){
//        try {
//            List<VolunteerCampaignResponse> listVolunteerApproved = organizationRepository.getListVolunteerApproved();
//            return ResponseEntity.ok().body(listVolunteerApproved);
//        } catch (Exception e){
//            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
//        }
        return null;
    }
    public ResponseEntity<?> getListTransaction(int organizationUserId){
//        try {
//            List<TransactionResponse> transaction = transactionRepository.findTransactionByOrganizationId(organizationUserId);
//            return ResponseEntity.ok().body(transaction);
//        } catch (Exception e){
//            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
//        }
        return null ;

    }
    @Override
    public ResponseEntity<?> getListCampaignTransaction(Integer organizationUserId, int campaignId) {
//        try {
//            List<TransactionResponse> transaction = transactionRepository.findTransactionByCampaignId(organizationUserId,campaignId);
//            return ResponseEntity.ok().body(transaction);
//        } catch (Exception e){
//            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
//        }
        return null ;
    }
}
