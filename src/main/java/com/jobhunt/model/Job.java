package com.jobhunt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private RecruiterProfile recruiter;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "job_type")
    private String jobType;  // FULL_TIME, PART_TIME, CONTRACT, etc.

    @Column(name = "experience_level")
    private String experienceLevel;  // ENTRY, MID, SENIOR, etc.

    @Column
    private String location;

    @Column(name = "remote_allowed")
    private boolean remoteAllowed;

    @Column
    private String salary;

    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> requiredSkills = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_benefits", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    @Column(name = "posted_date")
    private LocalDateTime postedDate;

    @Column(name = "closing_date")
    private LocalDateTime closingDate;

    @Column
    private boolean active;

    @ManyToMany(mappedBy = "appliedJobs")
    private List<JobSeekerProfile> applicants = new ArrayList<>();

    @ManyToMany(mappedBy = "savedJobs")
    private List<JobSeekerProfile> savedByUsers = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();
}
