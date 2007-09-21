package com.mysticcoders.pastebin.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;

/**
 * Base tag that makes sure all links are relative to the context root no matter the path
 *  
 * @author Igor Vaynberg (ivaynberg)
 */
public class BaseTag extends WebComponent {

	public BaseTag(String id) {
		super(id);
	}
	
	@Override
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "base");
		String href=buildBaseHref();
		tag.put("href", href);
	}
	
	private String buildBaseHref() {
		RequestCycle cycle=getRequestCycle();
		ServletWebRequest request=(ServletWebRequest) cycle.getRequest();
		HttpServletRequest req=request.getHttpServletRequest();
		
        StringBuffer href = new StringBuffer();

		String scheme = req.getScheme();

		href.append(scheme);
		href.append("://");
		href.append(req.getServerName());

        if(req.getServerPort()!=80) {        
            href.append(":");
            href.append(req.getServerPort());
        }
        href.append(req.getContextPath());
		href.append("/");
		
		return href.toString();
	}
}
