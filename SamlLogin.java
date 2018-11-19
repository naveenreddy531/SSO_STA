package com.hpe.mast.login;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hpe.mast.utility.CommonUtil;
import com.onelogin.saml2.Auth;

@Controller
@RequestMapping("/rest/mast")
public class SamlLogin {

	@Autowired
	CommonUtil commonUtil;

	/**
	 * API for launching the SSO(Single Sign On) using Java SAML(Security
	 * Assertion Mark-up Language) from OneLogin App
	 * 
	 * @throws Exception
	 * @redirects to SSO launch page
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public @ResponseBody void launchSSO(HttpServletResponse response, HttpServletRequest request) throws Exception {
		Map params = request.getParameterMap();
		// System.out.println("The Map is = "+params);
		String sourceApplication = "";
		Iterator i = params.keySet().iterator();
		while (i.hasNext()) {
			String key = (String) i.next();
			String value = ((String[]) params.get(key))[0];

			request.getSession().setAttribute(key, value);
		}

		Auth auth = new Auth(request, response);
		auth.login();
	}

	/**
	 * API for launching the MAST(Single Sign On)
	 * 
	 * @throws Exception
	 * @redirects to MAST launch page get User email after return auth user ,
	 *            like Endpoint
	 */
	@RequestMapping(value = "/launch", method = RequestMethod.POST)
	public @ResponseBody void fetchUserId(HttpServletResponse response, HttpServletRequest request) throws Exception {
		Auth auth = new Auth(request, response);
		auth.processResponse();
		String pathName = "";
		if (auth.getNameId() != null && auth.getNameId() != "") {
			request.getSession().setAttribute("UserID", auth.getNameId());
			CommonUtil.setLoggedInUser(auth.getNameId());
			response.sendRedirect(request.getContextPath() + "/mast");
		}
	}

}
