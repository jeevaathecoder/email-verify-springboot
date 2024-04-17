package com.jeeva.emailverification.registration;


import com.jeeva.emailverification.event.RegistrationCompleteEvent;
import com.jeeva.emailverification.registration.token.VerificationToken;
import com.jeeva.emailverification.registration.token.VerificationTokenRepository;
import com.jeeva.emailverification.user.User;
import com.jeeva.emailverification.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    private final ApplicationEventPublisher publisher;

    private final VerificationTokenRepository verificationTokenRepository;

    @PostMapping
    public String registerUser(@RequestBody  RegistrationRequest registrationRequest, final HttpServletRequest request) {
        User user = userService.registerUser(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Sucess! ,Please check your email for registration Confirmation";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token")  String token){
        VerificationToken theToken = verificationTokenRepository.findByToken(token);
        if(theToken.getUser().isEnabled()){
            return "User already verified";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equals("Valid")){
            return "User Verified Suceessfully, Now you can LOGIN";
        }
        return "Invalid Token, Please try again";
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
