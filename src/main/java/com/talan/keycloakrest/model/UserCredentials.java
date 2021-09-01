package com.talan.keycloakrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCredentials {
    
    private String password;
    private String confirmPassword;
    private String userId;

}
