package de.fayedev.watchybackend.service;

import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.exception.ApplicationExceptionCode;
import de.fayedev.watchybackend.model.common.Email;
import de.fayedev.watchybackend.model.user.Role;
import de.fayedev.watchybackend.model.user.User;
import de.fayedev.watchybackend.model.user.dto.*;
import de.fayedev.watchybackend.repo.UserRepository;
import de.fayedev.watchybackend.security.JwtTokenProvider;
import de.fayedev.watchybackend.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class UserService {

    private final MailService mailService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${mailing.enabled}")
    private boolean mailingEnabled;

    @Value("${mailing.link}")
    private String mailingConfirmLink;

    public UserService(MailService mailService, UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(UserRegisterRequest userRegisterRequest) throws ApplicationException {
        if (userRepository.existsByUsername(userRegisterRequest.getUsername()) || userRepository.existsByEmail(userRegisterRequest.getEmail())) {
            log.info(LogMessage.USER_ALREADY_EXISTS, userRegisterRequest.getEmail(), userRegisterRequest.getUsername());
            throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_ALREADY_EXISTS,
                    MessageFormatter.format(LogMessage.USER_ALREADY_EXISTS, userRegisterRequest.getEmail(), userRegisterRequest.getUsername()).getMessage());
        }

        var uuid = UUID.randomUUID().toString();
        var user = new User();
        user.setUuid(uuid);
        user.setEmail(userRegisterRequest.getEmail());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        user.setRoles(Collections.singletonList(Role.USER));
        userRepository.save(user);
        log.info(LogMessage.USER_CREATED, user.getUuid(), user.getUsername(), user.getEmail());

        // Cannot just use the user object. No clue why honestly. Throws a transaction cascading exception even though it shouldn't be the same transaction AFAIK.
        if (mailingEnabled) {
            // Don't set token here, as the user needs to confirm their email address first.
            sendConfirm(new UserMailConfirmRequest(user.getEmail()));
        } else {
            user.setToken(jwtTokenProvider.createToken(user.getUsername(), user.getRoles()));
        }

        return new UserResponse(user);
    }

    public void sendConfirm(UserMailConfirmRequest userConfirmIn) throws ApplicationException {
        if (!mailingEnabled) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_MAILING_DISABLED, LogMessage.USER_MAILING_DISABLED);
        }

        var user = userRepository.findByEmail(userConfirmIn.getEmail()).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND_EMAIL, userConfirmIn.getEmail());
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND_EMAIL, MessageFormatter.format(LogMessage.USER_NOT_FOUND_EMAIL, userConfirmIn.getEmail()).getMessage());
        });

        if (user.isEmailConfirmed()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_ALREADY_CONFIRMED, LogMessage.USER_ALREADY_CONFIRMED);
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        user.setEmailToken(token);

        var email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Watchy - Confirm your email address");
        email.setText("Hey there!\n\nThank you for registering a new Watchy account. Please confirm your email here:\n\n" + mailingConfirmLink + "/users/confirm?emailToken=" + token);

        mailService.sendMail(email);

        log.info(LogMessage.USER_REQUEST_CONFIRM, user.getUsername(), user.getEmail(), user.getEmailToken());
    }

    public void confirm(String token) throws ApplicationException {
        if (!mailingEnabled) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_MAILING_DISABLED, LogMessage.USER_MAILING_DISABLED);
        }

        var user = userRepository.findByEmailToken(token).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND_TOKEN, token);
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND_TOKEN, MessageFormatter.format(LogMessage.USER_NOT_FOUND_TOKEN, token).getMessage());
        });

        if (user.isEmailConfirmed()) {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_ALREADY_CONFIRMED, LogMessage.USER_ALREADY_CONFIRMED);
        }

        user.setEmailConfirmed(true);
        user.setEmailToken(null);

        log.info(LogMessage.USER_CONFIRM, user.getUsername(), user.getEmail(), token);
    }

    public UserResponse login(UserLoginRequest userLoginRequest) throws ApplicationException {
        var user = getUserByUserNameOrEmailAddress(userLoginRequest.getInput());

        // Check for equals instead of equalsIgnoreCase database call
        if ((user.getUsername().equals(userLoginRequest.getInput()) || user.getEmail().equals(userLoginRequest.getInput()))
                && passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {

            if (mailingEnabled && !user.isEmailConfirmed()) {
                log.info(LogMessage.USER_LOGGED_IN_FAILED_EMAIL, user.getUsername());
                throw new ApplicationException(HttpStatus.BAD_REQUEST, ApplicationExceptionCode.USER_LOGGED_IN_FAILED_EMAIL, MessageFormatter.format(LogMessage.USER_LOGGED_IN_FAILED_EMAIL, user.getUsername()).getMessage());
            }

            String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
            user.setToken(token);
            log.info(LogMessage.USER_LOGGED_IN, user.getUsername(), token);
            return new UserResponse(user);
        } else {
            log.info(LogMessage.USER_LOGGED_IN_FAILED, user.getUsername());
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, ApplicationExceptionCode.USER_AUTH_INVALID, LogMessage.USER_AUTH_INVALID);
        }
    }

    public UserResponse changePassword(String username, UserChangePasswordRequest userChangePassword) throws ApplicationException {
        var user = getUserByUserName(username);

        if (!passwordEncoder.matches(userChangePassword.getOldPassword(), user.getPassword())) {
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, ApplicationExceptionCode.USER_AUTH_INVALID, LogMessage.USER_AUTH_INVALID);
        }

        user.setPassword(passwordEncoder.encode(userChangePassword.getNewPassword()));
        userRepository.save(user);
        log.info(LogMessage.USER_CHANGE_PASSWORD, user.getUuid(), user.getUsername());
        return new UserResponse(user);
    }

    public User getUserByUserName(String username) throws ApplicationException {
        var user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND, username);
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND, MessageFormatter.format(LogMessage.USER_NOT_FOUND, username).getMessage());
        });

        Hibernate.initialize(user.getRoles());

        return user;
    }

    public User getUserByUserNameOrEmailAddress(String input) throws ApplicationException {
        var user = userRepository.findByUsernameOrEmail(input, input).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND, input);
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND, MessageFormatter.format(LogMessage.USER_NOT_FOUND, input).getMessage());
        });

        Hibernate.initialize(user.getRoles());

        return user;
    }

    public UserResponse getUserResponseByUserName(String username) throws ApplicationException {
        var user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.info(LogMessage.USER_NOT_FOUND, username);
            return new ApplicationException(HttpStatus.NOT_FOUND, ApplicationExceptionCode.USER_NOT_FOUND, MessageFormatter.format(LogMessage.USER_NOT_FOUND, username).getMessage());
        });

        Hibernate.initialize(user.getRoles());

        return new UserResponse(user);
    }
}
