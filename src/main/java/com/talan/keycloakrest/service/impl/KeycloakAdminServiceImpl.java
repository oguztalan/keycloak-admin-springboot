package com.talan.keycloakrest.service.impl;

import com.talan.keycloakrest.exception.NotFoundException;
import com.talan.keycloakrest.model.AccountDto;
import com.talan.keycloakrest.model.RealmRoleModel;
import com.talan.keycloakrest.model.UserCredentials;
import com.talan.keycloakrest.model.UserDto;
import com.talan.keycloakrest.service.KeycloakAdminService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class KeycloakAdminServiceImpl implements KeycloakAdminService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private UsersResource usersResource;
    private RealmResource realmResource;

    public KeycloakAdminServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @PostConstruct
    private void getConfigInstance(){
        this.realmResource =keycloak.realm(realm);
        this.usersResource = keycloak.realm(realm).users();
    }

    @Override
    public AccountDto createUser(AccountDto accountDto) {
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(accountDto.getPassword());
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(accountDto.getEmail());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setFirstName(accountDto.getFirstname());
        kcUser.setLastName(accountDto.getLastname());
        kcUser.setEmail(accountDto.getEmail());
        kcUser.setEnabled(accountDto.getIsEnabled());
        kcUser.setEmailVerified(accountDto.getEmailVerified());

        if (Objects.nonNull(usersResource)) {
            Response response = usersResource.create(kcUser);
            accountDto.setStatusCode(response.getStatus());
            accountDto.setStatus(response.getStatusInfo().toString());

            if (response.getStatus() == 201) {
                return accountDto;
            }else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
        else throw new NotFoundException("Keycloak users not found");
    }

    @Override
    public void assigneeRole(String userId, String role) {
        RoleRepresentation realmRoleUser = realmResource.roles().get(role).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().add(Collections.singletonList(realmRoleUser));
    }

    @Override
    public void removeRole(String userId, String role) {
        RoleRepresentation realmRoleUser = realmResource.roles().get(role).toRepresentation();
        UserResource userResource = usersResource.get(userId);
        userResource.roles().realmLevel().remove(Collections.singletonList(realmRoleUser));
    }

    @Override
    public List<RealmRoleModel> getAssignedRoles(String userId) {
        UserResource userResource = usersResource.get(userId);

        MappingsRepresentation representationRoles = userResource.roles().getAll();
        List<RealmRoleModel> realmRolesList = representationRoles.getRealmMappings()
                .stream()
                .map(role -> {
                    RealmRoleModel roleModel = new RealmRoleModel();
                    roleModel.setName(role.getName());
                    roleModel.setContainerId(role.getContainerId());
                    roleModel.setId(role.getId());
                    return roleModel;
                }).collect(Collectors.toList());
        return realmRolesList;
    }

    @Override
    public List<RealmRoleModel> getAvailableRoles(String userId) {
        UserResource userResource = usersResource.get(userId);
        List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

        List<RealmRoleModel> realmRoles = roleRepresentationList.stream()
                .map(role -> {
                    RealmRoleModel roleModel = new RealmRoleModel();
                    roleModel.setName(role.getName());
                    roleModel.setContainerId(role.getContainerId());
                    roleModel.setId(role.getId());
                    return roleModel;
                }).collect(Collectors.toList());
        return realmRoles;
    }

    @Override
    public List<UserDto> getUserList() {
        List<UserRepresentation> kcUsers = this.keycloak.realm(realm).users().list();

        return kcUsers.stream().map(kc -> {
            UserDto userDto = new UserDto();
            userDto.setId(kc.getId());
            userDto.setFirstName(kc.getFirstName());
            userDto.setLastName(kc.getLastName());
            userDto.setEmail(kc.getEmail());
            userDto.setEnabled(kc.isEnabled());
            userDto.setCreatedTimestamp(kc.getCreatedTimestamp());
            return userDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RoleRepresentation> getRoles() {
        return realmResource.roles().list();
    }

    @Override
    public UserRepresentation getUserById(String userId) {
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        if (Objects.nonNull(user))
            return user;
        else
            throw new NotFoundException("User Not Found: User Id: " + userId);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserRepresentation user = usersResource.get(userDto.getId()).toRepresentation();
        // change user
        user.setEnabled(userDto.getEnabled());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        //update
        usersResource.get(userDto.getId()).update(user);

        return userDto ;
    }

    @Override
    public void deleteUser(String userId) {
        UserResource userResource = usersResource.get(userId);
        userResource.remove();
    }

    @Override
    public void toggleUser(String userId, Boolean toggle) {
        UserRepresentation user = usersResource.get(userId).toRepresentation();
        user.setEnabled(toggle);
        usersResource.get(userId).update(user);
    }

    @Override
    public void resetPassword(UserCredentials userCredentials) {
        // Define password credential
        if (userCredentials.getPassword().equalsIgnoreCase(userCredentials.getConfirmPassword())) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(userCredentials.getPassword().trim());

            // Set password credential
            usersResource.get(userCredentials.getUserId()).resetPassword(passwordCred);
        }
        else throw new NotFoundException("Passwords not matched");
    }


    private static CredentialRepresentation  createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }
}
