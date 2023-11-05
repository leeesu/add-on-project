package com.onpurple.domain.report.repository;


import com.onpurple.domain.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByModifiedAtDesc();
}
