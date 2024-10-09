package com.mart.util;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CryptoUtils {

	private static final String SALT = "f,1,o,2,o,3,d,4,c,5,a,6,t,7,e,8,r,9,i,10,n,11,g,12";
	private static final String IV = "dvugwvbhbvwivbuw";
	
	@Value("${secret_key}")
	private String secret_Key_string;

	public String encode(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	public byte[] decode(String data) {
		return Base64.getDecoder().decode(data);
	}

	// This implementation is AES Algorithm

	public String generateSecretKey(String password) throws Exception {
		String sk = password + "FOOD2024";
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(sk.toCharArray(), SALT.getBytes(), 65536, 256);
		SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return encode(secret.getEncoded());
	}

	public String encrypt(String secret, String data) throws Exception {
		SecretKey key;
		if (secret != null && !secret.isEmpty()) {
			String skey = secret.replace(" ", "+");
			byte[] decodedKey = decode(skey);
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		} else {
			String skey = secret_Key_string.replace(" ", "+");
			byte[] decodedKey = decode(skey);
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		}
		byte[] secretMessageBytes = data.getBytes(StandardCharsets.UTF_8);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] encryptedData = cipher.doFinal(secretMessageBytes);
		return new String(encode(encryptedData));
	}

	public String decrypt(String secret, String data) throws Exception {
		SecretKey key;
		if (secret != null && !secret.isEmpty()) {
			String skey = secret.replace(" ", "+");
			byte[] decodedKey = decode(skey);
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		} else {
			String skey = secret_Key_string.replace(" ", "+");
			byte[] decodedKey = decode(skey);
			key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		}
		String enc = data.replace(" ", "+");
		byte[] encryptedBytes = decode(enc);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] decryptedData = cipher.doFinal(encryptedBytes);
		return new String(decryptedData, StandardCharsets.UTF_8);
	}
}
