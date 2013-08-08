package jp.keitai2013.practice.keiziban.heallin;

import jp.keitai2013.practice.keiziban.chikara.manager.LoginInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class StartActivity extends Activity {


	private static int REQCODE_LOGIN = 1001;
	//private static int REQCODE_SIGN_UP = 1002;


	private Handler mHandler;

	//private boolean mIsFirst = true;


	private static int S_MODE_NONE = 0;
	private static int S_MODE_START = 1;
	private static int S_MODE_LOGIN = 2;
	//private static int S_MODE_LOGIN_SUCCESS = 3;



	int mMode = S_MODE_NONE;


	private final Runnable mStartRunnuable = new Runnable() {
		@Override
		public void run() {
			Intent intent  = new Intent(StartActivity.this,LoginActivity.class);
			mMode = S_MODE_LOGIN;
			startActivityForResult(intent, REQCODE_LOGIN);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		mHandler = new Handler();
		//mIsFirst = true;
		mMode = S_MODE_START;
	}



	@Override
	protected void onStart() {
		super.onStart();

		if(mMode==S_MODE_NONE){
			finish();
		}
	}

	@Override
	protected void onRestart() {
		// TODO 自動生成されたメソッド・スタブ
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		if(mMode==S_MODE_START){
			mHandler.postDelayed(mStartRunnuable, 1000);
		}
	}

	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
		mHandler.removeCallbacks(mStartRunnuable);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}



















	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==REQCODE_LOGIN){
			if(resultCode==RESULT_OK){
				LoginInfo lf = (LoginInfo) data.getSerializableExtra(Const.AK_LOGIN_INFO);

				Intent intent = new Intent(StartActivity.this, BoardActivity.class);
				intent.putExtra(Const.AK_LOGIN_INFO, lf);

				mMode = S_MODE_NONE;
				startActivity(intent);
			}else{
				finish();
			}
		}

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

}
