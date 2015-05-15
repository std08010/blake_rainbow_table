package com.acipi.table;

import java.io.UnsupportedEncodingException;

import fr.cryptohash.BLAKE256;

public class HashFunction
{
	public static String hash(String stringToHash) throws UnsupportedEncodingException
	{
		BLAKE256 blake = new BLAKE256();

		byte[] digest = blake.digest(stringToHash.getBytes("UTF-8"));

		StringBuilder sb = new StringBuilder(2 * digest.length);
		for (byte b : digest)
		{
			sb.append(String.format("%02x", b & 0xff));
		}

		return sb.toString();
	}
}
