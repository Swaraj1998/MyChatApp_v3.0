package client;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;

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
			keys.createKeys();
			
			KeyExchange keyExchange = new KeyExchange(socket, keys, name);
			keyExchange.startExchange();
			
			System.out.println("Exchanging public keys... ");
			synchronized (keyExchange) {
				while (keyExchange.getPublicKey() == null)
					keyExchange.wait();
			}
			System.out.println("Key Received!");
			
			new ReadThread(socket, keys.getPrivateKey()).start();
			new WriteThread(socket, keyExchange.getPublicKey(), name).start();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
