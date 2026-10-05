package com.itsmartsystems.bookyourseat;

import com.itsmartsystems.bookyourseat.model.User;
import com.itsmartsystems.bookyourseat.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BookYourSeatApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookYourSeatApplication.class, args);
    }

}
