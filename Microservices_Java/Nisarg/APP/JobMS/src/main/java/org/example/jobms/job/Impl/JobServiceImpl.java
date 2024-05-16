package org.example.jobms.job.Impl;

import org.example.jobms.job.Job;
import org.example.jobms.job.JobRepo;
import org.example.jobms.job.JobService;
import org.example.jobms.job.dto.JobWithCompanyDTO;
import org.example.jobms.job.external.Company;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {
    JobRepo jobrepo;
    public JobServiceImpl(JobRepo repo){
        this.jobrepo = repo;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {
        List<Job> jobs = jobrepo.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOS = new ArrayList<>();

        RestTemplate rt = new RestTemplate();
        for(Job j : jobs) {
            JobWithCompanyDTO jwcd = new JobWithCompanyDTO();
            jwcd.setJob(j);

            Company c = rt.getForObject("http://localhost:8081/companies/"+j.getCompanyID(), Company.class);
            jwcd.setCompany(c);
            System.out.printf(String.valueOf(c));
            jobWithCompanyDTOS.add(jwcd);
        }

        return jobWithCompanyDTOS;
    }

    @Override
    public Job getJobById(Long Id) {
        return jobrepo.findById(Id).orElse(null);
    }

    @Override
    public void createJob(Job job) {
        jobrepo.save(job);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try{
            jobrepo.deleteById(id);
            return  true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job job) {
        Optional<Job> jobOpt = jobrepo.findById(id);
        if(jobOpt.isPresent()){
            Job ujob = jobOpt.get();
            ujob.setTitle(job.getTitle());
            ujob.setDescription(job.getDescription());
            ujob.setMinSalary(job.getMinSalary());
            ujob.setMaxSalary(job.getMaxSalary());
            ujob.setLocation(job.getLocation());
            jobrepo.save(ujob);
            return true;
        }
        return  false;
    }
}
