package com.talan.keycloakrest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private int statusCode;
    private String status;
    private Boolean isEnabled;
    private Boolean emailVerified;

}
