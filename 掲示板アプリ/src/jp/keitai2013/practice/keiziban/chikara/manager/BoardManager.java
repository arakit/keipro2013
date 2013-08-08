package jp.keitai2013.practice.keiziban.chikara.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import jp.keitai2013.practice.keiziban.heallin.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

public class BoardManager {


	//static boolean LOCAL_DEBUG = true;


	/*		Auth: Chikara Funabashi
	 * 		Date: 2013/07/06
	 *
	 */


//	public static int LOGIN_RESULT_OK = 0;
//	public static int LOGIN_RESULT_FAILED = -1;





	private static String BOARD_URL = CUtil.SERVER + CUtil.BOARD;

	private Context mContext;

	//private ArrayList<ConItem>mList = new ArrayList<ConItem>();

	private ArrayList<BItem> mList = new ArrayList<BoardManager.BItem>();


	public static class BItem implements Serializable{
		public int id;
		public long time;
		public String user_id;
		public String user_name;
		public String body;
	}




	public BoardManager(Context context){
		mContext = context;
	}


	public synchronized boolean getByRange(LoginInfo l,long start_time, long end_time){

		if(!l.isLoggedIn()) return false;

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("token", l.token.token));
//		params.add(new BasicNameValuePair("range_start", ""+start_time));
//		params.add(new BasicNameValuePair("range_end", ""+end_time));

		log("board 001");
		
		String data = null;
		
		//JSONObject json;
		if(CUtil.LOCAL_DEBUG){
			//json = CUtil.getRawTextJson(mContext, R.raw.board_resp_sample);
		}else{
			data = CUtil.postData(BOARD_URL, params);
			//json = CUtil.postDataReturnJson(BOARD_URL, params);
		}
		if(data==null) return false;
		
		log(data);

		mList.clear();
		
		log("board 002");

		try {
			
			log("board 003");
			
			JSONObject json = new JSONObject(data);
			
			String result = json.getString("result");
			
			log("board 004");

			if(!result.equalsIgnoreCase(LoginManager.BOARD_OK)){
				return false;
			}
			
			log("board 005");
			
			if(result.equalsIgnoreCase(LoginManager.BOARD_ERR_OVER_TIME_TOKEN)){
				return false;
			}
			
			log("board 006");

			JSONArray jdata = json.getJSONArray("data");
			
			log("board 007");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			int  num = jdata.length();
			for(int i=0;i<num;i++){
				JSONObject jc = jdata.getJSONObject(i);
				BItem item = new BItem();
				item.id = jc.getInt("post_id");
				item.user_name = jc.getString("name");
				item.user_id = jc.getString("id");
				item.time =  sdf.parse( jc.getString("post_time") ).getTime();
				item.body = jc.getString("body");
				mList.add(item);
			}
			
			log("board 008");

			return true;
		} catch (JSONException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		} catch (ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return false;
		}



	}


	public synchronized boolean deleteContoribute(LoginManager lf,LoginInfo li, int id){

		if( lf.deleteContribute(li, id) ){
			removeItemById(id);
			return true;
		}

		return false;
	}



	public synchronized BItem removeItemById(int id){
		BItem bitem = getItemById(id);
		if(bitem==null) return null;
		if( mList.remove(bitem) ) return bitem;
		return null;
	}





	public synchronized BItem getItemById(int id){
		for(int i=0;i<mList.size();i++){
			BItem item = mList.get(i);
			if( item.id == id ) return item;
		}
		return null;
	}

	public synchronized BItem getItemByIndex(int index){
		return mList.get(index);
	}
	public synchronized int getItemLength(){
		return mList.size();
	}







	public static void log(String str){
		android.util.Log.d("test", str);
	}




}
