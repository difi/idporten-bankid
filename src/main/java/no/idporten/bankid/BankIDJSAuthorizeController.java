package no.idporten.bankid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.bbs.server.exception.BIDException;
import no.bbs.server.vos.InitSessionInfo;
import no.idporten.bankid.util.BankIDProperties;
import no.idporten.bankid.util.LanguageUtils;
import no.idporten.domain.auth.AuthType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

/**
 * Logic implementation of BankID web client (BankID 2.0)  module.
 */
@Controller
@RequestMapping(value = "/authorize")
@Slf4j
@RequiredArgsConstructor
public class BankIDJSAuthorizeController {
    private final BankIDProperties bankIdProperties;
    private final BankIDFacadeWrapper bankIdFacadeWrapper;

    private final BankIDCache bankIDCache;

    @GetMapping
    public ModelAndView doGet(HttpServletRequest request) {
        final String userAgent = request.getHeader("user-agent");
        setBankIDResponseParameters(request);
        try {
            InitSessionInfo initSessionInfo = new InitSessionInfo();
            initSessionInfo.setAction(bankIdProperties.getBankIdAction());
            initSessionInfo.setUserAgent(userAgent);
            initSessionInfo.setMerchantURL(bankIdProperties.getBankIdServletAddress());
            initSessionInfo.setLocaleId(bankIdProperties.getLocale(request));
            initSessionInfo.setSid(request.getSession().getId());
            initSessionInfo.setSuppressBroadcast(bankIdProperties.getBankIdSuppressBroadcast());
            initSessionInfo.setTimeout(bankIdProperties.getBankIdTimeout());
            initSessionInfo.setNextURL(bankIdProperties.getBankIdResponseServletUrl());
            URL merchantURL = new URL(bankIdProperties.getBankIdServletAddress());
            initSessionInfo.setMerchantFEDomain(merchantURL.getProtocol() + "://" + merchantURL.getHost());
            initSessionInfo.setWithCredentials("Y");

            initSessionInfo = bankIdFacadeWrapper.getFacade().initSession(initSessionInfo);
            request.setAttribute("cid", initSessionInfo.getClientID());
            request.setAttribute("helperURI", initSessionInfo.getHelperURI());

            bankIDCache.putTraceId(request.getSession().getId(), initSessionInfo.getTraceID());
        } catch(BIDException e){
            if (isBlacklisted(e)) {
                log.info("Failed to init BankID helper for user agent [" + userAgent + "], will display info message for user", e);
                return new ModelAndView("error"); // empty helperURI
            } else {
                log.error("Bankid exception", e);
                throw new RuntimeException("Failed to init BankID helper", e);
            }
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException("Failed to init BankID helper", e);
        }
        request.getSession().setAttribute(BankIDProperties.HTTP_SESSION_AUTH_TYPE, AuthType.BANKID);
        request.getSession().setAttribute(BankIDProperties.HTTP_SESSION_CLIENT_TYPE, "BankID web client");

        return new ModelAndView("bankidjs");
    }

    boolean isBlacklisted(BIDException e) {
        return e.getErrorCode() == 6202
                && e.getMessage() != null
                && e.getMessage().contains("1A13");
    }

    private void setBankIDResponseParameters(final HttpServletRequest request) {
        request.getSession().setAttribute("forceAuth", request.getParameter("ForceAuth"));
        request.getSession().setAttribute("gx_charset", request.getParameter("gx_charset"));
        request.getSession().setAttribute("locale", LanguageUtils.getLanguage(request));
        request.getSession().setAttribute("goto", request.getParameter("goto"));
    }

}
