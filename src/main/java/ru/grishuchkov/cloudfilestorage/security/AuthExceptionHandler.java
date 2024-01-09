package ru.grishuchkov.cloudfilestorage.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AuthExceptionHandler implements AuthenticationFailureHandler {

    private final static String ERROR_LOGIN_URL = "/login?error=";
    private final static String BAD_CREDENTIALS_MESSAGE = "Bad login or password";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        sendRedirectWithMessage(response, BAD_CREDENTIALS_MESSAGE);
    }

    private void sendRedirectWithMessage(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect(ERROR_LOGIN_URL + UriUtils.encodePath(message, StandardCharsets.UTF_8));
    }
}
