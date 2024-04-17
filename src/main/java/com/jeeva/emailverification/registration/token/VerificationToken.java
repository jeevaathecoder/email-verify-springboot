package com.jeeva.emailverification.registration.token;

import com.jeeva.emailverification.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;

    private static final int EXPIRATION_TIME = 10;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public VerificationToken(String token) {
        super();
        this.token = token;
        this.expirationTime = getTokenExpirationTime();
    }

    public VerificationToken(User user,String token) {
        super();
        this.user = user;
        this.token = token;
        this.expirationTime = getTokenExpirationTime();
    }


    private Date getTokenExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
