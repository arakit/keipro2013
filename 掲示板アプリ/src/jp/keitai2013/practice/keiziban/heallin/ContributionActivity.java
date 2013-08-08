package jp.keitai2013.practice.keiziban.heallin;

import jp.keitai2013.practice.keiziban.chikara.manager.LoginInfo;
import jp.keitai2013.practice.keiziban.chikara.manager.LoginManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContributionActivity extends Activity {

	Button mCancelView;
	Button mSubmitView;
	EditText mBodyView;


	LoginInfo mLoginInfo;
	LoginManager mLoginManager;

	SubmitContributeTask mTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contribution);

		Intent intent = getIntent();
		mLoginInfo = (LoginInfo) intent.getSerializableExtra(Const.AK_LOGIN_INFO);

		mLoginManager = new LoginManager(getApplicationContext());

		mCancelView = (Button) findViewById(R.id.cancel);
		mSubmitView = (Button) findViewById(R.id.submit);
		mBodyView = (EditText) findViewById(R.id.body);

		mCancelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelContoribute();

			}
		});

		mSubmitView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitContribute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contribution, menu);
		return true;
	}


	private void submitContribute(){

		if(mTask!=null) return ;

		String body = mBodyView.getText().toString();

		if(TextUtils.isEmpty(body)){
			mBodyView.setError("入力してください。");
			return ;
		}

		mTask = new SubmitContributeTask();
		mTask.execute(body);
	}

	private void cancelContoribute(){
		setResult(RESULT_CANCELED);
		finish();
	}

	public class SubmitContributeTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... params) {


			//Toast.makeText(LoginActivity.this, "バックグラウンド処理中です！", Toast.LENGTH_SHORT).show();

			//通信処理

			//LoginInfo lf = mLoginManager.login(mId, mPassword);
			int id = mLoginManager.submitContribute(mLoginInfo, params[0] );

			return id;
		}

		@Override
		protected void onPostExecute(final Integer id) {
			mTask = null;
//			mAuthTask = null;
//			showProgress(false);


//			if (lf!=null) {
//
//				Intent data = new Intent();
//				data.putExtra(Const.AK_LOGIN_INFO, lf);
//				setResult(RESULT_OK, data);
//				finish();
//			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}

			if(id!=-1){
				toast("投稿しました。");

				Intent data = new Intent();
				data.putExtra(Const.AK_SUBMIT_CONTRIBUTE_ID, id);
				data.putExtra(Const.AK_LOGIN_INFO, mLoginInfo);

				setResult(RESULT_OK, data);
				finish();

			}else{
				toast("投稿に失敗しました。");
			}
		}

		@Override
		protected void onCancelled() {
			mTask = null;
			//mAuthTask = null;
			//showProgress(false);
		}

	}






	private Toast mToast;
	private void toast(String str){
		if(mToast==null){
			mToast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
		}else{
			mToast.setText(str);
		}
		mToast.show();
	}

}
