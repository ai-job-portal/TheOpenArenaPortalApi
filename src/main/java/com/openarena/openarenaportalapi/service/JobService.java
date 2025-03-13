package com.openarena.openarenaportalapi.service;


import com.openarena.openarenaportalapi.dto.CreateJobDto;
import com.openarena.openarenaportalapi.dto.JobDto;
import com.openarena.openarenaportalapi.dto.UpdateJobDto;
import com.openarena.openarenaportalapi.model.Employer;
import com.openarena.openarenaportalapi.model.Job;
import com.openarena.openarenaportalapi.model.Recruiter;
import com.openarena.openarenaportalapi.model.RecruiterEmployer;
import com.openarena.openarenaportalapi.repository.EmployerRepository;
import com.openarena.openarenaportalapi.repository.JobRepository;
import com.openarena.openarenaportalapi.repository.RecruiterEmployerRepository;
import com.openarena.openarenaportalapi.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final RecruiterRepository recruiterRepository;
    private final RecruiterEmployerRepository recruiterEmployerRepository; // Inject

    @Autowired
    public JobService(JobRepository jobRepository, EmployerRepository employerRepository,
                      RecruiterRepository recruiterRepository, RecruiterEmployerRepository recruiterEmployerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
        this.recruiterRepository = recruiterRepository;
        this.recruiterEmployerRepository = recruiterEmployerRepository; // Assign
    }

    @Transactional(readOnly = true)
    public List<JobDto> getAllJobs() {
        return jobRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<JobDto> getJobById(Integer id) {
        return jobRepository.findByIdAndIsActiveTrue(id).map(this::convertToDto);
    }
    @Transactional(readOnly = true)
    public List<Job> getAllJob(){
        return jobRepository.findAll();
    }

    private JobDto convertToDto(Job job) {
        JobDto dto = new JobDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setSkills(job.getSkills());
        dto.setLocation(job.getLocation());
        dto.setSalaryRange(job.getSalaryRange());
        dto.setCompanyName(job.getCompanyName());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setWorkplaceType(job.getWorkplaceType());
        dto.setEmploymentType(job.getEmploymentType());
        dto.setEducationalQualifications(job.getEducationalQualifications());
        dto.setAboutCompany(job.getAboutCompany());
        dto.setPostDate(job.getPostDate());
        dto.setTotalOpenings(job.getTotalOpenings());
        dto.setEmployerId(job.getEmployerId());
        dto.setPostedBy(job.getPostedBy());
        dto.setIsActive(job.getIsActive());


        if (job.getEmployer() != null) {
            dto.setEmployerName(job.getEmployer().getCompanyName()); // Assuming Employer has a companyName
        }
        if(job.getPoster() != null){
            dto.setPosterName(job.getPoster().getUsername());
        }
        return dto;
    }

    @Transactional
    public JobDto createJob(CreateJobDto createJobDto, Integer recruiterId) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recruiter not found with id: " + recruiterId));

        // Find the RecruiterEmployer entry, not just the Employer
        List<RecruiterEmployer> recruiterEmployers = recruiterEmployerRepository.findByRecruiterId(recruiterId);
        if (recruiterEmployers.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recruiter is not associated with any employer");
        }

        //In this example, I am picking the first associated employer.  You MUST decide on your logic here.
        // Do you allow a recruiter to post for multiple employers?  If so, how do you choose which one?
        RecruiterEmployer recruiterEmployer = recruiterEmployers.get(0);
        Employer employer = recruiterEmployer.getEmployer();



        Job job = new Job();
        job.setTitle(createJobDto.getTitle());
        job.setCompanyName(createJobDto.getCompanyName());
        job.setDescription(createJobDto.getDescription());
        job.setSkills(createJobDto.getSkills());
        job.setLocation(createJobDto.getLocation());
        job.setSalaryRange(createJobDto.getSalaryRange());
        job.setExperienceLevel(createJobDto.getExperienceLevel());
        job.setWorkplaceType(createJobDto.getWorkplaceType());
        job.setEmploymentType(createJobDto.getEmploymentType());
        job.setEducationalQualifications(createJobDto.getEducationalQualifications());
        job.setAboutCompany(createJobDto.getAboutCompany());
        job.setPostDate(createJobDto.getPostDate().atStartOfDay());
        job.setTotalOpenings(createJobDto.getTotalOpenings());
        job.setEmployerId(employer.getId()); // Set the employer ID
        job.setEmployer(employer); // Set the Employer entity.
        job.setPostedBy(recruiter.getId()); // Set the recruiter
        job.setPoster(recruiter); //set the Recruiter entity
        job.setIsActive(true);

        job = jobRepository.save(job);
        return convertToDto(job);
    }
    @Transactional
    public JobDto updateJob(Integer id, UpdateJobDto updateJobDto) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));

        // Update fields only if they are provided in the DTO
        if (updateJobDto.getTitle() != null) {
            job.setTitle(updateJobDto.getTitle());
        }
        if (updateJobDto.getDescription() != null) {
            job.setDescription(updateJobDto.getDescription());
        }
        if (updateJobDto.getSkills() != null) {
            job.setSkills(updateJobDto.getSkills());
        }
        if (updateJobDto.getLocation() != null) {
            job.setLocation(updateJobDto.getLocation());
        }
        if (updateJobDto.getSalaryRange() != null) {
            job.setSalaryRange(updateJobDto.getSalaryRange());
        }
        if (updateJobDto.getCompanyName() != null) {
            job.setCompanyName(updateJobDto.getCompanyName());
        }
        if (updateJobDto.getExperienceLevel() != null) {
            job.setExperienceLevel(updateJobDto.getExperienceLevel());
        }
        if (updateJobDto.getWorkplaceType() != null) {
            job.setWorkplaceType(updateJobDto.getWorkplaceType());
        }
        if (updateJobDto.getEmploymentType() != null) {
            job.setEmploymentType(updateJobDto.getEmploymentType());
        }
        if (updateJobDto.getEducationalQualifications() != null) {
            job.setEducationalQualifications(updateJobDto.getEducationalQualifications());
        }
        if (updateJobDto.getAboutCompany() != null) {
            job.setAboutCompany(updateJobDto.getAboutCompany());
        }
        if (updateJobDto.getPostDate() != null) {
            job.setPostDate(updateJobDto.getPostDate().atStartOfDay());
        }
        if (updateJobDto.getTotalOpenings() != null) {
            job.setTotalOpenings(updateJobDto.getTotalOpenings());
        }
        if (updateJobDto.getIsActive() != null) {
            job.setIsActive(updateJobDto.getIsActive());
        }
        // employerId and postedBy are *not* updated here

        job = jobRepository.save(job); // Save the updated job
        return convertToDto(job);
    }
    @Transactional
    public void deleteJob(Integer id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));
        job.setIsActive(false); // Set is_active to false
        jobRepository.save(job); // Save the updated job (don't actually delete)
    }

    @Transactional // Add this method
    public JobDto restoreJob(Integer id) {
        Job job = jobRepository.findById(id) // Find by ID, *regardless* of active status
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found with id: " + id));

        job.setIsActive(true); // Set is_active back to true
        job = jobRepository.save(job);
        return convertToDto(job);
    }
}