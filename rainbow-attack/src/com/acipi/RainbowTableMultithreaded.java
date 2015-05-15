package com.acipi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.acipi.table.HashFunction;
import com.acipi.table.ReduceFunction;

public class RainbowTableMultithreaded
{
	public static final int		chainLen	= 1000;
	private static final int	threadsNum	= 5;

	private static PrintWriter	writer;

	public static synchronized void println(String string)
	{
		writer.println(string);
		writer.flush();
	}

	public static void main(String[] args) throws IOException, InterruptedException
	{
		writer = new PrintWriter("E:/rainbow_table.txt", "UTF-8");

		long start = System.currentTimeMillis();

		int sublistSize = Dictionary.rowsNum / threadsNum;

		List<MyThread> threads = new ArrayList<MyThread>();

		for (int i = 0; i < threadsNum; i++)
		{
			MyThread thread = new MyThread((i * sublistSize) + 1);
			thread.start();

			threads.add(thread);
		}

		for (MyThread myThread : threads)
		{
			myThread.join();
		}

		long end = System.currentTimeMillis();

		System.out.println((end - start) + " milliseconds");

		writer.close();
	}

	public static class MyThread extends Thread
	{
		private int	startLine;
		private int	sublistSize;

		public MyThread(int startLine)
		{
			this.startLine = startLine;
			this.sublistSize = Dictionary.rowsNum / threadsNum;
		}

		@Override
		public void run()
		{
			try
			{
				FileInputStream fstream = new FileInputStream("E:/dictionary.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

				for (int i = 1; i < startLine; i++)
				{
					br.readLine();
				}

				for (int i = 0; i < this.sublistSize; i++)
				{
					String password = br.readLine();

					String currentPass = password;
					String currentHash = null;
					currentHash = HashFunction.hash(currentPass);

					for (int j = 0; j < chainLen; j++)
					{
						currentPass = ReduceFunction.reduce(currentHash);
						currentHash = HashFunction.hash(currentPass);
					}

					println(password + ":" + currentHash);
				}

				br.close();
				fstream.close();
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
