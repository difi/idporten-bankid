package no.idporten.bankid;

import no.bbs.server.constants.JServerConstants;
import no.bbs.server.exception.BIDException;
import no.bbs.server.vos.BIDSessionData;
import no.idporten.bankid.util.BankIDProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RequestMapping
@Controller
public class BankIDServlet {
    private final Logger log = LoggerFactory.getLogger(BankIDServlet.class);

    private BankIDProperties bankIDProperties;
    private BankIDFacadeWrapper bankIDFacadeWrapper;
    private BankIDCache bankIDCache;

    public BankIDServlet(BankIDProperties bankIDProperties,
                         BankIDFacadeWrapper bankIDFacadeWrapper,
                         BankIDCache bankIDCache) {
        this.bankIDProperties = bankIDProperties;
        this.bankIDFacadeWrapper = bankIDFacadeWrapper;
        this.bankIDCache = bankIDCache;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/bankid")
    protected void service(HttpServletRequest request,
                           @RequestParam String operation,
                           @RequestParam String sid,
                           @RequestParam String encKey,
                           @RequestParam String encData,
                           @RequestParam String encAuth,
                           HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        log.info(request.getParameter("ForceAuth"));
        if ("initAuth".equalsIgnoreCase(operation)) {
            // log.debug("initAuth Response: " + initAuth(request));
            response.getWriter().println(initAuth(operation, sid, encKey, encData, encAuth));
        } else if ("verifyAuth".equalsIgnoreCase(operation)) {
            // log.debug("verifyAuth Response: " + verifyAuth(request));
            response.getWriter().println(verifyAuth(operation, sid, encKey, encData, encAuth));
        } else if ("handleError".equalsIgnoreCase(operation)) {
            // log.debug("handleError Response: " + handleError(request));
            response.getWriter().println(handleError(operation, sid, encKey, encData, encAuth));
        } else {
            log.warn("Unexpected operation: " + operation);
        }

        response.getWriter().flush();
    }
    
    private String initAuth(String operation, String sid, String encKey, String encData, String encAuth) {
        log.debug("initAuth()");
        String traceID = bankIDCache.getTraceId(sid);//request.getSession().getId());
        BIDSessionData sessionData = new BIDSessionData(traceID);

        try {
            String responseToClient = bankIDFacadeWrapper.getFacade().initTransaction(operation, encKey, encData,
                            encAuth, sid, sessionData);
            // store the sessionData in local session store for later use
            bankIDCache.putBIDSessionData(sid, sessionData);
            return responseToClient;
        } catch (BIDException be) {
            log.error(be.toString());
            return handleBIDException(operation, sessionData, be);
        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String verifyAuth(String operation, String sid, String encKey, String encData, String encAuth) {
        log.debug("verifyAuth()");
        // get the sessionData from local session store
        BIDSessionData sessionData = bankIDCache.getBIDSessionData(sid);
        ArrayList additionalInfos = new ArrayList();
        additionalInfos.add(JServerConstants.LABEL_OID_OCSP_SSN);
        sessionData.setAdditionalInfoList(additionalInfos);

        try {
            bankIDFacadeWrapper.getFacade().verifyTransactionRequest(operation, encKey, encData, encAuth, sid,
                    sessionData);
            bankIDCache.putOCSP(sid, sessionData.getCertificateStatus().getOcspResponse());
            bankIDCache.putSSN(sid, sessionData.getCertificateStatus().getAddInfoSSN());
        } catch (BIDException be) {
            log.error("BankIDServlet.verifyAuth exception " + be);
            log.error(be.toString());
            return handleBIDException(operation, sessionData, be);
        }
        sessionData.setNextURL(bankIDProperties.getBankIdResponseServletUrl());
        try {
            // return the response to the client, and update user session as
            // authenticated
            return bankIDFacadeWrapper.getFacade().verifyTransactionResponse(sessionData);
        } catch (BIDException be) {
            log.error(be.toString());
            return handleBIDException(operation, sessionData, be);
        }

    }

    /**
     * Handles BIDExceptions and generates error response to BankID client.
     * 
     * @param operation operation (used for logging)
     * @param sessionData session data
     * @param exception the API exception
     * @return response string to client
     */
    private String handleBIDException(final String operation, final BIDSessionData sessionData,
                                      final BIDException exception) {
        log.error("Exception for operation [" + operation + "]", exception);
        final int errorCode = exception.getErrorCode();
        sessionData.setErrCode("" + errorCode);
        sessionData.setNextURL(bankIDProperties.getBankIdResponseServletUrl() + "?idpError="+errorCode);
        try {
            return bankIDFacadeWrapper.getFacade().verifyTransactionResponse(sessionData);
        } catch (BIDException e) {
            log.error("Error verifyTransactionResponse.");
            throw new RuntimeException("Cannot handle error");
        }
    }

    private String handleError(String operation, String sid, String encKey, String encData, String encAuth) {
        log.error("handleError()");
        BIDSessionData sessionData = bankIDCache.getBIDSessionData(sid);

        if (sessionData == null) {
            log.error("Error getting session");
            throw new RuntimeException("No BankID session associated with httpSession");
        }
        try {
            sessionData.setNextURL(bankIDProperties.getBankIdResponseServletUrl() + "?idpError=");
            bankIDFacadeWrapper.getFacade().verifyTransactionRequest(operation, encKey, encData, encAuth, sid,
                            sessionData);
            final String errCode = sessionData.getErrCode();
            log.error("Error code from BID client [" + errCode + "]");
            sessionData.setNextURL(bankIDProperties.getBankIdResponseServletUrl() + "?idpError="+errCode);
            return bankIDFacadeWrapper.getFacade().verifyTransactionResponse(sessionData);
        } catch (BIDException e) {
            return handleBIDException(operation, sessionData, e);
        }
    }

}
