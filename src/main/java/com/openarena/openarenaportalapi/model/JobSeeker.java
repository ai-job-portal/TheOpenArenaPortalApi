package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "jobseeker")
@Getter
@Setter
public class JobSeeker extends Auditable implements UserDetails, User{ // Extend Auditable and implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String skills; //  Store as a comma-separated string for now

    @Column(name = "resume_url") // Good practice to use snake_case for column names
    private String resumeUrl;

    @Column(nullable = false)
    private String mobile;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Consider LAZY fetching
    @JoinTable(name = "job_seeker_roles",
            joinColumns = @JoinColumn(name = "job_seeker_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )

    private Set<Role> roles = new HashSet<>();

    //  Add a method to check the password (important for login)
    public boolean checkPassword(String rawPassword) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, this.password);
    }

    @Override
    public Integer getId() {
        return id;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize as needed for account expiry
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize as needed for account locking
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize as needed for credential expiry
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize as needed for user enabled/disabled status
    }
}