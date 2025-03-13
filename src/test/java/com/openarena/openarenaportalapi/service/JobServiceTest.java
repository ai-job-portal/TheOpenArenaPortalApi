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
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.Inet4Address;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private EmployerRepository employerRepository; // Mock this too

    @Mock
    private RecruiterRepository recruiterRepository; // Mock this

    @Mock
    private RecruiterEmployerRepository recruiterEmployerRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void getAllJobs_shouldReturnActiveJobDtos() {
        // Arrange
        Job job1 = new Job();
        job1.setId(1);
        job1.setTitle("Software Engineer");
        job1.setCompanyName("Company A");
        job1.setPostDate(LocalDate.now().atStartOfDay()); // Use LocalDate
        job1.setIsActive(true); // Active job
        Employer employer1 = new Employer();
        employer1.setCompanyName("Company A");
        job1.setEmployer(employer1);
        Recruiter recruiter = new Recruiter();
        recruiter.setUsername("testuser");
        job1.setPoster(recruiter); //set Recruiter


        Job job2 = new Job();
        job2.setId(2);
        job2.setTitle("Data Scientist");
        job2.setCompanyName("Company B");
        job2.setPostDate(LocalDate.now().atStartOfDay()); // Use LocalDate
        job2.setIsActive(true);  //Active job
        Employer employer2 = new Employer();
        employer2.setCompanyName("Company B");
        job2.setEmployer(employer2); // Associate Employer
        Recruiter recruiter2 = new Recruiter();
        recruiter2.setUsername("recruiter2");
        job2.setPoster(recruiter2); //set Recruiter

        List<Job> activeJobs = Arrays.asList(job1, job2);
        when(jobRepository.findByIsActiveTrue()).thenReturn(activeJobs); // Corrected method

        // Act
        List<JobDto> jobDtos = jobService.getAllJobs();

        // Assert
        assertEquals(2, jobDtos.size());
        assertEquals("Software Engineer", jobDtos.get(0).getTitle());
        assertEquals("Data Scientist", jobDtos.get(1).getTitle());
        verify(jobRepository).findByIsActiveTrue(); // Verify correct method call
    }

    @Test
    void getAllJobs_shouldReturnEmptyList_whenNoActiveJobsExist() {
        // Arrange
        when(jobRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        // Act
        List<JobDto> jobDtos = jobService.getAllJobs();

        // Assert
        assertTrue(jobDtos.isEmpty());
        verify(jobRepository).findByIsActiveTrue();
    }

    @Test
    void getJobById_shouldReturnJobDto_whenJobExistsAndActive() {
        // Arrange
        Job job = new Job();
        job.setId(1); // Use Long
        job.setTitle("Software Engineer");
        job.setCompanyName("Company A");
        job.setPostDate(LocalDate.now().atStartOfDay()); // LocalDate
        job.setIsActive(true);
        Employer employer = new Employer();
        employer.setCompanyName("Company A");
        job.setEmployer(employer);  // Associate Employer
        Recruiter recruiter = new Recruiter();
        recruiter.setUsername("recruiter");
        job.setPoster(recruiter); //set Recruiter

        when(jobRepository.findByIdAndIsActiveTrue(1)).thenReturn(Optional.of(job)); // Corrected method

        // Act
        Optional<JobDto> jobDtoOptional = jobService.getJobById(1);

        // Assert
        assertTrue(jobDtoOptional.isPresent());
        JobDto jobDto = jobDtoOptional.get();
        assertEquals("Software Engineer", jobDto.getTitle());
        verify(jobRepository).findByIdAndIsActiveTrue(1);
    }


    @Test
    void getJobById_shouldReturnEmptyOptional_whenJobDoesNotExist() {
        // Arrange
        when(jobRepository.findByIdAndIsActiveTrue(1)).thenReturn(Optional.empty());

        // Act
        Optional<JobDto> jobDtoOptional = jobService.getJobById(1);

        // Assert
        assertFalse(jobDtoOptional.isPresent());
        verify(jobRepository).findByIdAndIsActiveTrue(1);
    }

    @Test
    void getJobById_shouldReturnEmptyOptional_whenJobExistsButInactive() {
        // Arrange
        Job job = new Job();
        job.setId(1);
        job.setTitle("Software Engineer");
        job.setIsActive(false); // Inactive job

        when(jobRepository.findByIdAndIsActiveTrue(1)).thenReturn(Optional.empty()); // findByIdAndIsActiveTrue returns empty

        // Act
        Optional<JobDto> jobDtoOptional = jobService.getJobById(1);

        // Assert
        assertFalse(jobDtoOptional.isPresent()); // Should be empty
        verify(jobRepository).findByIdAndIsActiveTrue(1); // Correct method is called
    }

    @Test
    void createJob_shouldCreateAndReturnJobDto() {
        // Arrange
        CreateJobDto createJobDto = new CreateJobDto();
        createJobDto.setTitle("New Job");
        createJobDto.setCompanyName("Test Company");
        createJobDto.setPostDate(LocalDate.now()); // Use LocalDate
        createJobDto.setDescription("Test Description");
        createJobDto.setTotalOpenings(2);
        // Set other required fields...

        Recruiter recruiter = new Recruiter();
        recruiter.setId(1);
        recruiter.setUsername("testuser"); // Add this

        Employer employer = new Employer();
        employer.setId(1); // Set an ID
        employer.setCompanyName("Test Company"); // Set a company name

        RecruiterEmployer recruiterEmployer = new RecruiterEmployer();  // Create instance
        recruiterEmployer.setEmployer(employer);  // Set the employer
        recruiterEmployer.setRecruiter(recruiter);
       // recruiter.(Set.of(recruiterEmployer));

        when(recruiterRepository.findById(1)).thenReturn(Optional.of(recruiter));
        // Mock the behavior to return a list containing the RecruiterEmployer instance
        when(recruiterEmployerRepository.findByRecruiterId(1)).thenReturn(List.of(recruiterEmployer));


        Job savedJob = new Job(); // Create a job object to simulate save
        savedJob.setId(10);  // Set a generated ID
        savedJob.setTitle(createJobDto.getTitle());
        savedJob.setCompanyName(createJobDto.getCompanyName());
        savedJob.setPostDate(createJobDto.getPostDate().atStartOfDay());
        savedJob.setDescription(createJobDto.getDescription());
        savedJob.setEmployer(employer);
        savedJob.setPoster(recruiter); //Set Recruiter
        savedJob.setTotalOpenings(2);
        savedJob.setIsActive(true);
        when(jobRepository.save(any(Job.class))).thenReturn(savedJob); // Mock saving and returning a job


        // Act
        JobDto createdJobDto = jobService.createJob(createJobDto, 1);

        // Assert
        assertNotNull(createdJobDto);
        assertEquals(10, createdJobDto.getId()); // Check the generated ID
        assertEquals("New Job", createdJobDto.getTitle());
        assertEquals("testuser", createdJobDto.getPosterName()); // Verify poster name
        // Add assertions for other fields
        verify(jobRepository).save(any(Job.class)); // Verify save was called

    }

    @Test
    void createJob_shouldThrowException_whenRecruiterNotFound() {
        // Arrange
        CreateJobDto createJobDto = new CreateJobDto(); // Don't need to set fields for this test

        when(recruiterRepository.findById(1)).thenReturn(Optional.empty()); // Recruiter not found

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobService.createJob(createJobDto, 1);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(recruiterRepository).findById(1);
        verify(jobRepository, never()).save(any(Job.class)); // Ensure save is NOT called
    }
    @Test
    void createJob_shouldThrowException_whenRecruiterNotAssociatedWithEmployer() {
        //Arrange
        CreateJobDto createJobDto = new CreateJobDto();
        Recruiter recruiter = new Recruiter();
        recruiter.setId(1);
        when(recruiterRepository.findById(1)).thenReturn(Optional.of(recruiter));
        // Simulate that recruiter is not belong to any employer
        when(recruiterEmployerRepository.findByRecruiterId(1)).thenReturn(Collections.emptyList());
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobService.createJob(createJobDto, 1);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()); // Verify status code
        assertEquals("Recruiter is not associated with any employer", exception.getReason()); // verify reason
        verify(recruiterRepository).findById(1);
        verify(jobRepository, never()).save(any(Job.class));
    }


    @Test
    void updateJob_shouldUpdateAndReturnJobDto_whenJobExists() {
        // Arrange
        Integer jobId = 1;
        UpdateJobDto updateJobDto = new UpdateJobDto();
        updateJobDto.setTitle("Updated Title");
        updateJobDto.setDescription("Updated Description");

        Job existingJob = new Job();
        existingJob.setId(jobId);
        existingJob.setTitle("Original Title");
        existingJob.setDescription("Original Description");
        existingJob.setPostDate(LocalDate.now().atStartOfDay());
        existingJob.setIsActive(true); // Make sure existing job is active
        Employer employer = new Employer(); // Create a dummy employer
        employer.setCompanyName("Test Company"); // Set a company name
        existingJob.setEmployer(employer);
        Recruiter recruiter = new Recruiter();
        recruiter.setUsername("recruiter");
        existingJob.setPoster(recruiter); //set Recruiter


        when(jobRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]); // Return the updated job

        // Act
        JobDto updatedJobDto = jobService.updateJob(jobId, updateJobDto);

        // Assert
        assertNotNull(updatedJobDto);
        assertEquals(jobId, updatedJobDto.getId());
        assertEquals("Updated Title", updatedJobDto.getTitle());
        assertEquals("Updated Description", updatedJobDto.getDescription());
        verify(jobRepository).findById(jobId);
        verify(jobRepository).save(existingJob);
    }


    @Test
    void updateJob_shouldThrowException_whenJobNotFound() {
        // Arrange
        Integer jobId = 1;
        UpdateJobDto updateJobDto = new UpdateJobDto(); // Don't need to set fields

        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            jobService.updateJob(jobId, updateJobDto);
        });

        verify(jobRepository).findById(jobId);
        verify(jobRepository, never()).save(any(Job.class));
    }

    @Test
    void deleteJob_shouldSetIsActiveFalse_whenJobExists() {
        // Arrange
        Integer jobId = 1;
        Job job = new Job();
        job.setId(jobId);
        job.setIsActive(true);

        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        // Act
        jobService.deleteJob(jobId);

        // Assert
        assertFalse(job.getIsActive()); // Verify isActive is set to false
        verify(jobRepository).findById(jobId);
        verify(jobRepository).save(job); // Verify save is called with the modified job
    }

    @Test
    void deleteJob_shouldThrowException_whenJobNotFound() {
        // Arrange
        Integer jobId = 1;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            jobService.deleteJob(jobId);
        });

        verify(jobRepository).findById(jobId);
        verify(jobRepository, never()).save(any(Job.class)); // Ensure save is NOT called
    }


    @Test
    void restoreJob_ShouldRestoreJob() {
        Integer jobId = 1;
        Job job = new Job();
        job.setId(jobId);
        job.setTitle("Test title"); // set title
        job.setIsActive(false); // Initially inactive

        // Mock repository to return this job
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobRepository.save(any(Job.class))).thenAnswer(i -> i.getArguments()[0]);
        Employer employer = new Employer();
        employer.setCompanyName("Company A");
        job.setEmployer(employer);  // Associate Employer
        Recruiter recruiter = new Recruiter();
        recruiter.setUsername("recruiter");
        job.setPoster(recruiter); //set Recruiter
        // Act
        JobDto restoredJobDto = jobService.restoreJob(jobId);

        // Assert
        assertTrue(job.getIsActive()); // Check if restored
        verify(jobRepository).save(job);  // Check if saved
        assertNotNull(restoredJobDto);
        assertEquals(restoredJobDto.getTitle(), job.getTitle());

    }

    @Test
    void restoreJob_ShouldThrowNotFound_WhenJobNotExist() {
        // Arrange
        Integer jobId = 1;
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> {
            jobService.restoreJob(jobId);
        });
        verify(jobRepository, never()).save(any());
    }
}