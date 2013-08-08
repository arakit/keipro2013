package jp.keitai2013.practice.keiziban.chikara.manager;

import java.io.Serializable;

public class LoginInfo implements Serializable{

	LoginToken token;

	String id;
	String name;
	String password;


	LoginInfo() {
		//this.token = token;
	}


	public LoginToken getToken(){
		return this.token;
	}

	public boolean isLoggedIn(){
		return token!= null;
	}

	public boolean isMyId(String id){
		if(id==null) return false;
		if(this.id==null) return false;
		return this.id.equals(id);
	}


	
	

	public String getId(){
		return id;
	}
	public String getPassword(){
		return password;
	}
	public String getName(){
		return name;
	}
//	public String getLoginToken(){
//		return mLoginToken;
//	}
//	public boolean isLogin(){
//		return mLoginToken!=null;
//	}

}
