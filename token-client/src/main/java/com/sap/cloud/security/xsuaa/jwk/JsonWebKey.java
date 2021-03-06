package com.sap.cloud.security.xsuaa.jwk;

import javax.annotation.Nullable;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import com.sap.cloud.security.xsuaa.jwt.JwtSignatureAlgorithm;

/**
 * See also JSON Web Key (JWK) specification:
 * https://tools.ietf.org/html/rfc7517
 */
public interface JsonWebKey {
	String DEFAULT_KEY_ID = "default-kid";

	/**
	 * Returns the key algorithm a JWT is/can be signed with, e.g.
	 * {@link JwtSignatureAlgorithm#RS256}.
	 * 
	 * @return the key algorithm.
	 */
	public JwtSignatureAlgorithm getKeyAlgorithm();

	/**
	 * Returns the key id. This is used, for instance, to choose among a set of keys
	 * within a JWK Set during key rollover.
	 * 
	 * @return unique key identifier.
	 */
	@Nullable
	public String getId();

	/**
	 * Returns the public key representation.
	 * 
	 * @return the public key.
	 * @throws InvalidKeySpecException
	 *             in case the a PublicKey can not be created for this JSON web key.
	 * @throws NoSuchAlgorithmException
	 *             in case the algorithm specified as part of the JSON web key is
	 *             not supported.
	 */
	@Nullable
	public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException;

}
