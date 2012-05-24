/**

 * This class helps you obtain the URL of the XE Core package from http://www.xpressengine.org/download section
 * This should be updated from time to time when there are significant updates on the download page
 */
package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Catalin
 *
 */

public class LatestXEVersion
{
	public final static int PARSE_XE_ORG_TIMEOUT = 10000;
	public final static String XE_ORG_DOWNLOAD_URL = "http://www.xpressengine.org/download";
	public final static String XE_ORG_PROTOCOL = "http";
	public final static String XE_ORG_HOST = "xpressengine.org";
	public final static String XE_CORE_SECTION_NAME = "XE Core";
	public final static String XE_PACKAGES_SECTION_NAME = "Packages";
	
	
	private static String lastErrorMessage = null;
	
	/**
	* Retrieves the URL for downloading XE Core package 
	*/
	public static String getLatestXECoreVersionURL()
	{
		String xeURL = null;
		lastErrorMessage = "";
		try
		{
			Document doc = Jsoup.parse(new URL(XE_ORG_DOWNLOAD_URL), PARSE_XE_ORG_TIMEOUT);
			
			if (XE_CORE_SECTION_NAME.compareToIgnoreCase(doc.select("ul.category").first().select("li").first().select("a[href]").first().text()) != 0)
			{
				lastErrorMessage = "Could not obtain the XE Core URL (item in the left tree ) from http://www.xpressengine.org/download";
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
	* Retrieves the URL for downloading a certain XE package 
	*/
	public static String getLatestXEPackageVersionURL(String packageName)
	{
		String xeURL = null;
		lastErrorMessage = "";
		try
		{
			//parse www.xpressengine.org/download page
			Document doc = Jsoup.parse(new URL(XE_ORG_DOWNLOAD_URL), PARSE_XE_ORG_TIMEOUT);
			//select the Packages item from the left tree
			Element elem = doc.select("ul.category > li > a[href]:contains(Packages)").first();
			if (elem == null || XE_PACKAGES_SECTION_NAME.compareToIgnoreCase(elem.text()) != 0)
			{
				lastErrorMessage = "Could not obtain the Packages URL (item in the left tree ) from http://www.xpressengine.org/download";
				return null;
			}
			
			String xePackageDownloadURL = elem.attr("href");
			URL url = new URL(XE_ORG_PROTOCOL, XE_ORG_HOST, xePackageDownloadURL);
			//parse the Packages page 
			doc = Jsoup.parse(url, PARSE_XE_ORG_TIMEOUT);
			
			elem = doc.select("div.resourceContent > div.dldItem > h3 > a[href]:contains(" + packageName + " package)").first();
			
			if (elem == null)
			{
				lastErrorMessage = "Could not found " + packageName + " item in the packages list";
				return null;
			}
			xePackageDownloadURL = elem.attr("href");
			url = new URL(XE_ORG_PROTOCOL, XE_ORG_HOST, xePackageDownloadURL);
			//parse the package page 
			doc = Jsoup.parse(url, PARSE_XE_ORG_TIMEOUT);
			
			elem = doc.select("div.resourceContent > div#packageInfo > table.packageView > tbody > tr > td.right.thumbnail > p > a[href]:contains(Download)").first();
			if (elem == null)
			{
				lastErrorMessage = "Could not found Download button in the " + packageName + " package page";
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
