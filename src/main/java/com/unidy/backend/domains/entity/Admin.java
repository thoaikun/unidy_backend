package com.unidy.backend.domains.entity;

import com.unidy.backend.domains.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)

        @Column(name = "admin_id")
        private Integer admin_id;

        @Column(name = "full_name")
        private String fullName;

        @Column(name = "day_of_birth")
        private Date dayOfBirth;

        @Column(name = "phone")
        private String phone;

        @Column(name = "email")
        private String email;

        @Column(name = "password")
        private String password;

        @Enumerated(EnumType.STRING)
        private Role role;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return role.getAuthorities();
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @OneToMany(fetch = FetchType.EAGER)
        @JoinColumn(name = "id")
        private List<UserDeviceFcmToken> userDeviceFcmTokens;
}
