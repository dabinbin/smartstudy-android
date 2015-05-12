package com.innobuddy.SmartStudy.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;
import android.util.Base64;

/**
 * 3重DES加密和解密算法的工具类
 * 
 * @author tangyichao
 * 
 */
public class ThreeDes {
	// private static final Logger log =
	// LoggerFactory.getLogger(ThreeDes.class);
	private static final String Algorithm = "DESede"; // 定义 加密算法,可用
														// DES,DESede,Blowfish

	// keybyte为加密密钥，长度为24字节
	// src为被加密的数据缓冲区（源）
	@SuppressLint("TrulyRandom")
	public static byte[] encryptMode(byte[] src, byte[] keybyte) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 加密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e) {
			// log.error(e.getMessage(), e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			// log.error(e.getMessage(), e);
		} catch (java.lang.Exception e) {
			// log.error(e.getMessage(), e);
		}
		return null;
	}

	// keybyte为加密密钥，长度为24字节
	// src为加密后的缓冲区
	public static byte[] decryptMode(byte[] src, byte[] keybyte) {
		try {
			// 生成密钥
			SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);

			// 解密
			Cipher c1 = Cipher.getInstance(Algorithm);
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e) {
			// log.error(e.getMessage(), e);
		} catch (javax.crypto.NoSuchPaddingException e) {
			// log.error(e.getMessage(), e);
		} catch (java.lang.Exception e) {
			// log.error(e.getMessage(), e);
		}
		return null;
	}

	// 转换成十六进制字符串
	@SuppressLint("DefaultLocale")
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = hs + ":";
		}
		return hs.toUpperCase();
	}

	/**
	 * <pre>
	 * 加密
	 * </pre>
	 * 
	 * @param str
	 * @param key
	 * @return
	 */
	public static String dataEncrypt(String str, byte[] key) {
		String encrypt = null;
		try {
			byte[] ret = encryptMode(str.getBytes("UTF-8"), key);
			// System.out.println(new String(ret,"UTF-8"));
			encrypt = new String(Base64.encode(ret, 0));
		} catch (Exception e) {
			// System.out.print(e);
			encrypt = str;
		}
		return encrypt;
	}

	/**
	 * <pre>
	 * 解密
	 * </pre>
	 * 
	 * @param str
	 * @param key
	 * @return
	 */
	public static String dataDecrypt(String str, byte[] key) {
		String decrypt = null;
		try {
			byte[] ret = decryptMode(Base64.decode(str.replaceAll(" ", "+").getBytes("UTF-8"), 0), key);
			decrypt = new String(ret, "UTF-8");
		} catch (Exception e) {
			// System.out.print(e);
			decrypt = str;
		}
		return decrypt;
	}

}