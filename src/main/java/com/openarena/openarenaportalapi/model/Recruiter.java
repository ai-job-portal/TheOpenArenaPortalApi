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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "recruiter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter extends Auditable implements UserDetails, User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employer_id", referencedColumnName = "id") // Correct join column
    private Employer employer;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(length = 100)
    private String name;

    @Column(length = 20)
    private String mobile;
    //No need as per new relation
    //@Column(name = "created_by")
    // private Integer createdBy;

    //@ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    // private Recruiter creator;
    //No need as per new relation
    // @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    //private Set<Recruiter> createdRecruiters = new HashSet<>();


    // Use RecruiterEmployer for the association
    @OneToMany(mappedBy = "recruiter", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<RecruiterEmployer> recruiterEmployers = new HashSet<>(); // Use the join table entity

    @ManyToMany(fetch = FetchType.EAGER)  //Eager is generally discouraged. We need to discuss this
    @JoinTable(
            name = "recruiter_roles",
            joinColumns = @JoinColumn(name = "recruiter_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = this.roles.stream().map((role)-> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        return authorities;
    }
    //Other methods remain the same
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
    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }
}