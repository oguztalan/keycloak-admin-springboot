package com.talan.keycloakrest.service;

import com.talan.keycloakrest.model.AccountDto;
import com.talan.keycloakrest.model.RealmRoleModel;
import com.talan.keycloakrest.model.UserCredentials;
import com.talan.keycloakrest.model.UserDto;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakAdminService {

    AccountDto createUser(AccountDto accountDto);

    void assigneeRole(String userId, String role);

    void removeRole(String userId, String role);

    List<RealmRoleModel> getAssignedRoles(String userId);

    List<RealmRoleModel> getAvailableRoles(String userId);

    List<UserDto> getUserList();

    List<RoleRepresentation> getRoles();

    UserRepresentation getUserById(String userId);

    UserDto updateUser(UserDto userDto);

    void deleteUser(String userId);

    void toggleUser(String userId, Boolean toggle);

    void resetPassword(UserCredentials userCredentials);


}
