package jp.keitai2013.practice.keiziban.chikara.manager;

import java.io.Serializable;

public class LoginToken implements Serializable{

	final String token;



	LoginToken(String token) {
		this.token = token;
	}

	public String getToken(){
		return this.token;
	}

}
