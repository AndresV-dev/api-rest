package com.andresv2.apirest.util;

import com.andresv2.apirest.auth.UserAuthProvider;
import com.andresv2.apirest.entities.User;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class AuthUtilities {

    @Autowired
    private static UserAuthProvider userAuthProvider;

    public static User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getCurrentUserToken() {
        User user = getCurrentUser();
        return userAuthProvider.createToken(user.getUsername());
    }
}
