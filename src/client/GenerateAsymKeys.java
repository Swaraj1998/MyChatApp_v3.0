package client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class GenerateAsymKeys {
	private KeyPairGenerator keyGen;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	public GenerateAsymKeys(int keySize) throws NoSuchAlgorithmException {
		keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(keySize);
	}
	
	public void createKeys() {
		KeyPair pair = keyGen.generateKeyPair();
		publicKey = pair.getPublic();
		privateKey = pair.getPrivate();
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public PrivateKey getPrivateKey() {
		return privateKey;
	}
}
