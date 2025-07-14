package com.jobhunt.controller;

import com.jobhunt.model.*;
import com.jobhunt.payload.response.MessageResponse;
import com.jobhunt.repository.JobApplicationRepository;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(job);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<Job> createJob(@RequestBody Job job, @AuthenticationPrincipal User user) {
        Job createdJob = jobService.createJob(job, user);
        return ResponseEntity.ok(createdJob);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job jobDetails, @AuthenticationPrincipal User user) {
        Job job = jobService.getJobById(id);
        
        // Check if the user is the creator of this job
        if (!jobService.isJobCreator(job, user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not authorized to update this job."));
        }
        
        Job updatedJob = jobService.updateJob(id, jobDetails);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Job job = jobService.getJobById(id);
        
        // Check if the user is the creator of this job
        if (!jobService.isJobCreator(job, user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not authorized to delete this job."));
        }
        
        jobService.deleteJob(id);
        return ResponseEntity.ok(new MessageResponse("Job deleted successfully!"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) Boolean remote) {
        
        List<Job> jobs = jobService.searchJobs(title, location, type, experienceLevel, remote);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<List<Job>> getRecruiterJobs(@AuthenticationPrincipal User user) {
        List<Job> jobs = jobService.getJobsByRecruiter(user);
        return ResponseEntity.ok(jobs);
    }

    @PostMapping("/{id}/apply")
    @PreAuthorize("hasRole('ROLE_JOB_SEEKER')")
    public ResponseEntity<?> applyForJob(
            @PathVariable Long id, 
            @RequestBody JobApplication application,
            @AuthenticationPrincipal User user) {
        
        Job job = jobService.getJobById(id);
        
        // Check if already applied
        if (jobService.hasApplied(job, user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You have already applied for this job."));
        }
        
        JobApplication newApplication = jobService.applyForJob(job, application, user);
        return ResponseEntity.ok(newApplication);
    }

    @GetMapping("/{id}/applications")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<?> getJobApplications(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Job job = jobService.getJobById(id);
        
        // Check if the user is the creator of this job
        if (!jobService.isJobCreator(job, user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not authorized to view applications for this job."));
        }
        
        List<JobApplication> applications = jobApplicationRepository.findByJob(job);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long id, 
            @RequestParam JobApplication.ApplicationStatus status,
            @AuthenticationPrincipal User user) {
        
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));
        
        // Check if the user is the creator of the job for this application
        if (!jobService.isJobCreator(application.getJob(), user)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not authorized to update this application."));
        }
        
        application.setStatus(status);
        JobApplication updatedApplication = jobApplicationRepository.save(application);
        return ResponseEntity.ok(updatedApplication);
    }
}
