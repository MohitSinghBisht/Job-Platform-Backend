package com.jobhunt.service;

import com.jobhunt.model.*;
import com.jobhunt.repository.JobApplicationRepository;
import com.jobhunt.repository.JobRepository;
import com.jobhunt.repository.JobSeekerRepository;
import com.jobhunt.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;
    
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private NotificationService notificationService;

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrueAndClosingDateAfter(LocalDateTime.now());
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public Job createJob(Job job, User user) {
        RecruiterProfile recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
        
        job.setRecruiter(recruiter);
        job.setPostedDate(LocalDateTime.now());
        job.setActive(true);
        
        return jobRepository.save(job);
    }

    public Job updateJob(Long id, Job jobDetails) {
        Job job = getJobById(id);
        
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setJobType(jobDetails.getJobType());
        job.setExperienceLevel(jobDetails.getExperienceLevel());
        job.setLocation(jobDetails.getLocation());
        job.setRemoteAllowed(jobDetails.isRemoteAllowed());
        job.setSalary(jobDetails.getSalary());
        job.setRequiredSkills(jobDetails.getRequiredSkills());
        job.setBenefits(jobDetails.getBenefits());
        job.setClosingDate(jobDetails.getClosingDate());
        job.setActive(jobDetails.isActive());
        
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        Job job = getJobById(id);
        job.setActive(false);
        jobRepository.save(job);
    }

    public List<Job> searchJobs(String title, String location, String type, String experienceLevel, Boolean remote) {
        List<Job> jobs = getAllActiveJobs();
        
        if (title != null && !title.isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (location != null && !location.isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (type != null && !type.isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getJobType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        
        if (experienceLevel != null && !experienceLevel.isEmpty()) {
            jobs = jobs.stream()
                    .filter(job -> job.getExperienceLevel().equalsIgnoreCase(experienceLevel))
                    .collect(Collectors.toList());
        }
        
        if (remote != null && remote) {
            jobs = jobs.stream()
                    .filter(job -> job.isRemoteAllowed())
                    .collect(Collectors.toList());
        }
        
        return jobs;
    }

    public List<Job> getJobsByRecruiter(User user) {
        RecruiterProfile recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
        
        return jobRepository.findByRecruiter(recruiter);
    }

    public boolean isJobCreator(Job job, User user) {
        return job.getRecruiter().getUser().getId().equals(user.getId());
    }

    public boolean hasApplied(Job job, User user) {
        JobSeekerProfile jobSeeker = jobSeekerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("JobSeeker profile not found"));
        
        return jobApplicationRepository.findByJobAndJobSeeker(job, jobSeeker).isPresent();
    }

    public JobApplication applyForJob(Job job, JobApplication application, User user) {
        JobSeekerProfile jobSeeker = jobSeekerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("JobSeeker profile not found"));
        
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(JobApplication.ApplicationStatus.APPLIED);
        
        JobApplication savedApplication = jobApplicationRepository.save(application);
        
        // Send notification to the recruiter
        String message = user.getFirstName() + " " + user.getLastName() + " applied for job: " + job.getTitle();
        notificationService.createNotification(
                job.getRecruiter().getUser(),
                "New Job Application",
                message,
                Notification.NotificationType.JOB_APPLICATION_UPDATE,
                job.getId()
        );
        
        return savedApplication;
    }

    public List<Job> getRecommendedJobs(User user) {
        JobSeekerProfile jobSeeker = jobSeekerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("JobSeeker profile not found"));
        
        List<String> skills = jobSeeker.getSkills();
        if (skills == null || skills.isEmpty()) {
            return new ArrayList<>();
        }
        
        return jobRepository.findMatchingJobsBySkills(skills, LocalDateTime.now());
    }
}
