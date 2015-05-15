package com.acipi.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnectionHelper
{
	private static final String	user		= "acipi";
	private static final String	host		= "sbox.di.uoa.gr";
	private static final String	password	= "UlVCHU2TsldjRMBrYYD5Iw==";

	public static Session getSSHSession()
	{
		JSch jsch = new JSch();

		Session session = null;
		try
		{
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
		}
		catch (JSchException e)
		{
			e.printStackTrace();
		}

		return session;
	}
}
