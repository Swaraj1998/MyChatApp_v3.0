package client;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class KeyExchange {
	private Socket socket;
	private GenerateAsymKeys keys;
	private PrintWriter out;
	private BufferedReader in;
	private String authName;
	private PublicKey inPublicKey;
	
	public KeyExchange(Socket socket, GenerateAsymKeys keys, String authName) {
		this.socket = socket;
		this.keys = keys;
		this.authName = authName;
	}
	
	public void startExchange() {
		sendPublicKey();
		receivePublicKey();
	}
	
	public void sendPublicKey() {
		new Thread(new Runnable() {
			public void run() {
				try {
					out = new PrintWriter(socket.getOutputStream(), true);
					out.println(authName);
					out.println("::KEY");
					out.println(Base64.getEncoder()
							.encodeToString(keys.getPublicKey().getEncoded()));
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void receivePublicKey() {
		new Thread(new Runnable() {
			public void run() {
				try {
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while (inPublicKey == null) {
						String msg = in.readLine();;
						if (msg.equals("::KEY")) {
							msg = in.readLine();
							X509EncodedKeySpec ks = new X509EncodedKeySpec(Base64.getDecoder().decode(msg));
							inPublicKey = KeyFactory.getInstance("RSA").generatePublic(ks);
						} else if (msg.equals("You are disconnected.")) {
							System.out.println(msg);
							System.exit(1);
						}
					}
					notifySuccess();
				} catch (IOException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public PublicKey getPublicKey() {
		return inPublicKey;
	}
	
	public void notifySuccess() {
		synchronized (this) {
			this.notifyAll();
		}
	}
}
