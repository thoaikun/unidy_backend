package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.S3.S3Service;
import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.SuccessReponse;
import com.unidy.backend.domains.dto.UserDto;
import com.unidy.backend.domains.dto.responses.UserInformationRespond;
import com.unidy.backend.domains.entity.User;
import com.unidy.backend.domains.entity.UserProfileImage;
import com.unidy.backend.mapper.DtoMapper;
import com.unidy.backend.repositories.UserProfileImageRepository;
import com.unidy.backend.repositories.UserRepository;
import com.unidy.backend.domains.dto.requests.ChangePasswordRequest;
import com.unidy.backend.services.servicesInterface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceIplm implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final DtoMapper dtoMapper;
    private final S3Service s3Service;
    private  final UserProfileImageRepository userProfileImageRepository;
    public UserInformationRespond getUserInformation(Principal connectedUser){
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
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

//        byte[] profileImage = s3Service.getObject(
//                "unidy",
//                "profile-images/%s/%s".formatted(userId, image.getLinkImage())
//        );
//        information.setImage(profileImage);
        UserProfileImage image = userProfileImageRepository.findByUserId(user.getUserId());
        if (image != null){
            URL urlImage = s3Service.getObjectUrl(
                    "unidy",
                    "profile-images/%s/%s".formatted(user.getUserId(), image.getLinkImage())
            );
            information.setImage(urlImage.toString());
        }

        return information ;
    }

    public ResponseEntity<?> updateUserInformation(UserDto userDto,Principal connectedUser){
        try {
            var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            User userData = repository.findByUserId(user.getUserId());
            dtoMapper.updateUserInformation(userDto,userData);
            repository.save(userData);
            return ResponseEntity.ok().body(new SuccessReponse("Cập nhật thành công"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Lỗi cập nhật thông tin"));
        }
    }


    public ResponseEntity<?> changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Sai mật khẩu"));
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Nhập lại khẩu mới không trùng khớp"));
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
        return ResponseEntity.ok().body(new SuccessReponse("Đổi mật khẩu thành công"));
    }


    public ResponseEntity<?> newPassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto("Nhập lại khẩu mới không trùng khớp"));
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        return ResponseEntity.ok().body(new SuccessReponse("Đổi mật khẩu mới thành công"));
    }

    public ResponseEntity<?> updateProfileImage(MultipartFile imageFile, Principal connectedUser){
        String profileImageId = UUID.randomUUID().toString();
        String fileContentType = imageFile.getContentType();
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        int userId = user.getUserId();

        try {
            if (fileContentType != null &&
                    (fileContentType.equals("image/png") ||
                            fileContentType.equals("image/jpeg") ||
                            fileContentType.equals("image/jpg"))) {
                fileContentType = fileContentType.replace("image/",".");
                s3Service.putImage(
                        "unidy",
                        fileContentType,
                        "profile-images/%s/%s".formatted(userId, profileImageId+fileContentType ),
                        imageFile.getBytes()
                );

                UserProfileImage image = userProfileImageRepository.findByUserId(userId);
                if (image == null){
                    image = new UserProfileImage();
                }
                image.setLinkImage(profileImageId+fileContentType);
                image.setUpdateDate(new Date());
                image.setUserId(userId);
                userProfileImageRepository.save(image);
                return ResponseEntity.ok().body(new SuccessReponse("Update image success"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponseDto("Unsupported file format"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDto(e.toString()));
        }
    }


}
