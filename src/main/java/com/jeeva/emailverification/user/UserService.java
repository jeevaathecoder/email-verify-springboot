package com.jeeva.emailverification.user;

import com.jeeva.emailverification.exception.UserAlreadyExistsException;
import com.jeeva.emailverification.registration.RegistrationRequest;
import com.jeeva.emailverification.registration.token.VerificationToken;
import com.jeeva.emailverification.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.email());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        var user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return userRepository.save(user);

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User user, String Token) {

        var verificationToken = new VerificationToken(user, Token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if (token == null) {
            return "Invalid Verfication Token";
        }
        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if (token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(token);
            return "Token already Expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }

}
