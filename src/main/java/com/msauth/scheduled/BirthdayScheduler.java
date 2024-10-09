package com.msauth.scheduled;

import com.msauth.entity.UserEntity;
import com.msauth.repository.UserRepository;
import com.msauth.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BirthdayScheduler {

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Scheduled(cron = "0 1 0 * * ?")  // At 00:01 AM every day
    public void checkAndSendBirthdayEmails() {
        System.out.println("50 sec");
        LocalDate today = LocalDate.now();
        System.out.println("LocalDate.now()" + LocalDate.now());
        List<UserEntity> usersWithBirthdayToday = userRepository.findByDateOfBirth(today);

        if (usersWithBirthdayToday.isEmpty()) {
            log.info("No birthdays found for today: {}", today);
            return;
        }

        for (UserEntity user : usersWithBirthdayToday) {
            emailService.sendBirthdayEmail(user.getEmail());
            log.info("Birthday email sent to: {}", user.getEmail());
        }
    }
}