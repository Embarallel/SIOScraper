package com.example.grant.sioscraper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.os.Handler.Callback;
import android.os.Handler;


import java.util.logging.Logger;


public class SIOScraper extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sioscraper);

        final Callback callback = new Callback() {
            public boolean handleMessage(Message msg)
            {
                ((TextView) findViewById(R.id.debugView)).setText(msg.getData().getCharSequence("html"));
                return true;
            }
        };

        final Handler handler = new Handler(callback);

        /* An instance of this class will be registered as a JavaScript interface */
        class MyJavaScriptInterface
        {
            Context ctx;
            public MyJavaScriptInterface(Context ctx)
            {
                this.ctx = ctx;
            }

            @JavascriptInterface
            public void showHTML(String html)
            {
                Log.d("SIOScraper", "showHTML called");
                Message message = Message.obtain();
                message.getData().putCharSequence("html", html);
                handler.sendMessage(message);
                Log.d("SIOScraper", html);
            }
        }

        final WebView browser = (WebView)findViewById(R.id.browser);
        browser.getSettings().setJavaScriptEnabled(true);

        /* Register a new JavaScript interface called HTMLOUT */
        browser.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");

        /* WebViewClient must be set BEFORE calling loadUrl! */
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("SIOScraper", "onPageFinished called");
                /* This call injects JavaScript into the page which just finished loading. */
                browser.loadUrl("javascript:window.HTMLOUT.showHTML(document.getElementsByTagName('html')[0].innerHTML);");
            }
        });

        browser.loadUrl("www.example.com");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sioscraper, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}