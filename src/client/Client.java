package client;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Client {
	private static Socket socket;

	public static void main(String[] args) {
		if(args.length != 2) {
			System.out.println("Specify the ip and port as arguments.");
			System.exit(1);
		}

		String ip = args[0];
		int port = Integer.parseInt(args[1]);
		System.out.print("Enter username: ");
		Console console = System.console();
		String name = console.readLine();
		
		try {
			socket = new Socket(ip, port);
			Thread.sleep(1000);
			
			GenerateAsymKeys keys = new GenerateAsymKeys(2048);
			boolean sendAuthName;
			
			if (!keys.keyFileExists()) {
				keys.createKeys();
				
				KeyExchange keyExchange = new KeyExchange(socket, keys, name);
				keyExchange.startExchange();
				
				System.out.println("Exchanging public keys... ");
				synchronized (keyExchange) {
					while (keyExchange.getPublicKey() == null)
						keyExchange.wait();
				}
				System.out.println("Key Received!");
				
				keys.setPublicKeyOfOtherUser(keyExchange.getPublicKey());
				keys.writeKeysToFile();
				sendAuthName = false;
			} else {
				System.out.println("Reading from key file...");
				keys.readKeysFromFile();
				sendAuthName = true;
			}	
			
			new ReadThread(socket, keys.getPrivateKey()).start();
			new WriteThread(socket, keys.getPublicKey(), name, sendAuthName).start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
