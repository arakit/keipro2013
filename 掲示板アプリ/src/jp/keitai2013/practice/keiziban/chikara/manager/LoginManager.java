package jp.keitai2013.practice.keiziban.chikara.manager;


import java.util.ArrayList;
import jp.keitai2013.practice.keiziban.heallin.R;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

public class LoginManager {





	/*		Auth: Chikara Funabashi
	 * 		Date: 2013/07/06
	 *
	 */


	private static String LOGIN_URL = CUtil.SERVER + CUtil.LOGIN;
	private static String SIGN_UP_URL = CUtil.SERVER + CUtil.SIGN_UP;
	private static String CONTRIBUTE_URL = CUtil.SERVER + CUtil.CONTRIBUTE;
	private static String DEL_CONTRIBUTE_URL = CUtil.SERVER + CUtil.DEL_CONTRIBUTE;


	private Context mContext;

	public LoginManager(Context context){
		mContext = context;
	}


	public static final String DEL_OK = "OK";

	public static final String BOARD_OK = "OK";
	public static final String BOARD_ERR_OVER_TIME_TOKEN = "E04";

	public static final String CONTOBUTE_OK = "OK";

	public static final String SIGN_UP_OK = "OK";
	public static final String SIGN_UP_ERR_CONNECT = "Err_Connect";

	public static final String LOGIN_OK = "OK";
	public static final String LOGIN_ERR_CONNECT = "Err_Connect";
	public static final String LOGIN_ERR_DATA = "Err_Data";
	public static final String LOGIN_ERR_DB = "E01";
	public static final String LOGIN_ERR_SELECT = "E02";
	public static final String LOGIN_ERR_NOT_REG_ID = "E03";
	public static final String LOGIN_ERR_NOT_MATCH_PASS = "E04";


	//E01 データベースの接続不良
	//E02 select失敗
	//E03 ID未登録
	//E04 : パスワード不一位

	//ログインをします。
	public LoginInfo login(String id, String password,String[] outResult){

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("pass", password));

		log("00001");

		String data = null;
		if(CUtil.LOCAL_DEBUG){
			//data = CUtil.getRawText(mContext, R.raw.login_resp_sample);

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				if(outResult!=null) outResult[0] = LOGIN_ERR_CONNECT;
				return null;
			}

		}else{
			data = CUtil.postData(LOGIN_URL, params);
		}

		if(data==null){
			if(outResult!=null) outResult[0] = LOGIN_ERR_CONNECT;
			return null;
		}

		log("00002");

		try {
			JSONObject json = new JSONObject(data);

			String result = json.getString("result");
			String token = json.getString("token");
			String name = json.getString("name");

			log("00004");

			if(!result.equalsIgnoreCase(LOGIN_OK)){
				if(outResult!=null) outResult[0] = result;
				return null;//LOGIN_RESULT_FAILED;
			}
			if(TextUtils.isEmpty(token)){
				if(outResult!=null) outResult[0] = LOGIN_ERR_DATA;
				return null;//LOGIN_RESULT_FAILED;
			}
			if(TextUtils.isEmpty(name)){
				if(outResult!=null) outResult[0] = LOGIN_ERR_DATA;
				return null;//LOGIN_RESULT_FAILED;
			}

			log("00005");

			LoginInfo lf = new LoginInfo();
			lf.token = new LoginToken(token);
			lf.id = id;
			lf.password = password;
			lf.name = name;

			return lf;

		} catch (JSONException e) {
			e.printStackTrace();
			log("00003");
			if(outResult!=null) outResult[0] = LOGIN_ERR_DATA;
			return null;
		}
		 catch (Exception e) {
			e.printStackTrace();
			if(outResult!=null) outResult[0] = LOGIN_ERR_DATA;
			log("00003-b");
			return null;
		}

	}

	//ログインをし直します。
	public boolean retryLogin(LoginInfo lf,String[] outResult){

		LoginInfo new_lf = login(lf.id, lf.password,outResult);

		if(new_lf==null) return false;

		lf.id = new_lf.id;
		lf.password = new_lf.password;
		lf.name = new_lf.name;
		lf.token = new_lf.token;

		return true;
	}

	//アカウントを作成します
	public boolean createUser(String id, String password,String name, String[] outResult){

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("pass", password));
		params.add(new BasicNameValuePair("repass", password));
		params.add(new BasicNameValuePair("name", name));

		String data = null;
		if(CUtil.LOCAL_DEBUG){
			//data = CUtil.getRawText(mContext, R.raw.sign_up_resp_sample);
			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				if(outResult!=null) outResult[0] = SIGN_UP_ERR_CONNECT;
				return false;
			}
		}else{
			data = CUtil.postData(SIGN_UP_URL, params);
		}

		if(data==null) {
			if(outResult!=null) outResult[0] = SIGN_UP_ERR_CONNECT;
			return false;
		}

		try {
			JSONObject json = new JSONObject(data);

			String result = json.getString("result");
//			String id = json.getString("id");
//			String name = json.getString("name");
//			String pass = json.getString("pass");

//			boolean result = json.getBoolean("result");

			if(!result.equalsIgnoreCase(SIGN_UP_OK)){
				if(outResult!=null) outResult[0] = result;
				return false;
			}

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			if(outResult!=null) outResult[0] = SIGN_UP_ERR_CONNECT;
			return false;
		}


	}




	//投稿をします
	public int submitContribute(LoginInfo lf, String body){

		if(TextUtils.isEmpty(body)){
			return -1;
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("token", lf.token.token));
		params.add(new BasicNameValuePair("body", body));

		String data = null;
		if(CUtil.LOCAL_DEBUG){
			//data = CUtil.getRawText(mContext, R.raw.contribute_resp_sample);

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return -1;
			}

		}else{
			data = CUtil.postData(CONTRIBUTE_URL, params);
		}


		try {
			JSONObject json = new JSONObject(data);

			String result = json.getString("result");
			int res_post_id = json.getInt("post_id");
//			int res_acc_id = json.getInt("acc_id");
//			long res_post_time = json.getLong("post_time");
//			int res_body = json.getInt("body");
			String res_token = json.getString("token");

			if(!result.equalsIgnoreCase(CONTOBUTE_OK)){
				return -1;
			}

			if(!TextUtils.isEmpty(res_token)){
				lf.token = new LoginToken( res_token );
				log("tokenがNuLLではないです。");
			}else{
				log("tokenがNuLLです。");
			}

			return res_post_id;

		} catch (JSONException e) {
			e.printStackTrace();
			return -1;
		}
	}

	//投稿を削除します
	public boolean deleteContribute(LoginInfo lf, int id){

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("token", lf.token.token));
		params.add(new BasicNameValuePair("post", ""+id));

		String data = null;
		if(CUtil.LOCAL_DEBUG){
			//data = CUtil.getRawText(mContext, R.raw.del_cont_resp_sample);

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

		}else{
			data = CUtil.postData(DEL_CONTRIBUTE_URL, params);
		}


		try {
			JSONObject json = new JSONObject(data);

			String result = json.getString("result");

			if(!result.equalsIgnoreCase(DEL_OK)){
				return false;
			}

			return true;

		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}







	public static void log(String str){
		android.util.Log.d("test", str);
	}





}
