package com.reardenapps.googlealertsfinder;

import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleAlertFinder {
    private WebView webView;
    private OnAlertsLoadedListener listener;

    public GoogleAlertFinder() {
    }

    public void setOnAlertsLoadedListener(OnAlertsLoadedListener listener) {
        this.listener = listener;
    }

    public void getAlerts(View parentView) throws InvalidClassException {
        webView = new WebView(parentView.getContext());
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        if (parentView instanceof LinearLayout) {
            webView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (parentView instanceof RelativeLayout) {
            webView.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        } else if (parentView instanceof FrameLayout) {
            webView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        } else {
            throw new InvalidClassException("Parent view passed in must be LinearLayout, Relative" +
                    "Layout or FrameLayout.");
        }

        ((ViewGroup) parentView).addView(webView);
        parentView.invalidate();
        webView.loadUrl("https://accounts.google.com/ServiceLogin");
    }

    public interface OnAlertsLoadedListener {
        void onSuccess(ArrayList<GoogleAlert> alerts);

        void onFailure(Exception e);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("https://myaccount.google.com")) {
                view.setVisibility(View.GONE);
                view.loadUrl("https://www.google.com/alerts");
            }

            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.equals("https://www.google.com/alerts")) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                ViewGroup parent = (ViewGroup) view.getParent();
                parent.removeView(view);
            }
        }
    }

    private class MyJavaScriptInterface {
        @android.webkit.JavascriptInterface
        public void processHTML(String html) {
            ArrayList<GoogleAlert> alerts = new ArrayList<>();
            Pattern pattern = Pattern.compile("window\\.STATE = (.*?);</script>");   // the pattern to search for
            Matcher matcher = pattern.matcher(html);
            while (matcher.find()) {
                String data = matcher.group(1);
                try {
                    JSONArray json = new JSONArray(data);
                    if (json.optJSONArray(1) != null) {
                        JSONArray array = json.getJSONArray(1).getJSONArray(1);
                        if ((array != null) && (array.length() > 0)) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONArray anArray = array.getJSONArray(i);

                                // Add the following code to omit adding "e-mail only" alerts
                                // String email = anArray.getJSONArray(2).getJSONArray(6).getJSONArray(0).getString(2);
                                // if (email.equals(""))

                                alerts.add(new GoogleAlert(anArray.getJSONArray(2).getJSONArray(3).optString(1, ""), anArray.getString(3), anArray.getJSONArray(2).getJSONArray(6).getJSONArray(0).getString(11)));
                            }
                        }
                    }

                    if (listener != null) {
                        listener.onSuccess(alerts);
                    }

                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }
            }
        }
    }
}
