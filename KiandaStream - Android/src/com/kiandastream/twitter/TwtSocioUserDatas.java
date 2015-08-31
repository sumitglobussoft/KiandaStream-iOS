package com.kiandastream.twitter;

public class TwtSocioUserDatas {

	String userid;
	String username;
	String userAcessToken;
	String usersecretKey;
  
 
	public String getUsersecretKey() {
		return usersecretKey;
	}

	public void setUsersecretKey(String usersecretKey) {
		this.usersecretKey = usersecretKey;
	}

	public TwtSocioUserDatas(String userid, String username,
			String userAcessToken, String usersecretKey, String userimage,
			String emailId) {
		super();
		this.userid = userid;
		this.username = username;
		this.userAcessToken = userAcessToken;
		this.usersecretKey = usersecretKey;
 	}

	@Override
	public String toString() {
		return "ModelUserDatas [userid=" + userid + ", username=" + username
				+ ", userAcessToken=" + userAcessToken + ", usersecretKey="
				+ usersecretKey + ", userimage="   ;
	}

	 
	public String getUserAcessToken() {
		return userAcessToken;
	}

	public void setUserAcessToken(String userAcessToken) {
		this.userAcessToken = userAcessToken;
	}

	public TwtSocioUserDatas() {
	}

	/**
	 * @return the userid
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * @param userid
	 *            the userid to set
	 */
	public void setUserid(String userid) {
		this.userid = userid;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the level
	 */

}
