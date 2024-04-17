package com.jeeva.emailverification.registration.token;

import org.antlr.v4.runtime.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long>{
    public VerificationToken findByToken(String token);
}
