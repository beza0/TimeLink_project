package com.timebank.timebank.user;

import com.timebank.timebank.config.JwtService;
import com.timebank.timebank.exchange.ExchangeRequestRepository;
import com.timebank.timebank.skill.SkillRepository;
import com.timebank.timebank.user.dto.LoginRequest;
import com.timebank.timebank.user.dto.LoginResponse;
import com.timebank.timebank.user.dto.RegisterRequest;
import com.timebank.timebank.user.dto.UpdateUserProfileRequest;
import com.timebank.timebank.user.dto.UserDashboardResponse;
import com.timebank.timebank.user.dto.UserProfileResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SkillRepository skillRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       SkillRepository skillRepository,
                       ExchangeRequestRepository exchangeRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.skillRepository = skillRepository;
        this.exchangeRequestRepository = exchangeRequestRepository;
    }

    public User register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Bu email zaten kayıtlı.");
        }

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");
        user.setTimeCreditMinutes(120); // başlangıç kredisi

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email veya şifre hatalı"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email veya şifre hatalı");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    public UserProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Kullanıcı bulunamadı"));

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getBio(),
                user.getPhone(),
                user.getTimeCreditMinutes()
        );
    }

    public UserProfileResponse updateMyProfile(String email, UpdateUserProfileRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Kullanıcı bulunamadı"));

        user.setFullName(req.getFullName().trim());
        user.setBio(req.getBio() != null ? req.getBio().trim() : null);
        user.setPhone(req.getPhone() != null ? req.getPhone().trim() : null);

        User saved = userRepository.save(user);

        return new UserProfileResponse(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getBio(),
                saved.getPhone(),
                saved.getTimeCreditMinutes()
        );
    }

    public UserDashboardResponse getMyDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Kullanıcı bulunamadı"));

        long mySkillsCount = skillRepository.countByOwnerEmail(email);
        long sentRequestsCount = exchangeRequestRepository.countByRequesterEmail(email);
        long receivedRequestsCount = exchangeRequestRepository.countBySkillOwnerEmail(email);

        return new UserDashboardResponse(
                user.getFullName(),
                user.getTimeCreditMinutes(),
                mySkillsCount,
                sentRequestsCount,
                receivedRequestsCount
        );
    }
}