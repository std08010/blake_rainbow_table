package com.acipi;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Dictionary
{
	public static final char	charSet[]	= { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
			'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '!', '@' };
	public static final int		rowsNum		= 35000000;
	public static final int		passLen		= 6;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException
	{
		Random rand = new Random();
		HashMap<String, String> passMap = new HashMap<String, String>();

		int count = 0;

		while (passMap.size() < rowsNum)
		{
			String pass = "";
			for (int j = 0; j < passLen; j++)
			{
				pass += charSet[rand.nextInt(charSet.length - 1)];
			}

			passMap.put(pass, null);
			count++;
		}

		System.out.println(count);

		PrintWriter writer = new PrintWriter("E:/dictionary.txt", "UTF-8");
		Iterator it = passMap.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry pair = (Map.Entry) it.next();
			writer.println(pair.getKey());
			it.remove();
		}

		writer.close();
	}
}
