package com.goach_backend.goach.logic.entity.user;

import com.goach_backend.goach.logic.entity.role.RoleEnum;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleEnum roleEnum;

    private final UserRepository userRepository;

    private  final PasswordEncoder passwordEncoder;

    public AdminSeeder( UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleEnum = RoleEnum.ADMIN;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void  onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent){
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        User superAdmin = new User();
        superAdmin.setName("Admin");
        superAdmin.setEmail("super.admin@gmail.com");
        superAdmin.setPassword("superadmin123");
        superAdmin.setRole(RoleEnum.ADMIN);

        var user = new User();
        user.setName(superAdmin.getName());
        user.setEmail(superAdmin.getEmail());
        user.setPassword(passwordEncoder.encode(superAdmin.getPassword()));
        user.setRole(superAdmin.getRole());

        userRepository.save(user);
    }
}
