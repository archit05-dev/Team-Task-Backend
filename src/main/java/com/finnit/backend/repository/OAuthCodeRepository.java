
package com.finnit.backend.repository;

import com.finnit.backend.entity.OAuthCode;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OAuthCodeRepository
        extends JpaRepository<OAuthCode, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM OAuthCode c WHERE c.codeHash = :codeHash")
    Optional<OAuthCode> findByCodeHashForUpdate(
            @Param("codeHash") String codeHash
    );
}
