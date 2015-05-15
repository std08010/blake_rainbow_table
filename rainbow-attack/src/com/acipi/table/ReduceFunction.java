package com.acipi.table;

import java.io.UnsupportedEncodingException;

import com.acipi.Dictionary;

public class ReduceFunction
{
	public static String reduce(String stringToReduce) throws UnsupportedEncodingException
	{
		StringBuilder sb = new StringBuilder();

		int length = stringToReduce.length();
		int chunk = length / Dictionary.passLen;

		for (int i = 0; i < Dictionary.passLen; i++)
		{
			int start = i * chunk;
			String subString = stringToReduce.substring(start, start + chunk);

			int number = subString.hashCode();
			int abs_number = (number < 0) ? -number : number;

			sb.append(Dictionary.charSet[abs_number % Dictionary.charSet.length]);
		}
		
		int random = stringToReduce.hashCode();
		int abs_random = (random < 0) ? -random : random;
		
		sb.setCharAt(abs_random % Dictionary.passLen, Dictionary.charSet[abs_random % Dictionary.charSet.length]);

		return sb.toString();
	}
}
