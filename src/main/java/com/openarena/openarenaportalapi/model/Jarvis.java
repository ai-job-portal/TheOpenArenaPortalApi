// src/main/java/com/openarena/openarenaportalapi/model/Jarvis.java
package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "jarvis")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Jarvis implements UserDetails { // Implement UserDetails directly

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER) // EAGER loading for roles
    @JoinTable(
            name = "jarvis_roles",
            joinColumns = @JoinColumn(name = "jarvis_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>(); // Initialize the set

    // Password hashing
    public void setPassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(rawPassword);
    }

    public boolean checkPassword(String rawPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, this.password);
    }

    // --- UserDetails interface methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username; // Or email, if you use email as the principal
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Or implement your logic for account expiration
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Or implement account locking logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Or implement password expiration logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Or implement account enabling/disabling logic
    }
    // --- End UserDetails methods ---
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role){
        this.roles.remove(role);
    }

    public Integer getId(){
        return this.id;
    }
}