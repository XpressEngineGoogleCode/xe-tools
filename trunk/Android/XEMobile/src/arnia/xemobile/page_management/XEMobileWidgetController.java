package arnia.xemobile.page_management;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import arnia.xemobile.R;
import arnia.xemobile.classes.XEHost;

public class XEMobileWidgetController extends Activity
{
	private WebView webView;
	private String pageMid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xemobilewidgetlayout);
		
		pageMid = getIntent().getStringExtra("mid");
		
		//get vid for virtual site
		String vid="";
		if(getIntent().getExtras().containsKey("vid")){
			vid = getIntent().getExtras().getString("vid");
		}
		
		webView = (WebView) findViewById(R.id.XEMOBILE_WIDGET_WEBVIEW);
		
	//	setCookies();
		if(vid.compareTo("")==0){
			webView.loadUrl(XEHost.getINSTANCE().getURL() + "/index.php?mid=" + pageMid);
		}else{
			webView.loadUrl(XEHost.getINSTANCE().getURL() + "/index.php?mid=" + pageMid +"&vid=" + vid);	
		}
	}

//save the cookies from XEHost to webView
//	private void setCookies()
//	{
//		List<Cookie> cookies = XEHost.getINSTANCE().getCookies();
//		
//		if (cookies != null) {
//		      for (Cookie cookie : cookies) {
//		        String cookieString = cookie.getName() + "="
//		            + cookie.getValue() + "; domain=" + cookie.getDomain();
//		        CookieManager.getInstance().setCookie(cookie.getDomain(),
//		            cookieString);
//		      }
//		      CookieSyncManager.getInstance().sync();
//		    }
//	}
}
