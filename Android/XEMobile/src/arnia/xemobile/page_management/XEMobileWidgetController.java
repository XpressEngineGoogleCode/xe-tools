package arnia.xemobile.page_management;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;

public class XEMobileWidgetController extends Activity {
	private WebView webView;
	private String pageMid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilewidgetlayout);

		pageMid = getIntent().getStringExtra("mid");

		// get vid for virtual site
		String vid = "";
		if (getIntent().getExtras().containsKey("vid")) {
			vid = getIntent().getExtras().getString("vid");
		}

		webView = (WebView) findViewById(R.id.XEMOBILE_WIDGET_WEBVIEW);

		// setCookies();
		if (vid.compareTo("") == 0) {
			webView.loadUrl(XEHost.getINSTANCE().getURL() + "/index.php?mid="
					+ pageMid);
		} else {
			webView.loadUrl(XEHost.getINSTANCE().getURL() + "/index.php?mid="
					+ pageMid + "&vid=" + vid);
		}
	}
}
