package com.app.erozukan;

import com.app.erozukan.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ErozukanMainActivity extends Activity {
	
	WebView webview = null;
	SharedPreferences sharedPreferences;
	private final String EXECUTE_TIME_NAME = "execute_time_name";
	private final String EXECUTE_TIME_KEY = "exevute_time_key";
	private final String REVIEW_FLAG_NAME = "review_flag_name";
	private final String REVIEW_FLAG_KEY = "review_flag_key";
	
	private int count = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// WebViewインスタンスの作成
		webview = new WebView(this);
		
		// 使用するXMLを設定する
		setContentView(R.layout.main);
		
		// 指定されたIDにWebViewをセットする
		webview = (WebView) findViewById(R.id.webview);
		
		// WebViewClientをオーバーライドしたWebViewClientをセットする
		webview.setWebViewClient(new CustomWebViewClient());
		
		// URLを読み込む
		webview.loadUrl("http://sasamori.sakura.ne.jp/");
		
		// javascriptを有効にする
		webview.getSettings().setJavaScriptEnabled(true);
		
		// javascriptから呼ばれるファンクションをセット
		webview.addJavascriptInterface(new JSInterface(), "native_call");
		
		saveExecuteTime();
		
		if(loadExecuteTime() == 2) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("Twitterで情報発信中");
			alertDialogBuilder.setMessage("ランキング上位の人気ちょいエロアプリや新着アプリを紹介。紹介できない過激アプリも!?");
			alertDialogBuilder.setPositiveButton("フォローする",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uri = Uri.parse("https://twitter.com/erozukan");
					Intent i = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(i);
				}
			});
			alertDialogBuilder.setNegativeButton("閉じる",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} else if(loadExecuteTime() % 10 == 0 && loadReviewFlag() == false) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			alertDialogBuilder.setTitle("アプリ評価のお願い");
			alertDialogBuilder.setMessage("皆様からの評価が無料運営のモチベーションになります。よろしくお願いします。");
			alertDialogBuilder.setPositiveButton("評価する",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveReviewFlag();
					Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=jp.ne.sakura.sasamori");
					Intent i = new Intent(Intent.ACTION_VIEW,uri);
					startActivity(i);
				}
			});
			alertDialogBuilder.setNegativeButton("今はしない",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialogBuilder.setCancelable(true);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}
	
	private void saveReviewFlag() {
		sharedPreferences = this.getSharedPreferences(REVIEW_FLAG_NAME, MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(REVIEW_FLAG_KEY, true);
		editor.commit();
	}
	
	private boolean loadReviewFlag() {
		sharedPreferences = this.getSharedPreferences(REVIEW_FLAG_NAME, MODE_PRIVATE);
		return sharedPreferences.getBoolean(REVIEW_FLAG_KEY, false);
	}
	
	private void saveExecuteTime() {
		sharedPreferences = this.getSharedPreferences(EXECUTE_TIME_NAME, MODE_PRIVATE);
		count = sharedPreferences.getInt(EXECUTE_TIME_KEY, 0);
		count++;
		Editor editor = sharedPreferences.edit();
		editor.putInt(EXECUTE_TIME_KEY, count);
		editor.commit();
	}
	
	private int loadExecuteTime() {
		sharedPreferences = this.getSharedPreferences(EXECUTE_TIME_NAME, MODE_PRIVATE);
		count = sharedPreferences.getInt(EXECUTE_TIME_KEY, 0);
		return count;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if (webview.canGoBack()){
				webview.goBack();
			} else {
				finish();
			}
			return true;
		} else if(keyCode == KeyEvent.KEYCODE_MENU){
			webview.reload();
			return true;
		}else{
			return false;
		}
	}
	
	class JSInterface {
		public void executeBrowser(String url) {
			Uri uri = Uri.parse(url);
			Intent i = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(i);
		}
	}
	
	// ページ読込中のダイヤログ表示など
	class CustomWebViewClient extends WebViewClient {
		ProgressDialog waitDialog = null;
		@Override
		//読み込み開始
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			waitDialog = new ProgressDialog(view.getContext());
			waitDialog.setMessage("読み込み中");
			waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			waitDialog.show();
		}
		@Override
		// 読み込み完了
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if(waitDialog != null){
				waitDialog.dismiss();
				waitDialog = null;
			}
		}
		@Override
		// 読み込み時にエラー
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
			dialog.setTitle("エラー");
			dialog.setMessage("読み込みに失敗しました。");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {}
			});
			if(waitDialog != null){
				waitDialog.dismiss();
				waitDialog = null;
			}
			dialog.setCancelable(false).create().show();
		}
	}
}