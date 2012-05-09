/**
 * This class helps you obtain the URL of the XE Core package from http://www.xpressengine.org/download section
 * This should be updated from time to time when there are significant updates on the download page
 */
package utils;

import org.jsoup.Jsoup;

/**
 * @author Catalin
 *
 */

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LatestXEVersion 
{
	private final static int PARSE_XE_ORG_TIMEOUT = 10000;
	private final static String XE_ORG_DOWNLOAD_URL = "http://www.xpressengine.org/download";
	private final static String XE_ORG_PROTOCOL = "http";
	private final static String XE_ORG_HOST = "xpressengine.org";
	private final static String XE_CORE_SECTION_NAME = "XE Core";
	
	
	private static String lastErrorMessage = null;
	
	/**
	* Retrieves the URL for downloading XE Core package 
	*/
	public static String getLatestXELatestVersionURL()
	{
		String xeURL = null;
		lastErrorMessage = "";
		try
		{
			Document doc = Jsoup.parse(new URL(XE_ORG_DOWNLOAD_URL), PARSE_XE_ORG_TIMEOUT);
			
			if (XE_CORE_SECTION_NAME.compareToIgnoreCase(doc.select("ul.category").first().select("li").first().select("a[href]").first().text()) != 0)
			{
				lastErrorMessage = "Could not obtain the XE Core URL (item in the left tree ) from http://www.xpressengine.org";
				return null;
			}
			String xeCoreDownloadURL = doc.select("ul.category").first().select("li").first().select("a[href]").first().attr("href");
			URL url = new URL(XE_ORG_PROTOCOL, XE_ORG_HOST, xeCoreDownloadURL);
			doc = Jsoup.parse(url, PARSE_XE_ORG_TIMEOUT);
			
			Element elem = doc.select("div.resourceContent > div.dldItem > div.dldItemContent > p.info > a[href]:contains(Download)").first();
			if (elem == null)
			{
				lastErrorMessage = "Could not obtain the XE Core URL from http://www.xpressengine.org";
			}
			else
				xeURL = elem.attr("href");
		}
		catch(MalformedURLException mfue)
		{
			lastErrorMessage = "Caught Exception: " + mfue.getMessage();
			System.err.println(lastErrorMessage);
			mfue.printStackTrace(System.err);
		}
		catch(IOException ioe)
		{
			lastErrorMessage = "Caught Exception: " + ioe.getMessage();
			System.err.println(lastErrorMessage);
			ioe.printStackTrace(System.err);
		}
		
		return xeURL;
	}
	
	/**
	* Retrieve the last error message
	*/
	public static String getLastErrorMessage()
	{
		return lastErrorMessage;
	}
}
