package com.google.appengine.mct;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gdata.client.appsforyourdomain.AppsForYourDomainQuery;
import com.google.gdata.client.appsforyourdomain.AppsGroupsService;
import com.google.gdata.client.appsforyourdomain.EmailListRecipientService;
import com.google.gdata.client.appsforyourdomain.EmailListService;
import com.google.gdata.client.appsforyourdomain.NicknameService;
import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.Link;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.Login;
import com.google.gdata.data.appsforyourdomain.Name;
import com.google.gdata.data.appsforyourdomain.Nickname;
import com.google.gdata.data.appsforyourdomain.Quota;
import com.google.gdata.data.appsforyourdomain.provisioning.NicknameEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.NicknameFeed;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.data.appsforyourdomain.provisioning.UserFeed;
import com.google.gdata.util.ServiceException;

// TODO: Auto-generated Javadoc
/**
 * The Class AppsForYourDomainClient.
 */
public class AppsForYourDomainClient {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(
			AppsForYourDomainClient.class.getName());

	/** The Constant APPS_FEEDS_URL_BASE. */
	private static final String APPS_FEEDS_URL_BASE =
		"https://apps-apis.google.com/a/feeds/";

	/** The Constant SERVICE_VERSION. */
	protected static final String SERVICE_VERSION = "2.0";

	/** The domain url base. */
	protected String domainUrlBase;

	/** The email list recipient service. */
	protected EmailListRecipientService emailListRecipientService;

	/** The email list service. */
	protected EmailListService emailListService;

	/** The nickname service. */
	protected NicknameService nicknameService;

	/** The user service. */
	protected UserService userService;

	/** The group service. */
	protected AppsGroupsService groupService;

	/**
	 * Gets the group service.
	 *
	 * @return the group service
	 */
	public AppsGroupsService getGroupService() {
		return groupService;
	}

	/** The domain. */
	protected final String domain;

	/**
	 * Instantiates a new apps for your domain client.
	 *
	 * @param domain the domain
	 */
	protected AppsForYourDomainClient(String domain) {
		this.domain = domain;
		this.domainUrlBase = APPS_FEEDS_URL_BASE + domain + "/";
	}

	/**
	 * Instantiates a new apps for your domain client.
	 *
	 * @param adminEmail the admin email
	 * @param adminPassword the admin password
	 * @param domain the domain
	 * @throws Exception the exception
	 */
	public AppsForYourDomainClient(String adminEmail, String adminPassword,
			String domain) throws Exception {
		this(domain);

		// Configure all of the different Provisioning services
		userService = new UserService(
		"gdata-sample-AppsForYourDomain-UserService");
		userService.setUserCredentials(adminEmail, adminPassword);

		nicknameService = new NicknameService(
				"gdata-sample-AppsForYourDomain-NicknameService");
		nicknameService.setUserCredentials(adminEmail, adminPassword);

		emailListService = new EmailListService(
				"gdata-sample-AppsForYourDomain-EmailListService");
		emailListService.setUserCredentials(adminEmail, adminPassword);

		emailListRecipientService = new EmailListRecipientService(
				"gdata-sample-AppsForYourDomain-EmailListRecipientService");
		emailListRecipientService.setUserCredentials(adminEmail, adminPassword);

		groupService = new AppsGroupsService(adminEmail, adminPassword, domain, 
				"gdata-sample-AppsForYourDomain-AppsGroupService");
	}


	/**
	 * Creates the user.
	 *
	 * @param username the username
	 * @param givenName the given name
	 * @param familyName the family name
	 * @param password the password
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry createUser(String username, String givenName,
			String familyName, String password) throws AppsForYourDomainException, 
			ServiceException, IOException {

		return createUser(username, givenName, familyName, password, null, null);
	}

	/**
	 * Creates the user.
	 *
	 * @param username the username
	 * @param givenName the given name
	 * @param familyName the family name
	 * @param password the password
	 * @param quotaLimitInMb the quota limit in mb
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry createUser(String username, String givenName,
			String familyName, String password, Integer quotaLimitInMb)
	throws AppsForYourDomainException, ServiceException, IOException {

		return createUser(username, givenName, familyName, password, null, 
				quotaLimitInMb);
	}

	/**
	 * Creates the user.
	 *
	 * @param username the username
	 * @param givenName the given name
	 * @param familyName the family name
	 * @param password the password
	 * @param passwordHashFunction the password hash function
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry createUser(String username, String givenName,
			String familyName, String password, String passwordHashFunction)
	throws AppsForYourDomainException, ServiceException, IOException {

		return createUser(username, givenName, familyName, password,
				passwordHashFunction, null);
	}

	/**
	 * Creates the user.
	 *
	 * @param username the username
	 * @param givenName the given name
	 * @param familyName the family name
	 * @param password the password
	 * @param passwordHashFunction the password hash function
	 * @param quotaLimitInMb the quota limit in mb
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry createUser(String username, String givenName,
			String familyName, String password, String passwordHashFunction,
			Integer quotaLimitInMb)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO,
				"Creating user '" + username + "'. Given Name: '" + givenName +
				"' Family Name: '" + familyName +
				(passwordHashFunction != null 
						? "' Hash Function: '" + passwordHashFunction : "") + 
						(quotaLimitInMb != null 
								? "' Quota Limit: '" + quotaLimitInMb + "'." : "'.")
		);

		UserEntry entry = new UserEntry();
		Login login = new Login();
		login.setUserName(username);
		login.setPassword(password);
		if (passwordHashFunction != null) {
			login.setHashFunctionName(passwordHashFunction);
		}
		entry.addExtension(login);

		Name name = new Name();
		name.setGivenName(givenName);
		name.setFamilyName(familyName);
		entry.addExtension(name);

		if (quotaLimitInMb != null) {
			Quota quota = new Quota();
			quota.setLimit(quotaLimitInMb);
			entry.addExtension(quota);
		}

		URL insertUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION );
		return userService.insert(insertUrl, entry);
	}

	/**
	 * Retrieve user.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry retrieveUser(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO,
				"Retrieving user '" + username + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.getEntry(retrieveUrl, UserEntry.class);
	}

	/**
	 * Retrieve all users.
	 *
	 * @return the user feed
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserFeed retrieveAllUsers()
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO,
		"Retrieving all users.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/");
		UserFeed allUsers = new UserFeed();
		UserFeed currentPage;
		Link nextLink;

		do {
			currentPage = userService.getFeed(retrieveUrl, UserFeed.class);
			allUsers.getEntries().addAll(currentPage.getEntries());
			nextLink = currentPage.getLink(Link.Rel.NEXT, Link.Type.ATOM);
			if (nextLink != null) {
				retrieveUrl = new URL(nextLink.getHref());
			}
		} while (nextLink != null);

		return allUsers;
	}

	/**
	 * Retrieve page of users.
	 *
	 * @param startUsername the start username
	 * @return the user feed
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserFeed retrievePageOfUsers(String startUsername)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Retrieving one page of users"
				+ (startUsername != null ? " starting at " + startUsername : "") + ".");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/");
		AppsForYourDomainQuery query = new AppsForYourDomainQuery(retrieveUrl);
		query.setStartUsername(startUsername);
		return userService.query(query, UserFeed.class);
	}

	/**
	 * Update user.
	 *
	 * @param username the username
	 * @param userEntry the user entry
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry updateUser(String username, UserEntry userEntry)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Updating user '" + username + "'.");

		URL updateUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Delete user.
	 *
	 * @param username the username
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void deleteUser(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Deleting user '" + username + "'.");

		URL deleteUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		userService.delete(deleteUrl);
	}

	/**
	 * Suspend user.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry suspendUser(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Suspending user '" + username + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		UserEntry userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
		userEntry.getLogin().setSuspended(true);

		URL updateUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Restore user.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry restoreUser(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Restoring user '" + username + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		UserEntry userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
		userEntry.getLogin().setSuspended(false);

		URL updateUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Adds the admin privilege.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry addAdminPrivilege(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Setting admin privileges for user '" + username + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		UserEntry userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
		userEntry.getLogin().setAdmin(true);

		URL updateUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Removes the admin privilege.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry removeAdminPrivilege(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Removing admin privileges for user '" + username + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		UserEntry userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
		userEntry.getLogin().setAdmin(false);

		URL updateUrl = new URL(domainUrlBase + "user/" + SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Force user to change password.
	 *
	 * @param username the username
	 * @return the user entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public UserEntry forceUserToChangePassword(String username)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Requiring " + username + " to change password at " +
		"next login.");

		URL retrieveUrl = new URL(domainUrlBase + "user/"
				+ SERVICE_VERSION + "/" + username);
		UserEntry userEntry = userService.getEntry(retrieveUrl, UserEntry.class);
		userEntry.getLogin().setChangePasswordAtNextLogin(true);

		URL updateUrl = new URL(domainUrlBase + "user/"
				+ SERVICE_VERSION + "/" + username);
		return userService.update(updateUrl, userEntry);
	}

	/**
	 * Creates the nickname.
	 *
	 * @param username the username
	 * @param nickname the nickname
	 * @return the nickname entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public NicknameEntry createNickname(String username, String nickname) 
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO,
				"Creating nickname '" + nickname +
				"' for user '" + username + "'.");

		NicknameEntry entry = new NicknameEntry();
		Nickname nicknameExtension = new Nickname();
		nicknameExtension.setName(nickname);
		entry.addExtension(nicknameExtension);

		Login login = new Login();
		login.setUserName(username);
		entry.addExtension(login);

		URL insertUrl = new URL(domainUrlBase + "nickname/" + SERVICE_VERSION);
		return nicknameService.insert(insertUrl, entry);
	}

	/**
	 * Retrieve nickname.
	 *
	 * @param nickname the nickname
	 * @return the nickname entry
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public NicknameEntry retrieveNickname(String nickname) throws AppsForYourDomainException,
	ServiceException, IOException {
		LOGGER.log(Level.INFO, "Retrieving nickname '" + nickname + "'.");

		URL retrieveUrl = new URL(domainUrlBase + "nickname/" + SERVICE_VERSION + "/" + nickname);
		return nicknameService.getEntry(retrieveUrl, NicknameEntry.class);
	}

	/**
	 * Retrieve nicknames.
	 *
	 * @param username the username
	 * @return the nickname feed
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public NicknameFeed retrieveNicknames(String username)
	throws AppsForYourDomainException, ServiceException, IOException {
		LOGGER.log(Level.INFO,
				"Retrieving nicknames for user '" + username + "'.");

		URL feedUrl = new URL(domainUrlBase + "nickname/" + SERVICE_VERSION);
		AppsForYourDomainQuery query = new AppsForYourDomainQuery(feedUrl);
		query.setUsername(username);
		return nicknameService.query(query, NicknameFeed.class);
	}

	/**
	 * Retrieve page of nicknames.
	 *
	 * @param startNickname the start nickname
	 * @return the nickname feed
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public NicknameFeed retrievePageOfNicknames(String startNickname)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Retrieving one page of nicknames"
				+ (startNickname != null ? " starting at " + startNickname : "") + ".");

		URL retrieveUrl = new URL(
				domainUrlBase + "nickname/" + SERVICE_VERSION + "/");
		AppsForYourDomainQuery query = new AppsForYourDomainQuery(retrieveUrl);
		query.setStartNickname(startNickname);
		return nicknameService.query(query, NicknameFeed.class);
	}

	/**
	 * Retrieve all nicknames.
	 *
	 * @return the nickname feed
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public NicknameFeed retrieveAllNicknames()
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO,
		"Retrieving all nicknames.");

		URL retrieveUrl = new URL(domainUrlBase + "nickname/"
				+ SERVICE_VERSION + "/");
		NicknameFeed allNicknames = new NicknameFeed();
		NicknameFeed currentPage;
		Link nextLink;

		do {
			currentPage = nicknameService.getFeed(retrieveUrl, NicknameFeed.class);
			allNicknames.getEntries().addAll(currentPage.getEntries());
			nextLink = currentPage.getLink(Link.Rel.NEXT, Link.Type.ATOM);
			if (nextLink != null) {
				retrieveUrl = new URL(nextLink.getHref());
			}
		} while (nextLink != null);

		return allNicknames;
	}

	/**
	 * Delete nickname.
	 *
	 * @param nickname the nickname
	 * @throws AppsForYourDomainException the apps for your domain exception
	 * @throws ServiceException the service exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void deleteNickname(String nickname)
	throws AppsForYourDomainException, ServiceException, IOException {

		LOGGER.log(Level.INFO, "Deleting nickname '" + nickname + "'.");

		URL deleteUrl = new URL(domainUrlBase + "nickname/" + SERVICE_VERSION + "/" + nickname);
		nicknameService.delete(deleteUrl);
	}




}