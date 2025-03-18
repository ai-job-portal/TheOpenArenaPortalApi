// src/main/java/com/openarena/openarenaportalapi/model/Auditable.java
package com.openarena.openarenaportalapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass //  Important:  Marks this as a base class for entities
@EntityListeners(AuditingEntityListener.class) // Enable auditing
public abstract class Auditable {

    @CreatedBy
    @Column(name = "created_by", updatable = false) //  Don't allow updates
    protected Long createdBy;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    protected Long lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_at")
    protected LocalDateTime lastModifiedAt;
}