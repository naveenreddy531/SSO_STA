package com.hpe.mast.controller;

import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.onelogin.saml2.Auth;
import com.hpe.mast.utility.*;

@Controller
public class ResourceController {

	private static final Logger logger = Logger.getLogger(ResourceController.class);

	@Value("${noaccess.content.display}")
	private String noacccontent;

	@Autowired
	private CommonUtil commonUtil;

	@RequestMapping(value = "{path}", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView redirect(@PathVariable String path, Model m, HttpServletRequest request,
			HttpServletResponse response) {

		String scope = request.getParameter("scope");

		if (request.getSession().getAttribute("ActiveUser") == null && (scope == null || !scope.equals("dev"))) {
			Map params = request.getParameterMap();
			Iterator i = params.keySet().iterator();
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = ((String[]) params.get(key))[0];
				request.getSession().setAttribute(key, value);
			}

			try {
				Auth auth = new Auth(request, response);
				if (!auth.isAuthenticated()) {
					request.getSession().setAttribute("ActiveUser", "login");
					auth.login();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {

			ModelAndView modelAndView = new ModelAndView();
			if ("mast".equals(path)) {
				if (commonUtil.getCurrentUserName().equalsIgnoreCase("noaccess")) {
					ModelAndView noaccessmodel = new ModelAndView("redirect:/noaccess.jsp");
					return noaccessmodel;
				} else {
					String url = path;
					;
					modelAndView.setViewName(url);
				}
			} else {
				modelAndView.setViewName("logout");
			}
			return modelAndView;

		}
	}
}
