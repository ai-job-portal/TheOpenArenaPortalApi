package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "recruiter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Add this for easier construction in services/tests
public class Recruiter implements UserDetails { // Implement UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 128) // Increased length for hashed passwords
    private String password;

    @Column(length = 100)
    private String name;

    @Column(length = 20)
    private String mobile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id") // Corrected: Single employer
    private Employer employer;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER for roles
    @JoinTable(
            name = "recruiter_roles",
            joinColumns = @JoinColumn(name = "recruiter_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    // --- Methods required by UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can implement account expiry logic here
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can implement account locking logic here
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can implement password expiry logic here
    }

    @Override
    public boolean isEnabled() {
        return true; // You can implement user enabling/disabling logic here
    }

    // --- Other potentially useful methods (not strictly part of UserDetails) ---
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }
    // --- Remove the old relationships.  These are no longer needed ---

    //  @Column(name = "created_by") // Removed
    //  private Integer createdBy;

    //  @ManyToOne(fetch = FetchType.LAZY)
    //  @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    // @ToString.Exclude
    //  private Recruiter creator; // Removed

    //   @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    //    @ToString.Exclude
    //   private Set<Recruiter> createdRecruiters = new HashSet<>();

    //    @ManyToMany
    //   @JoinTable(
    //        name = "recruiter_employer",  // We no longer use a join table directly
    //        joinColumns = @JoinColumn(name = "recruiter_id"),
    //        inverseJoinColumns = @JoinColumn(name = "employer_id")
    //   )
    //  @ToString.Exclude
    //   private Set<Employer> employers = new HashSet<>();  // Removed

    //    @ManyToMany(mappedBy = "sharedByRecruiter")
    //   private Set<JobShare> sharedBy = new HashSet<>();

    //    @ManyToMany(mappedBy = "sharedWithRecruiter")
    //   private Set<JobShare> sharedWith = new HashSet<>();
    //--- End of methods to Remove
}