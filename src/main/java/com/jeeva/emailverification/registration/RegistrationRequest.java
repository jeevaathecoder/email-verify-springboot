package com.jeeva.emailverification.registration;

import lombok.Data;


public record RegistrationRequest(String firstName,
                                  String lastName,
                                  String email,
                                  String password,
                                  String role) {
}
