package jp.ne.sakura.sasamori;

import jp.ne.sakura.sasamori.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SasamorieroActivity extends Activity {
	
	WebView webview = null;
	
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