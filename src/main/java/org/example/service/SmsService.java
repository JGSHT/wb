package org.example.service;


import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class SmsService {

    private final UserService userService;

    public Mono<UserDetails> checkAndReturn(String phone, String smsCode){
        return checkSms(smsCode).flatMap(isCheck -> {
            if (isCheck){
                return userService.findByPhone(phone);
            }else {
                return Mono.empty();
            }
        });
    }

    private Mono<Boolean> checkSms(String smsCode){
        return Mono.just(smsCode.equals("666666"));
    }
}
