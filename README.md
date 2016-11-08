# Google Alerts Finder
Google Alerts Finder is a simple library for Android for generating a list of Google Alert feeds for a specific Google account. Since there is no public API for Google Alerts, the library prompts the user to log in to Google through a WebView and then scrapes the Google Alerts page.

## Gradle
```
compile 'com.reardenapps:googlealertsfinder:1.1.0'
```

## Usage
Create a new instance of GoogleAlertFinder. Set a OnAlertsLoadedListener for the GoogleAlertFinder instance -- the onSuccess method will be called after the user presses the login button and the resulting page is successfully scraped.
```
GoogleAlertFinder finder = new GoogleAlertFinder();
finder.setOnAlertsLoadedListener(new GoogleAlertFinder.OnAlertsLoadedListener() {
    @Override
    public void onSuccess(ArrayList<GoogleAlert> alerts) {
      // Do something with the results
    }

    @Override
    public void onFailure(Exception e) {
    }
});
```

The results are generated as GoogleAlert objects.
```
// Gets the associated Google search query for the alert
String query = alerts.get(1).getQuery();
// Gets the full url for the alert's RSS feed
String url = alerts.get(1).getFeedUrl();
```

To launch the WebView, call getAlerts() and pass in the desired parent view. The WebView will fill up whatever view you pass in. In most cases, android.R.id.content will work fine, but you can pass in any LinearLayout, RelativeLayout or FrameLayout.

**Note:** if using a LinearLayout, the WebView may not fill up the entire parent view. If that's the case, just wrap your LinearLayout in a FrameLayout and pass that in instead.
```
finder.getAlerts(findViewById(android.R.id.content));
```