package main.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import main.constants.ServerConstants;

public class CredentialsHandler {

	public static boolean isValidEmail(String email) {

		String domainChars = "abcdefghijklmnopqrstuvwxyz0123456789-";

		if (email.length() < 7) {

			return false;

		}

		if (!email.contains("@") || !email.contains(".")) {

			return false;

		}

		String local = email.substring(0, email.lastIndexOf("@"));

		if (local.length() < 1) {

			return false;

		}

		if (local.contains("@") || local.startsWith(".") || local.endsWith(".") || local.contains("..")) {

			return false;

		}

		if (email.lastIndexOf("@") < email.lastIndexOf(".")) {

			String domain = email.substring(email.lastIndexOf("@") + 1, email.lastIndexOf("."));

			if (domain.length() < 1) {

				return false;
			}

			if (domain.equals("example")) {

				return false;

			}

			for (int i = 0; i < domain.length(); i++) {

				char c = domain.charAt(i);

				if (!domainChars.contains(Character.toString(c).toLowerCase())) {

					return false;

				}

			}

		} else {

			return false;

		}

		String extension = email.substring(email.lastIndexOf(".") + 1, email.length());
		if (!extension.equals("com") && !extension.equals("net")) {

			return false;

		}

		// check existing
		File f = new File(ServerConstants.pathUserData + "/credentials/emaillist.txt");

		StringBuilder sb = new StringBuilder();

		try {

			@SuppressWarnings("resource")
			InputStream in = new FileInputStream(f);

			int s;

			while ((s = in.read()) != -1) {

				sb.append((char) s);

			}

			String[] emails = sb.toString().split(System.lineSeparator());

			for (int i = 0; i < emails.length; i++) {

				if (email.equals(emails[i])) {

					return false;

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static boolean isValidUsername(String username) {

		File f = new File(ServerConstants.pathUserData + "/credentials/usernamelist.txt");

		StringBuilder sb = new StringBuilder();

		try {

			@SuppressWarnings("resource")
			InputStream in = new FileInputStream(f);

			int s;

			while ((s = in.read()) != -1) {

				sb.append((char) s);

			}

			String[] users = sb.toString().split(System.lineSeparator());

			for (int i = 0; i < users.length; i++) {

				if (username.equals(users[i])) {

					return false;

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static boolean isValidUniqueID(int id) {

		File f = new File(ServerConstants.pathUserData + "/credentials/uniqueidlist.txt");

		StringBuilder sb = new StringBuilder();

		try {

			@SuppressWarnings("resource")
			InputStream in = new FileInputStream(f);

			int s;

			while ((s = in.read()) != -1) {

				sb.append((char) s);

			}

			String[] users = sb.toString().split(System.lineSeparator());

			for (int i = 0; i < users.length; i++) {

				int num_id = Integer.parseInt(users[i]);

				if (id == num_id) {

					return false;

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static String encryptPassword(String password) {

		return password;
		
	}

	public static void saveCredentials(String creds, File f) {

		if (f.exists()) {

			try {

				FileOutputStream fos = new FileOutputStream(f, true);

				fos.write(creds.getBytes());
				fos.write(System.lineSeparator().getBytes());
				fos.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
