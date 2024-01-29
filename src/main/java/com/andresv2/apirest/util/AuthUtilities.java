package com.andresv2.apirest.util;

import com.andresv2.apirest.auth.UserAuthProvider;
import com.andresv2.apirest.entities.User;
import com.andresv2.apirest.service.UserService;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class AuthUtilities {

    @Autowired
    private static UserService userService;
    @Autowired
    private static UserAuthProvider userAuthProvider;

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        return userService.findByUsername(currentPrincipalName);
    }

    public static String getCurrentUserToken() {
        User user = getCurrentUser();
        return userAuthProvider.createToken(user.getUsername());
    }
}
