package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.requests.UserInformationRequest;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.mapper.DtoMapper;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.services.servicesInterface.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceIplm implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final DtoMapper dtoMapper;

    public UserInformationRespond getUserInformation(int userId){
        User user = repository.findByUserId(userId);
        UserInformationRespond information = new UserInformationRespond() ;
        information.setUserId(user.getUserId());
        information.setFullName(user.getFullName());
        information.setAddress(user.getAddress());
        information.setSex(user.getSex());
        information.setPhone(user.getPhone());
        information.setDayOfBirth(user.getDayOfBirth());
        information.setJob(user.getJob());
        information.setRole(user.getRole());
        information.setWorkLocation(user.getWorkLocation());
        return information ;
    }

    public ResponseEntity<?> updateUserInformation(@RequestBody UserDto userDto){
        try {
            User userData = repository.findByUserId(userDto.getUserId());
            dtoMapper.updateUserInformation(userDto,userData);
            repository.save(userData);
            return ResponseEntity.ok().body(new SuccessReponse("Cập nhật thành công"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Lỗi cập nhật thông tin"));
        }
    }


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
}
