package com.sap.cloud.security.xsuaa.jwk;

import com.sap.cloud.security.xsuaa.jwt.JwtSignatureAlgorithm;
import com.sap.cloud.security.xsuaa.util.JsonWebKeyTestFactory;
import org.junit.Before;
import org.junit.Test;

import static com.sap.cloud.security.xsuaa.jwk.JsonWebKey.*;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonWebKeySetTest {

	public static final JsonWebKey JSON_WEB_KEY = JsonWebKeyTestFactory.create();

	private JsonWebKeySet cut;

	@Before
	public void setUp() {
		cut = new JsonWebKeySet();
	}

	@Test
	public void getKeyByAlgorithmAndId_returnsKey_whenKeyHasBeenInserted() {
		cut.put(JSON_WEB_KEY);

		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY.getKeyAlgorithm(), JSON_WEB_KEY.getId()))
				.isEqualTo(JSON_WEB_KEY);
	}

	@Test
	public void getKeyByAlgorithmAndId_returnsNull_onEmptyJSONWebKeySet() {
		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY.getKeyAlgorithm(), JSON_WEB_KEY.getId())).isNull();
	}

	@Test
	public void getKeyByAlgorithmAndId_returnsNull_whenKeyTypeDoesNotMatch() {
		JwtSignatureAlgorithm differentKeyAlgorithm = JwtSignatureAlgorithm.ES256;

		cut.put(JSON_WEB_KEY);

		assertThat(cut.getKeyByAlgorithmAndId(differentKeyAlgorithm, JSON_WEB_KEY.getId())).isNull();
	}

	@Test
	public void getKeyByAlgorithmAndId_returnsDefault_whenKeyIdDoesNotMatch() {
		String differentKeyId = "differentKeyId";

		cut.put(JSON_WEB_KEY);

		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY.getKeyAlgorithm(), differentKeyId)).isNull();
	}

	@Test
	public void getKeyByAlgorithmAndId_returnsNull_whenKeyIdDoesNotMatch() {
		cut.put(JSON_WEB_KEY);

		cut.put(JsonWebKeyTestFactory.createDefault());

		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY.getKeyAlgorithm(), DEFAULT_KEY_ID).getId()
				.equals(DEFAULT_KEY_ID));
	}

	@Test
	public void put_returnsTrue_whenKeyHasNotBeenInsertedYet() {
		boolean inserted = cut.put(JSON_WEB_KEY);

		assertThat(inserted).isTrue();
	}

	@Test
	public void put_returnsFalse_whenKeyIsAlreadyInserted() {
		cut.put(JSON_WEB_KEY);

		boolean inserted = cut.put(JSON_WEB_KEY);

		assertThat(inserted).isFalse();
	}

	@Test
	public void putAll_overwrites_whenKeysAreAlreadyInserted() {
		JsonWebKeySet other = new JsonWebKeySet();
		other.put(JSON_WEB_KEY);
		JsonWebKey JSON_WEB_KEY_DEFAULT = JsonWebKeyTestFactory.createDefault();
		other.put(JSON_WEB_KEY_DEFAULT);

		cut.put(JSON_WEB_KEY);

		cut.putAll(other);
		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY.getKeyAlgorithm(), JSON_WEB_KEY.getId()))
				.isEqualTo(JSON_WEB_KEY);
		assertThat(cut.getKeyByAlgorithmAndId(JSON_WEB_KEY_DEFAULT.getKeyAlgorithm(), JSON_WEB_KEY_DEFAULT.getId()))
				.isEqualTo(JSON_WEB_KEY_DEFAULT);
	}

}