package com.unidy.backend.services.servicesIplm;

import com.unidy.backend.domains.ErrorResponseDto;
import com.unidy.backend.domains.dto.requests.RegisterRequest;
import com.unidy.backend.config.JwtService;
import com.unidy.backend.domains.dto.requests.AuthenticationRequest;
import com.unidy.backend.domains.dto.responses.AuthenticationResponse;
import com.unidy.backend.domains.entity.*;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import com.unidy.backend.domains.role.Role;
import com.unidy.backend.repositories.*;
import com.unidy.backend.domains.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unidy.backend.services.servicesInterface.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceIplm implements AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final Neo4j_UserRepository neo4j_userRepository;
  private final FavoriteActivitiesRepository favoriteActivitiesRepository;
  private final OrganizationRepository organizationRepository;
  @Transactional
  public ResponseEntity<?> register(RegisterRequest request) {
    try {
      var findUser = repository.findByEmail(request.getEmail());
      if (findUser.isPresent()) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto("Invalid Email"));
      }

      if (request.getRole().equals(Role.ORGANIZATION)){
        var user = User.builder()
                .role(Role.ORGANIZATION)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isBlock(false)
                .build();
        repository.save(user);

        int user_id = repository.findByEmail(user.getEmail()).get().getUserId();
        Organization organization = Organization.builder()
                .organizationName(request.getFullName())
                .email(request.getEmail())
                .status("0")
                .phone(request.getPhone())
                .address(request.getAddress())
                .country(request.getCountry())
                .userId(user_id)
                .firebaseTopic(request.getFullName().toLowerCase().replace(" ", "_") + "_" + user_id + "_topic")
                .isApproved(false)
                .build();
        organizationRepository.save(organization);
        UserNode userNode = new UserNode() ;
        userNode.setUserId(user_id);
        userNode.setFullName(request.getFullName());
        userNode.setIsBlock(false);
        userNode.setProfileImageLink(null);
        userNode.setRole(Role.ORGANIZATION.toString());
        neo4j_userRepository.save(userNode);

      } else if (request.getRole().equals(Role.VOLUNTEER)){
        var user = User.builder()
                .fullName(request.getFullName())
                .address(request.getAddress())
                .dayOfBirth(request.getDayOfBirth())
                .sex(request.getSex())
                .phone(request.getPhone())
                .email(request.getEmail())
                .job(request.getJob())
                .workLocation(request.getWorkLocation())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isBlock(false)
                .build();
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        Optional<User> registerUser = repository.findByEmail(request.getEmail());
        UserNode userNode = new UserNode() ;
        userNode.setUserId(registerUser.get().getUserId());
        userNode.setFullName(request.getFullName());
        userNode.setIsBlock(false);
        userNode.setProfileImageLink(null);
        neo4j_userRepository.save(userNode);
        saveUserToken(savedUser, jwtToken, jwtToken);
      }

      return ResponseEntity.ok().header("Register").body("Register success");
    }catch (Exception e){
      return ResponseEntity.badRequest().body(e);
    }
  }
  public ResponseEntity<?> authenticate(AuthenticationRequest request) {
    try {
      boolean check = false;
      authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getEmail(),
                      request.getPassword()
              )
      );
      var user = repository.findByEmail(request.getEmail())
              .orElseThrow();

      Map<String, String> jwtTokens = getJwtToken(user);
      if (jwtTokens.get("isExpired").equals("true")){
        saveUserToken(user, jwtTokens.get("accessToken"), jwtTokens.get("refreshToken"));
      }

      FavoriteActivities favorite = favoriteActivitiesRepository.findByUserId(user.getUserId());
      if (favorite != null) {
        check = true;
      }
      if (user.getIsBlock()){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto("Tài khoản bạn đã bị khoá"));
      }
      if (user.getRole().equals(Role.ORGANIZATION)){
        Optional<Organization> organization = organizationRepository.findByUserId(user.getUserId());
        if (!organization.get().getIsApproved()){
          return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponseDto("Tổ chức của bạn chưa được Unidy chấp nhận tham gia"));
        }
      }
      return ResponseEntity.ok().body(AuthenticationResponse.builder()
              .accessToken(jwtTokens.get("accessToken"))
              .refreshToken(jwtTokens.get("refreshToken"))
              .isChosenFavorite(check)
              .role(user.getRole())
              .build());
    } catch (Exception e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto("Email hoặc mật khẩu không đúng"));
    }
  }

  private Map<String, String> getJwtToken(User user) {
    List<Token> previousTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());

    for (Token token : previousTokens) {
      try {
        isTokenStillValid(token);
        return Map.of("accessToken", token.getToken(), "refreshToken", token.getRefreshToken(), "isExpired", "false");
      }
      catch (Exception e){
          token.setExpired(true);
          token.setRevoked(true);
          tokenRepository.save(token);
      }
    }

    return Map.of("accessToken", jwtService.generateToken(user), "refreshToken", jwtService.generateRefreshToken(user), "isExpired", "true");
  }

  private void saveUserToken(User user, String jwtToken, String refreshToken) {
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .refreshToken(refreshToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private boolean isTokenStillValid(Token token) {
    return jwtService.isTokenExpired(token.getToken());
  }

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }


  public ResponseEntity<?> refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error");
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken, refreshToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        return ResponseEntity.ok().body(accessToken);
      }
    }
    return ResponseEntity.internalServerError().body("Internal server");
  }
}
