package com.example.mailsender.mail.repository;

import com.example.mailsender.mail.domain.MailRequest;
import com.example.mailsender.mail.domain.MailStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MailRequestRepository extends JpaRepository<MailRequest, Long> {

    List<MailRequest> findTop100ByStatusOrderByCreatedAtAsc(MailStatus status);


    @Query("""
                SELECT m\s
                FROM MailRequest m
                WHERE (:status IS NULL OR m.status = :status)
                  AND (:from IS NULL OR m.createdAt >= :from)
                  AND (:to IS NULL OR m.createdAt <= :to)
                ORDER BY m.createdAt DESC
            """)
    List<MailRequest> search(@Param("status") MailStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<MailRequest> findByStatusOrderByCreatedAtAsc(MailStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MailRequest m " +
            "where m.status = :status " +
            "order by m.createdAt asc")
    List<MailRequest> findReadyForUpdate(@Param("status") MailStatus status, Pageable pageable);
}
