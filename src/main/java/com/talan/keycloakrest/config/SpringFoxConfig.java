package com.talan.keycloakrest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Configuration
public class SpringFoxConfig {

	@Value("${swagger-auth-server-url}")
	private String AUTH_SERVER;

	@Value("${swagger.credentials.secret}")
	private String CLIENT_SECRET;

	@Value("${swagger.resource}")
	private String CLIENT_ID;

	@Value("${keycloak.realm}")
	private String REALM;

	private static final String OAUTH_NAME = "Keycloak-Admin Auth Gateway";
	private static final String TITLE = "API Documentation for Keycloak Admin Rest";
	private static final String DESCRIPTION = "Keycloak Admin Web API Service with Spring Boot.";
	private static final String VERSION = "1.0";

	final List<Response> globalResponses = Arrays.asList(
			new ResponseBuilder().code("401").description("You are not authorized to view the resource").build(),
			new ResponseBuilder().code("404").description("Not found").build(),
			new ResponseBuilder().code("500").description("Internal server error").build(),
			new ResponseBuilder().code("503").description("Service Unavailable").build()
	);

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.useDefaultResponseMessages(false)
				.globalResponses(HttpMethod.GET, globalResponses)
				.globalResponses(HttpMethod.POST, globalResponses)
				.globalResponses(HttpMethod.PUT, globalResponses)
				.globalResponses(HttpMethod.DELETE, globalResponses)
				.apiInfo(apiInfo())
				.securitySchemes(Arrays.asList(securityScheme()))
				.securityContexts(Arrays.asList(securityContext())).select()
				.apis(RequestHandlerSelectors.basePackage("com.talan"))
				.paths(PathSelectors.any())
				.build();
	}

	@Bean
	public SecurityConfiguration security() {
		return SecurityConfigurationBuilder.builder()
				.realm(REALM)
				.clientId(CLIENT_ID)
				.clientSecret(CLIENT_SECRET)
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				TITLE,
				DESCRIPTION,
				VERSION,
				null,
				null,
				null, null, Collections.emptyList());
	}

	private SecurityScheme securityScheme() {
		Consumer<TokenEndpointBuilder> tokenEndpoint = new Consumer<TokenEndpointBuilder>() {
			@Override
			public void accept(TokenEndpointBuilder tokenEndpointBuilder) {
				tokenEndpointBuilder
						.url(AUTH_SERVER + "realms/" + REALM + "/protocol/openid-connect/token")
						.tokenName("oauthtoken")
						.build();
			}
		};
		Consumer<TokenRequestEndpointBuilder> tokenRequestEndpoint = new Consumer<TokenRequestEndpointBuilder>() {
			@Override
			public void accept(TokenRequestEndpointBuilder tokenRequestEndpointBuilder) {
				tokenRequestEndpointBuilder
						.clientSecretName(CLIENT_SECRET)
						.clientIdName(CLIENT_ID)
						.url(AUTH_SERVER + "realms/" + REALM + "/protocol/openid-connect/auth")
						.build();
			}
		};

		GrantType grantType =
				new AuthorizationCodeGrantBuilder()
						.tokenEndpoint(tokenEndpoint)
						.tokenRequestEndpoint(tokenRequestEndpoint)
						.build();

		SecurityScheme oauth =
				new OAuthBuilder()
						.name(OAUTH_NAME)
						.grantTypes(Arrays.asList(grantType))
						.scopes(Arrays.asList(scopes()))
						.build();
		return oauth;
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(Arrays.asList(new SecurityReference(OAUTH_NAME, scopes())))
				.build();
	}

	private AuthorizationScope[] scopes() {
		AuthorizationScope[] scopes = {};
		return scopes;
	}
}
