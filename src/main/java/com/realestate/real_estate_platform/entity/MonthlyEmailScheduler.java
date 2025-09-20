package com.realestate.real_estate_platform.entity;

import com.realestate.real_estate_platform.repositories.PropertyRepository;
import com.realestate.real_estate_platform.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class MonthlyEmailScheduler {

    private final EmailService emailService;
    private final PropertyRepository propertyRepository; // Your repository

    public MonthlyEmailScheduler(EmailService emailService, PropertyRepository propertyRepository) {
        this.emailService = emailService;
        this.propertyRepository = propertyRepository;
    }

    // Runs on 1st day of every month at 9 AM
    @Scheduled(cron = "0 0 9 1 * ?")
    public void sendMonthlyEmails() {
        List<Property> properties = propertyRepository.findAll();
        for (Property property : properties) {
            String email = property.getOwner().getEmail(); // assuming owner entity
            String message = "Hello " + property.getOwner().getName() + ", here is your monthly property summary...";
            emailService.sendMonthlyEmail(email, "Monthly Property Summary", message);
        }
    }
}
