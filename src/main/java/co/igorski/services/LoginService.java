package co.igorski.services;

import co.igorski.client.WebClient;
import co.igorski.configuration.Configuration;
import co.igorski.exceptions.SnitcherException;
import co.igorski.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);
    private final Configuration configuration;
    private final WebClient webClient;

    LoginService(Configuration configuration, WebClient webClient) {
        this.configuration = configuration;
        this.webClient = webClient;
    }

    User login() {
        User user = null;
        Map<String, String> form = new HashMap<>();
        form.put("username", configuration.getUsername());
        form.put("password", configuration.getPassword());

        try {
            String loginUrl = configuration.getServerUrl() + "/login";
            int responseStatus = webClient.login(loginUrl, form);

            if (responseStatus == 302) {
                user = new User();
                user.setUsername(configuration.getUsername());
            }

        } catch (IOException | SnitcherException e) {
            LOGGER.error("Error while trying to log in.", e);
        }

        return user;
    }
}
