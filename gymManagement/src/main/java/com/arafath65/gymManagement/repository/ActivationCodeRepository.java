package com.arafath65.gymManagement.repository;

import com.arafath65.gymManagement.entity.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationCodeRepository
        extends JpaRepository<ActivationCode, Integer> {

    Optional<ActivationCode> findByActivationCode(String activationCode);

}