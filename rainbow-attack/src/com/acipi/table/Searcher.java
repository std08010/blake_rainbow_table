package com.acipi.table;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Altin Cipi
 *
 */
public class Searcher
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		File file = new File("E:/rainbow_table_sorted.txt");

		String myPass = "wVq7f5";
		String myHash = HashFunction.hash(myPass);
		
		long start = System.currentTimeMillis();

		String password = binarySearch(file, myHash);

		long end = System.currentTimeMillis();

		System.out.println((end - start) + " milliseconds");

		System.out.println(password);
	}

	public static String binarySearch(File file, String hashStr) throws IOException
	{
		RandomAccessFile raf = new RandomAccessFile(file, "r");

		long low = 0;
		long high = file.length();

		long p = -1;
		while (low < high)
		{
			long mid = (low + high) / 2;
			p = mid;
			while (p >= 0)
			{
				raf.seek(p);

				char c = (char) raf.readByte();
				if (c == '\n')
				{
					break;
				}
				p--;
			}
			if (p < 0)
			{
				raf.seek(0);
			}
			String line = raf.readLine();
			String[] parts = line.trim().split(":");
			String hash = parts[1];

			if (hash.compareTo(hashStr) < 0)
			{
				low = mid + 1;
			}
			else
			{
				high = mid;
			}
		}

		p = low;
		while (p >= 0)
		{
			raf.seek(p);
			if (((char) raf.readByte()) == '\n')
			{
				break;
			}
			p--;
		}

		if (p < 0)
		{
			raf.seek(0);
		}

		while (true)
		{
			String line = raf.readLine();
			String[] parts = line.trim().split(":");
			String password = parts[0];
			String hash = parts[1];

			if (line == null || !hash.startsWith(hashStr))
			{
				break;
			}
			return password;
		}

		raf.close();

		return null;
	}
}
