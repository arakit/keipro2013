package jp.keitai2013.practice.keiziban.heallin;


import java.text.DateFormat;
import java.util.Date;


import jp.keitai2013.practice.keiziban.chikara.manager.BoardManager;
import jp.keitai2013.practice.keiziban.chikara.manager.LoginInfo;
import jp.keitai2013.practice.keiziban.chikara.manager.LoginManager;
import jp.keitai2013.practice.keiziban.chikara.manager.BoardManager.BItem;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BoardActivity extends Activity {


	public static int REQCODE_CONTRIBUTE_SUBMIT = 1003;


	private ListView mListView;

	private LoginManager mLoginManager;
	private BoardManager mBoradManager;

	private LayoutInflater mLayoutInflater;
	private DateFormat mDateFormat;


	private GetBoadTask mGetBoradTask;

	private LoginInfo mLoginInfo;


	private DeleteContributeTask mDelTask;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_board);

		mListView  = (ListView) findViewById(R.id.listView_board);

		mLayoutInflater = getLayoutInflater();
		mDateFormat = DateFormat.getInstance();

		mLoginManager = new LoginManager(getApplicationContext());
		mBoradManager = new BoardManager(getApplicationContext());

		Intent intent = getIntent();
		mLoginInfo = (LoginInfo) intent.getSerializableExtra(Const.AK_LOGIN_INFO);


		mListView.setAdapter(mAdapter);

		if(mLoginInfo!=null){
			attemptGetBorad();
		}else{
			toast("ログインに失敗しています。");
			finish();
		}



	}

	private void attemptGetBorad(){
		if(mGetBoradTask!=null){
			return ;
		}

		mGetBoradTask = new GetBoadTask();
		mGetBoradTask.execute((Void)null);

	}

	private void updateListView(){


		mAdapter.notifyDataSetChanged();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.board, menu);
		return true;
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		//Intent intent;

		int id = item.getItemId();
		switch(id){
		case R.id.action_write:

			//showContiributeDlg();
			showContiributeActivity();

			return true;
		case R.id.action_update:

			attemptGetBorad();

			return true;
		}

		return false;
	}


	@Override
	public void onBackPressed() {
		// TODO 自動生成されたメソッド・スタブ
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
		if(mGetBoradTask!=null){
			mGetBoradTask.cancel(true);
		}
	}
	
	

	@Override
	protected void onPause() {
		// TODO 自動生成されたメソッド・スタブ
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO 自動生成されたメソッド・スタブ
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mGetBoradTask!=null){
			mGetBoradTask.cancel(true);
		}
	}


	/**
	 * doBack, Progress, postExecute
	 */
	private class GetBoadTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			boolean result = mBoradManager.getByRange(mLoginInfo, 0, Long.MAX_VALUE);

			return result;
		
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mGetBoradTask = null;
			//showProgress(false);

			if(success){
				toast("投稿を取得しました。");
				updateListView();
			}else{
				toast("投稿を取得出来ませんでした。");
			}

		}

		@Override
		protected void onCancelled() {
			mGetBoradTask = null;
			//showProgress(false);
		}
	}




	private final BaseAdapter mAdapter = new BaseAdapter() {

		public View getView(int position, View convertView, ViewGroup parent) {
			View line = convertView;
			if(line==null){
				line =  mLayoutInflater.inflate(R.layout.board_list_item, null);
			}

			BItem bitem = mBoradManager.getItemByIndex(position);

			//ListData data = mListData.get(position);

			TextView text_user = (TextView) line.findViewById(R.id.text_user);
			TextView text_time = (TextView) line.findViewById(R.id.text_time);
			TextView text_body = (TextView) line.findViewById(R.id.text_body);
			Button btn_del = (Button) line.findViewById(R.id.btn_del);


			String str_titme = mDateFormat.format(new Date(bitem.time));

			text_user.setText(bitem.user_name+"/"+bitem.user_id);
			text_time.setText(str_titme);
			text_body.setText(""+bitem.body);
			
			if(mLoginInfo.isMyId(bitem.user_id)){
				btn_del.setVisibility(View.VISIBLE);
				btn_del.setOnClickListener(new OnDelBtnClickListener(bitem.id));
			}else{
				btn_del.setVisibility(View.INVISIBLE);
				btn_del.setOnClickListener(null);
			}



			return line;
		}

		public long getItemId(int position) {
			return position;
		}

		public Object getItem(int position) {
			return mBoradManager.getItemByIndex(position);
		}

		public int getCount() {
			return mBoradManager.getItemLength();
		}
	};



	private void showContiributeActivity(){

		Intent intent  = new Intent(this, ContributionActivity.class);
		intent.putExtra(Const.AK_LOGIN_INFO, mLoginInfo);
		startActivityForResult(intent, REQCODE_CONTRIBUTE_SUBMIT);


	}


	private class OnDelBtnClickListener implements View.OnClickListener{

		int mmId;

		public OnDelBtnClickListener(int id) {
			mmId = id;
		}

		@Override
		public void onClick(View v) {

			if( mDelTask!=null ) return ;

			final BItem bitem = mBoradManager.getItemById(mmId);

			AlertDialog.Builder ab = new AlertDialog.Builder(BoardActivity.this);
			ab.setTitle("確認");
			ab.setMessage("削除しますか。");

			ab.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mDelTask = new DeleteContributeTask();
					mDelTask.execute(bitem.id);
				}
			});
			ab.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			ab.create().show();

		}
	}





	public class DeleteContributeTask extends AsyncTask<Integer, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {


			//Toast.makeText(LoginActivity.this, "バックグラウンド処理中です！", Toast.LENGTH_SHORT).show();

			//通信処理

			//LoginInfo lf = mLoginManager.login(mId, mPassword);
			boolean result = mBoradManager.deleteContoribute(mLoginManager , mLoginInfo, params[0]);

			return result;
		}

		@Override
		protected void onPostExecute(final Boolean result) {
			mDelTask = null;
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

			if(result){
				toast("削除しました。");
				//mBoradManager.removeItemById(id)
				mAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onCancelled() {
			mDelTask = null;
			//mAuthTask = null;
			//showProgress(false);
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==REQCODE_CONTRIBUTE_SUBMIT){
			if(resultCode==RESULT_OK){

				int id = data.getIntExtra(Const.AK_SUBMIT_CONTRIBUTE_ID, -1);
				LoginInfo lf = (LoginInfo) data.getSerializableExtra(Const.AK_LOGIN_INFO);
				if(lf!=null){
					mLoginInfo = lf;
				}
				
//				LoginInfo lf = (LoginInfo) data.getSerializableExtra(Const.AK_LOGIN_INFO);
//
//				Intent intent = new Intent(StartActivity.this, BoardActivity.class);
//				intent.putExtra(Const.AK_LOGIN_INFO, lf);
//
//				mMode = S_MODE_NONE;
//				startActivity(intent);

				attemptGetBorad();

			}
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
