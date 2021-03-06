package org.zerock.security.logout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.zerock.security.JSONConverterForAJAX;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CustomLogoutSuccessHandler extends JSONConverterForAJAX implements LogoutSuccessHandler {

    private static final Logger log = LogManager.getLogger();

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        if (authentication != null && authentication.getDetails() != null) {
            String prevPage = request.getHeader("Referer");
            if (prevPage == null)
                prevPage = "/";
            request.getSession().invalidate();

            log.info("Logout Success - " + authentication.getName());
            log.info("Previous Page: " + prevPage);
            response.setStatus(HttpServletResponse.SC_OK);
            request.getSession().setAttribute("signAlertType", "Logout");
            request.getSession().setAttribute("signAlertStatus", "SUCCESS");
            response.sendRedirect(prevPage);

//            Map<String, Object> map = new HashMap<>();
//            map.put("status", "SUCCESS");
//            map.put("redirectURL", prevPage);
//
//            JSONConverter(response, authentication, map);

        }
    }

}
