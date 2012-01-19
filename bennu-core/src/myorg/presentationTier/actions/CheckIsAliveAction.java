package myorg.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.MyOrg;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

//@Mapping(path = "/isAlive")
public class CheckIsAliveAction extends Action {
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws Exception {
	MyOrg.getInstance();
	String timeout = request.getParameter("timeout");
	if (!StringUtils.isEmpty(timeout)) {
	    long secs = Long.parseLong(timeout);
	    Thread.sleep(secs * 1000);
	}
	return super.execute(mapping, form, request, response);
    }
}
