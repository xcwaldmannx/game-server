package main.server;

import java.io.File;
import java.util.Random;

import com.waldmanngames.networkinglibrary.datahandling.DataHandler;
import com.waldmanngames.networkinglibrary.datahandling.DataTree;
import com.waldmanngames.networkinglibrary.network.Network;

import main.constants.ClientConstants;
import main.constants.NetworkConstants;
import main.constants.ServerConstants;

public class Server extends Network {

	private File emailListFile, usernameListFile, uniqueidListFile;

	private static DataTree dataToSend;

	boolean isDataReady;

	public Server(String ipAddress, int port) {
		super(ipAddress, port);

		emailListFile = new File(ServerConstants.pathEmailList);
		usernameListFile = new File(ServerConstants.pathUernameList);
		uniqueidListFile = new File(ServerConstants.pathUniqueIDList);

	}

	@Override
	protected void updateReceive() {

		String receivedData = DataTransferer().receiveData();

		DataTree dt = DataHandler.getStringToTree(receivedData);

		String sessionid = null;

		if (dt.getMetaData().containsKey(ClientConstants.SESSIONID)) {

			sessionid = dt.getMetaData().get(ClientConstants.SESSIONID);

		}

		System.out.println("----Data received: \n" + receivedData);
		System.out.println("________");

		final String TAG = dt.getName();

		switch (TAG) {

		case NetworkConstants.GET_USER_CREATE:

			String emailCreate = dt.getMetaData().get(ClientConstants.EMAIL);
			String usernameCreate = dt.getMetaData().get(ClientConstants.USERNAME);
			String passwordCreate = dt.getMetaData().get(ClientConstants.PASSWORD);

			if (CredentialsHandler.isValidEmail(emailCreate)) {

				if (CredentialsHandler.isValidUsername(usernameCreate)) {

					if (passwordCreate.length() >= 8) {

						// everything is valid

						// generate unique id
						String uniqueidCreate = 10000000 + new Random().nextInt(89999999) + "";

						// save credentials
						CredentialsHandler.saveCredentials(emailCreate, emailListFile);
						CredentialsHandler.saveCredentials(usernameCreate, usernameListFile);
						CredentialsHandler.saveCredentials(uniqueidCreate, uniqueidListFile);

						// save profile
						DataTree userFile = DataHandler.getFileToTree("/data/users/templates/user.json");
						userFile.getTree("credentials").updateMetaData("email", emailCreate);
						userFile.getTree("credentials").updateMetaData("username", usernameCreate);
						userFile.getTree("credentials").updateMetaData("password", passwordCreate);
						userFile.getTree("credentials").updateMetaData("uniqueid", uniqueidCreate);

						DataHandler.saveJsonFile("res/data/users/profiledata/" + usernameCreate + ".json",
								DataHandler.getTreeToString(userFile));

						// success response
						dataToSend = new DataTree(ServerConstants.USER_CREATE_SUCCESS);
						dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

					} else {

						dataToSend = new DataTree(ServerConstants.USER_CREATE_INVALID_PASSWORD);
						dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

					}

				} else {

					dataToSend = new DataTree(ServerConstants.USER_CREATE_INVALID_USERNAME);
					dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

				}

			} else {

				dataToSend = new DataTree(ServerConstants.USER_CREATE_INVALID_EMAIL);
				dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

			}

			break;

		case NetworkConstants.GET_USER_LOGIN:

			String usernameLogin = dt.getMetaData().get(ClientConstants.USERNAME);
			String passwordLogin = dt.getMetaData().get(ClientConstants.PASSWORD);

			DataTree dtLogin = DataHandler.getFileToTree("/data/users/profiledata/" + usernameLogin + ".json");

			if (dtLogin != null) {

				if (dtLogin.getTree("credentials").getMetaData().get("username").equals(usernameLogin)
						&& dtLogin.getTree("credentials").getMetaData().get("password").equals(passwordLogin)) {

					if (dtLogin.getTree("credentials").getMetaData().get("verified").equals("1")) {

						// login success response
						dataToSend = new DataTree(ServerConstants.USER_LOGIN_SUCCESS);
						dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

					} else {

						System.out.println(dtLogin.getTree("credentials").getMetaData().get("verified"));

						dataToSend = new DataTree(ServerConstants.USER_LOGIN_NOT_VERIFIED);
						dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

					}

				} else {

					dataToSend = new DataTree(ServerConstants.USER_LOGIN_INVALID_USERPASS);
					dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

				}

			} else {

				dataToSend = new DataTree(ServerConstants.USER_LOGIN_INVALID_USERPASS);
				dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

			}

			break;

		case NetworkConstants.GET_USER_VERIFY:

			String emailVerify = dt.getMetaData().get(ClientConstants.EMAIL);
			String usernameVerify = dt.getMetaData().get(ClientConstants.USERNAME);
			String passwordVerify = dt.getMetaData().get(ClientConstants.PASSWORD);
			String uniqueidVerify = dt.getMetaData().get(ClientConstants.UNIQUEID);

			DataTree dtVerify = DataHandler.getFileToTree("/data/users/profiledata/" + usernameVerify + ".json");

			if (dtVerify != null) {

				if (dtVerify.getTree("credentials").getMetaData().get("email").equals(emailVerify)) {

					if (dtVerify.getTree("credentials").getMetaData().get("username").equals(usernameVerify)
							&& dtVerify.getTree("credentials").getMetaData().get("password").equals(passwordVerify)) {

						if (dtVerify.getTree("credentials").getMetaData().get("uniqueid").equals(uniqueidVerify)) {

							// set user verified
							dtVerify.getTree("credentials").updateMetaData("verified", "1");

							// save user verified
							DataHandler.saveJsonFile("res/data/users/profiledata/" + usernameVerify + ".json",
									DataHandler.getTreeToString(dtVerify));

							// verify success response
							dataToSend = new DataTree(ServerConstants.USER_VERIFY_SUCCESS);
							dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

						} else {

							dataToSend = new DataTree(ServerConstants.USER_VERIFY_INVALID_UNIQUEID);
							dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

						}

					} else {

						dataToSend = new DataTree(ServerConstants.USER_VERIFY_INVALID_USERPASS);
						dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

					}

				} else {

					dataToSend = new DataTree(ServerConstants.USER_VERIFY_INVALID_EMAIL);
					dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

				}

			} else {

				dataToSend = new DataTree(ServerConstants.USER_VERIFY_INVALID_USERPASS);
				dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

			}

			break;

		case NetworkConstants.GET_USER_UPDATE:

			dataToSend = new DataTree(ServerConstants.USER_UPDATED);
			dataToSend.updateMetaData(ClientConstants.SESSIONID, sessionid);

			break;

		}

		if (dataToSend != null) {

			isDataReady = true;

		}

	}

	@Override
	protected void updateSend() {

		if (isDataReady) {

			System.out.println("----Sending data: " + dataToSend.getName());

			String data = DataHandler.getTreeToString(dataToSend);

			dataToSend = null;
			isDataReady = false;

			DataTransferer().sendData(data);

			System.out.println("Data sent: \n" + data);
			System.out.println("________");

		}

	}

	public static void main(String[] args) {

		Server server = new Server("239.121.121.121", 12345);
		server.establishConnection();
		server.run();

	}

}
