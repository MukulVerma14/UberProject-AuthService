package com.example.uberprojectauthservice.services;

import com.example.uberprojectauthservice.dto.PassengerDto;
import com.example.uberprojectauthservice.dto.PassengerSignUpRequestDto;
import com.example.uberprojectauthservice.models.Passenger;
import com.example.uberprojectauthservice.repositories.PassengerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final PassengerRepository passengerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public AuthService(PassengerRepository passengerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passengerRepository = passengerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public PassengerDto signupPassenger(PassengerSignUpRequestDto passengerSignUpRequestDto) {
        Passenger passenger = Passenger.builder()
                .email(passengerSignUpRequestDto.getEmail())
                .name(passengerSignUpRequestDto.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignUpRequestDto.getPassword()))
                .phoneNumber(passengerSignUpRequestDto.getPhoneNumber())
                .build();

        Passenger newPassenger = passengerRepository.save(passenger);

       return PassengerDto.from(newPassenger);
    }
}

