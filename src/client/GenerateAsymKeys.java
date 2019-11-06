package client;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class GenerateAsymKeys {
	private KeyPairGenerator keyGen;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private static final String keyFileName = "keys.txt";
	
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
	
	public void setPublicKeyOfOtherUser(PublicKey k) {
		this.publicKey = k;
	}
	
	public void writeKeysToFile() throws IOException {
		File keyFile = new File(keyFileName);
		if (keyFile.exists()) {
			keyFile.delete();
		}
		keyFile.createNewFile();
		PrintWriter fout = new PrintWriter(keyFile);
		fout.println(Base64.getEncoder()
				.encodeToString(publicKey.getEncoded()));
		fout.println(Base64.getEncoder()
				.encodeToString(privateKey.getEncoded()));
		fout.flush();
		fout.close();
	}
	
	public boolean keyFileExists() {
		return new File(keyFileName).exists();
	}
	
	public void readKeysFromFile() throws IOException, 
				InvalidKeySpecException, NoSuchAlgorithmException {
		File keyFile = new File(keyFileName);
		if (!keyFile.exists()) {
			System.err.println("Key file does not exist!");
			return;
		}
		
		BufferedReader fin = new BufferedReader(new FileReader(keyFile));
		byte[] pub = Base64.getDecoder().decode(fin.readLine());
		byte[] pri = Base64.getDecoder().decode(fin.readLine());
		fin.close();
		
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pub);
		publicKey = KeyFactory.getInstance("RSA").generatePublic(pubSpec);
		
		PKCS8EncodedKeySpec priSpec = new PKCS8EncodedKeySpec(pri);
		privateKey = KeyFactory.getInstance("RSA").generatePrivate(priSpec);
	}
}
