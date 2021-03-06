package com.sap.cloud.security.token;

import com.sap.cloud.security.config.Service;
import com.sap.cloud.security.config.cf.CFConstants;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XsuaaTokenTest {

	private static final String APP_ID = "app1";

	private final XsuaaToken scopesToken;
	private XsuaaToken clientCredentialsToken;
	private XsuaaToken userToken;

	public XsuaaTokenTest() throws IOException {
		clientCredentialsToken = new XsuaaToken(
				IOUtils.resourceToString("/xsuaaCCAccessTokenRSA256.txt", UTF_8));
		userToken = new XsuaaToken(IOUtils.resourceToString("/xsuaaUserAccessTokenRSA256.txt", UTF_8));

		scopesToken = new XsuaaToken(IOUtils.resourceToString("/xsuaaScopesTokenRSA256.txt", UTF_8));
	}

	@Test
	public void constructor_raiseIllegalArgumentExceptions() {
		assertThatThrownBy(() -> {
			new XsuaaToken("");
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("accessToken must not be null / empty");

		assertThatThrownBy(() -> {
			new XsuaaToken("abc");
		}).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("JWT token does not consist of 'header'.'payload'.'signature'.");
	}

	@Test
	public void getScopes() {
		assertThat(clientCredentialsToken.getScopes()).containsExactly("ROLE_SERVICEBROKER", "uaa.resource");
	}

	@Test
	public void hasScope_scopeExists_isTrue() {
		assertThat(clientCredentialsToken.hasScope("ROLE_SERVICEBROKER")).isTrue();
		assertThat(clientCredentialsToken.hasScope("uaa.resource")).isTrue();
	}

	@Test
	public void hasScope_scopeDoesNotExist_isFalse() {
		assertThat(clientCredentialsToken.hasScope("scopeDoesNotExist")).isFalse();
	}

	@Test
	public void hasLocalScope() {
		scopesToken.withScopeConverter(new XsuaaScopeConverter(APP_ID));
		assertThat(scopesToken.hasScope(APP_ID + ".scope")).isTrue();
		assertThat(scopesToken.hasLocalScope("scope")).isTrue();
		assertThat(scopesToken.hasScope("openid")).isTrue();
		assertThat(scopesToken.hasLocalScope("openid")).isTrue();
	}

	@Test
	public void getUserPrincipal() {
		assertThat(userToken.getClaimAsString(TokenClaims.XSUAA.USER_NAME)).isEqualTo("testUser");
		assertThat(userToken.getClaimAsString(TokenClaims.XSUAA.ORIGIN)).isEqualTo("userIdp");
		assertThat(userToken.getPrincipal()).isNotNull();
		assertThat(userToken.getPrincipal().getName()).isEqualTo("user/userIdp/testUser");
	}

	@Test
	public void getClientPrincipal() {
		assertThat(clientCredentialsToken.getClaimAsString(TokenClaims.XSUAA.CLIENT_ID)).isEqualTo("sap_osb");
		assertThat(clientCredentialsToken.getPrincipal()).isNotNull();
		assertThat(clientCredentialsToken.getPrincipal().getName()).isEqualTo("client/sap_osb");
	}

	@Test
	public void getBearerAccessToken() {
		assertThat(userToken.getBearerAccessToken()).startsWith("Bearer ");
	}

	@Test
	public void getGrantType() {
		assertThat(clientCredentialsToken.getGrantType()).isEqualTo(GrantType.CLIENT_CREDENTIALS);
		assertThat(userToken.getGrantType()).isEqualTo(GrantType.USER_TOKEN);
	}

	@Test
	public void getService() {
		assertThat(userToken.getService()).isEqualTo(Service.XSUAA);
	}

	@Test
	public void getUniquePrincipalName() {
		assertThat(XsuaaToken.getUniquePrincipalName("origin", "user"))
				.isEqualTo("user/origin/user");
	}

	@Test
	public void getUniquePrincipalName_raiseIllegalArgumentExceptions() {
		assertThatThrownBy(() -> {
			XsuaaToken.getUniquePrincipalName("origin/", "user");
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("/");

		assertThatThrownBy(() -> {
			XsuaaToken.getUniquePrincipalName("origin", "");
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("User");

		assertThatThrownBy(() -> {
			XsuaaToken.getUniquePrincipalName("", "user");
		}).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Origin");
	}

}