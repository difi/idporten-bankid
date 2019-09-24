package no.idporten.bankid;

import no.bbs.server.exception.BIDException;
import no.bbs.server.implementation.BIDFacade;
import no.bbs.server.vos.InitSessionInfo;
import no.idporten.bankid.config.CacheConfiguration;
import no.idporten.bankid.util.BankIDProperties;
import no.idporten.domain.auth.AuthType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.cache.type=jcache"})
@ContextConfiguration(classes = {BankidApplication.class, CacheConfiguration.class})
public class BankIDJSAuthorizeControllerTest {

    @Autowired
    private BankIDJSAuthorizeController logic;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private BIDFacade facade;

    @Autowired
    private BankIDFacadeWrapper facadeWrapper;

    @Autowired
    private BankIDProperties bankIDProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0 (iPad; CPU OS 5_1_1 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9B206 Safari/7534.48.3");
        when(request.getSession()).thenReturn(session);
        when(session.getId()).thenReturn("mysid");
        when(session.getAttribute("forceAuth")).thenReturn("true");
        when(session.getAttribute("gx_charset")).thenReturn("UTF-8");
        when(session.getAttribute("locale")).thenReturn("en");
        when(session.getAttribute("goto")).thenReturn("home");
    }

    @Test(expected = RuntimeException.class)
    public void testInitFails() throws Exception {
        facadeWrapper.setFacade(facade);
        when(facade.initSession(isA(InitSessionInfo.class))).thenThrow(new BIDException(1, "error"));
        logic.doGet(request);
    }

    @Test
    public void testInitWithBlacklistedCLient() throws Exception {
        facadeWrapper.setFacade(facade);
        when(facade.initSession(isA(InitSessionInfo.class))).thenThrow(new BIDException(6202, "Error preparing request or parsing response from Session Data Manager. Reason: (errorCode=1A13)"));
        logic.doGet(request);
        verify(request, never()).setAttribute(anyString(), anyString());
    }

    @Test
    public void testInit() throws Exception {
        when(facade.initSession(isA(InitSessionInfo.class))).thenAnswer(invocationOnMock -> {
            InitSessionInfo initSessionInfo = (InitSessionInfo) invocationOnMock.getArguments()[0];
            initSessionInfo.setHelperURI("http://www.helper.bankid.no");
            initSessionInfo.setClientID("clientId");
            initSessionInfo.setTraceID("traceId");
            return initSessionInfo;
        });
        facadeWrapper.setFacade(facade);
        logic.doGet(request);
        verify(request).setAttribute("cid", "clientId");
        verify(request).setAttribute("helperURI", "http://www.helper.bankid.no");
        verify(session).setAttribute(eq(BankIDProperties.HTTP_SESSION_AUTH_TYPE), eq(AuthType.BANKID));
        verify(session).setAttribute(eq(BankIDProperties.HTTP_SESSION_CLIENT_TYPE), eq("BankID web client"));
    }

    @Test
    public void isBlacklisted() {
        BIDException e = new BIDException(6202, "Error preparing request or parsing response from Session Data Manager. Reason: (errorCode=1A13)");
        assertTrue(logic.isBlacklisted(e));
    }

    @Test
    public void isNotBlacklisted() {
        BIDException e = new BIDException(9999, "(errorCode=1A13)");
        assertFalse(logic.isBlacklisted(e));

        e = new BIDException(6202, "Smurf");
        assertFalse(logic.isBlacklisted(e));
    }

}
