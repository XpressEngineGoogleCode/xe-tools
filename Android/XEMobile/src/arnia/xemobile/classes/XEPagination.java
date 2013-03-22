package arnia.xemobile.classes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "pagination")
public class XEPagination {

	@Element
	public int total_count;

	@Element
	public int total_page;

	@Element
	public int cur_page;

	@Element
	public int page_count;

	@Element
	public int first_page;

	@Element
	public int last_page;

}
