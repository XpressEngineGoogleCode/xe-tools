package arnia.xemobile.data;

public class XEMobileSite {
	public long id ;
	public String siteUrl;
	public String userName;
	public String password;
	
	public XEMobileSite(long id, String siteUrl, String username, String password){
		this.id = id;
		this.siteUrl = siteUrl;
		this.userName = username;
		this.password = password;
	}
	@Override
	public String toString() {
	
		return this.siteUrl;
	}
}
