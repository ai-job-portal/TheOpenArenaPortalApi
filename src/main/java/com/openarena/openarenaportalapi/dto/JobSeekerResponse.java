package com.openarena.openarenaportalapi.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class JobSeekerResponse {
    private Integer id;
    private String username;
    private String email;
    private String name;
    private String skills;
    private String resumeUrl;
    private String mobile;

    public JobSeekerResponse(){

    }

    public JobSeekerResponse(Integer id, String username, String email, String name, String skills, String resumeUrl, String mobile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.skills = skills;
        this.resumeUrl = resumeUrl;
        this.mobile = mobile;
    }

}