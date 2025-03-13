package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobshare")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class JobShare {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_id", nullable = false)
    private Integer jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Job job;

    @Column(name = "shared_by", nullable = false)
    private Integer sharedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by",referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private Recruiter sharer;

    @Column(name = "shared_with", nullable = false)
    private Integer sharedWith;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_with", referencedColumnName = "id",insertable = false, updatable = false)
    @ToString.Exclude
    private Recruiter recipient;

    @Column(name = "shared_at")
    private LocalDateTime sharedAt = LocalDateTime.now();;
}