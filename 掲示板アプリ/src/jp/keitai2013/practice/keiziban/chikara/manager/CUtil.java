package jp.keitai2013.practice.keiziban.chikara.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

public class CUtil {


	static boolean LOCAL_DEBUG = false;


	//private static String LOGIN_URL = "http://****/login.php";

	//http://172.21.31.199/bbs/login.php
	//public static final String SERVER = "http://172.21.31.199/";
	public static final String SERVER = "http://180.0.56.207:15060/";
	//public static final String SERVER = "http://192.168.1.5/";
	//public static final int PORT = 15060;


	public static final String LOGIN = "bbs/login_and.php";
	public static final String SIGN_UP = "bbs/register_and.php";
	public static final String BOARD = "bbs/index_and.php";
	public static final String CONTRIBUTE = "bbs/submit.php";
	public static final String DEL_CONTRIBUTE = "bbs/delete_and.php";




//	 public static boolean containsZenkaku ( String str ){
//
//		 Pattern ptn = Pattern.compile("[^-~｡-ﾟ]");
//		 Matcher mat = ptn.matcher(str);
//		 if(mat.matches()) return true;
//		 return false;
//
//		 // Regular expression.
//		 //return str.matches("[^-~｡-ﾟ]");
//	 }


	 public static boolean isEisuuzi( String str ){
		// ^[0-9a-zA-Z]*$
		return str.matches("^[0-9a-zA-Z]*$") ;
	 }

//	 public static boolean isEisuuziAnd( String str ){
//		// ^[0-9a-zA-Z]*$
//		return str.matches("^[0-9a-zA-Z^-~｡-ﾟ]*$") ;
//	 }




	public static String getRawText(Context context,int id){
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new  BufferedReader( new InputStreamReader( context.getResources().openRawResource(id) ));

		try {
			String line;
			while((line =r.readLine())!=null){
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	public static JSONObject getRawTextJson(Context context,int id){
		String data = getRawText(context, id);
		if(TextUtils.isEmpty(data)) return null;
		try {
			JSONObject json = new JSONObject(data);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static JSONObject postDataReturnJson(String sUrl, ArrayList<NameValuePair> params){
		String data = postData(sUrl, params);
		if(TextUtils.isEmpty(data)) return null;
		try {
			JSONObject json = new JSONObject(data);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String postData(String sUrl, ArrayList<NameValuePair> params) {

		HttpClient objHttp = new DefaultHttpClient();
		String sReturn = "";

		try {
		
			HttpPost httpPost = new HttpPost(sUrl);
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			if(true){
				HttpResponse objResponse = objHttp.execute(httpPost);

				if (objResponse.getStatusLine().getStatusCode() < 400){
					InputStream objStream = objResponse.getEntity().getContent();
					InputStreamReader objReader = new InputStreamReader(objStream);
					BufferedReader objBuf = new BufferedReader(objReader);
					StringBuilder objJson = new StringBuilder();
					String sLine;
					while((sLine = objBuf.readLine()) != null){
						objJson.append(sLine);
					}
					sReturn = objJson.toString();
					objStream.close();
				}
			}else{
				objHttp.execute(httpPost);
				return null;
			}
		} catch (IOException e) {
			return null;
		}
			return sReturn;
		}

		public static String getData(String sUrl) {
			HttpClient objHttp = new DefaultHttpClient();
			HttpParams params = objHttp.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 1000); //接続のタイムアウト
			HttpConnectionParams.setSoTimeout(params, 1000); //データ取得のタイムアウト
			String sReturn = "";
		try {

			HttpGet objGet = new HttpGet(sUrl);
			HttpResponse objResponse = objHttp.execute(objGet);
			if (objResponse.getStatusLine().getStatusCode() < 400){
				InputStream objStream = objResponse.getEntity().getContent();

				InputStreamReader objReader = new InputStreamReader(objStream);
				BufferedReader objBuf = new BufferedReader(objReader);
				StringBuilder objJson = new StringBuilder();
				String sLine;

				while((sLine = objBuf.readLine()) != null){
					objJson.append(sLine);
				}
				sReturn = objJson.toString();
				objStream.close();
			}
		} catch (IOException e) {
			return null;
		}
		return sReturn;
	}

}
