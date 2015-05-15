package com.acipi;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.acipi.ssh.SSHConnectionHelper;
import com.acipi.table.HashFunction;
import com.acipi.table.ReduceFunction;
import com.acipi.table.Searcher;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Application
{
	public static final int	chainLength	= 1000;

	private static File		file		= new File("E:/rainbow_table_sorted.txt");
	private static Session	session;
	private static Channel	channel1;
	private static Channel	channel2;

	public static void main(String[] args) throws JSchException, InterruptedException, NoSuchAlgorithmException, IOException
	{
		session = SSHConnectionHelper.getSSHSession();

		channel1 = session.openChannel("shell");
		channel1.connect();
		BufferedReader dataIn1 = new BufferedReader(new InputStreamReader(channel1.getInputStream()));
		DataOutputStream dataOut1 = new DataOutputStream(channel1.getOutputStream());

		channel2 = session.openChannel("shell");
		channel2.connect();
		BufferedReader dataIn2 = new BufferedReader(new InputStreamReader(channel2.getInputStream()));
		DataOutputStream dataOut2 = new DataOutputStream(channel2.getOutputStream());

		readShell(dataIn1, 1);
		readShell(dataIn2, 2);

		dataOut1.writeBytes("python ./dbus_listener.py" + "\r\n");
		dataOut1.flush();
		readDbusShell(dataIn1, dataOut1, dataIn2, dataOut2);

		dataIn1.close();
		dataOut1.close();

		dataIn2.close();
		dataOut2.close();

		session.disconnect();
		channel1.disconnect();
		channel2.disconnect();

		//
		// TEST
		//

//		 String myPass = findNextPassword("XYL9ix");
//		 // String myPass = "XYL9ix";
//		
//		 String myHash = HashFunction.hash(myPass);
//		 // String myHash = "3451a0e680aa784f2960bb0b84818e68fec48721a7a2a2630742b110544fd17d";
//		
//		 System.out.println(myPass);
//		 System.out.println(myHash);
//		
//		 String found = findPassword(myHash);
//		
//		 if (found != null)
//		 {
//		 System.out.println("Your password is " + found);
//		 StringSelection selection = new StringSelection(found);
//		 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		 clipboard.setContents(selection, selection);
//		 }
//		 else
//		 {
//		 System.out.println("Sorry i could not find your password :(");
//		 }

//		testOldHashes();
	}

	private static void testOldHashes() throws IOException
	{
		List<String> hashes = new ArrayList<String>();

		FileInputStream fstream = new FileInputStream("E:/previous_hashes");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		while ((strLine = br.readLine()) != null && !strLine.equals(""))
		{
			hashes.add(strLine.trim());
		}

		br.close();
		fstream.close();

		int found = 0;

		for (String hash : hashes)
		{
			String foundPass = findPassword(hash);

			if (foundPass != null)
			{
				System.out.println("Your password is " + foundPass);
				found++;
			}
			else
			{
				System.out.println("Sorry i could not find your password :(");
			}
		}

		System.out.println("You found " + found + " passwords");
	}

	private static void readShell(BufferedReader dataIn, int terminalNo) throws IOException
	{
		String line = dataIn.readLine();
		while (!line.startsWith("acipi@sbox:~$"))
		{
			System.out.println("Terminal " + terminalNo + ": " + line);
			line = dataIn.readLine();
		}
		System.out.println("Terminal " + terminalNo + ": " + line);
		System.out.flush();
	}

	private static void readDbusShell(BufferedReader dataIn1, DataOutputStream dataOut1, BufferedReader dataIn2, DataOutputStream dataOut2) throws IOException, InterruptedException
	{
		String hashValue = null;

		String line = dataIn1.readLine();
		while (!line.startsWith("acipi@sbox:~$"))
		{
			System.out.println("Terminal 1: " + line);

			if (line.startsWith("Hash value is ") && line.contains(":") && !line.contains("."))
			{
				hashValue = line.substring(line.indexOf(":") + 1);
				System.out.println("###############" + hashValue + "############");

				String password = null;

				if (hashValue != null)
				{
					password = findPassword(hashValue);
				}

				if (password == null)
				{
					System.out.println("Sorry i could not find the password :(");
				}
				else
				{
					System.out.println("Your password is " + password);
					StringSelection selection = new StringSelection(password.trim());
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
					Thread.sleep(1000);
					dataOut2.writeBytes("nc localhost 4433" + "\r\n");
					dataOut2.flush();
					readLoginService(dataIn2, dataOut2, password);
				}
			}

			line = dataIn1.readLine();
		}
		System.out.println("Terminal 1: " + line);
		System.out.flush();
	}

	private static void readLoginService(BufferedReader dataIn, DataOutputStream dataOut, String password) throws IOException
	{
		String line = dataIn.readLine();
		while (!line.startsWith("acipi@sbox:~$"))
		{
			System.out.println("Terminal 2: " + line);

			if (line.contains("password:"))
			{
				dataOut.write((password.trim() + "\n").getBytes("UTF-8"));
				dataOut.flush();
			}

			if (line.startsWith("Nop, try again mate."))
			{
				dataOut.write(3);
				dataOut.flush();
			}

			line = dataIn.readLine();
		}
		System.out.println("Terminal 2: " + line);
		System.out.flush();
	}

	private static String findNextPassword(String password) throws UnsupportedEncodingException
	{
		String temp = password;

		for (int i = 0; i < 500; i++)
		{
			temp = HashFunction.hash(temp);
			temp = ReduceFunction.reduce(temp);
		}

		return temp;
	}

	private static String findPassword(String hash) throws IOException
	{
		long start = System.currentTimeMillis();

		int falsePositives = 0;
		int step = 0;
		String temp = hash;
		String existent = Searcher.binarySearch(file, temp);
		String foundHash = null;
		String foundPass = null;
		boolean found = false;

		while (!found)
		{
			while (existent == null && step < chainLength)
			{
				step++;

				temp = ReduceFunction.reduce(temp);
				temp = HashFunction.hash(temp);

				existent = Searcher.binarySearch(file, temp);
			}

			String currentPass = existent;
			String currentHash = null;
			for (int i = 0; i < chainLength - step; i++)
			{
				currentHash = HashFunction.hash(currentPass);
				currentPass = ReduceFunction.reduce(currentHash);
			}

			foundPass = currentPass;

			if (foundPass == null)
			{
				long end = System.currentTimeMillis();

				System.out.println((end - start) + " milliseconds");
				System.out.println(falsePositives);
				return null;
			}

			foundHash = HashFunction.hash(foundPass);

			if (hash.equals(foundHash))
			{
				found = true;
			}
			else
			{
				falsePositives++;
				existent = null;
			}
		}

		long end = System.currentTimeMillis();

		System.out.println((end - start) + " milliseconds");
		System.out.println(falsePositives);

		return foundPass;
	}
}
