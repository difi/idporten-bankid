package no.idporten.bankid;

import no.idporten.bankid.config.CacheConfiguration;
import no.idporten.bankid.util.BankIDProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.cache.type=jcache"})
@ContextConfiguration(classes = {BankidApplication.class, CacheConfiguration.class})
public class BankIDResponseServletTest {

    private BankIDResponseServlet rs;

    @Mock
    private HttpServletRequest mockedRequest;

    @Mock
    private HttpServletResponse mockedResponse;

    @Mock
    private HttpSession mockedSession;

    @Autowired
    BankIDProperties bankIDProperties;

    @Autowired
    BankIDCache bankIDCache;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockedRequest.getSession()).thenReturn(mockedSession);
        when(mockedSession.getAttribute("forceAuth")).thenReturn("true");
        when(mockedSession.getAttribute("gx_charset")).thenReturn("UTF-8");
        when(mockedSession.getAttribute("locale")).thenReturn("en");
        when(mockedSession.getAttribute("goto")).thenReturn("home");
        when(mockedSession.getId()).thenReturn("mysid");
        rs = new BankIDResponseServlet(bankIDProperties, bankIDCache);

    }

    @Test
    public void testServiceHttpServletRequestHttpServletResponseAuthenticated() throws ServletException, IOException {
        bankIDCache.putSSN("mysid", "19096948045");

        rs.service(mockedRequest, null, mockedResponse);
        verify(mockedResponse, times(1))
                .sendRedirect(contains("service=BankIDEkstern"));

    }

    @Test
    public void testServiceHttpServletRequestHttpServletResponseInvalidSSN() throws ServletException, IOException {
        bankIDCache.putSSN("mysid", "");
        rs.service(mockedRequest, null, mockedResponse);
        verify(mockedResponse, times(1))
                .sendRedirect(contains("service=null"));

    }

    @Test
    public void testServiceHttpServletRequestHttpServletResponseError() throws IOException, ServletException {
        when(mockedRequest.getParameter("idpError")).thenReturn("feil");
        when(mockedSession.getAttribute("start-service")).thenReturn("startService");
        rs.service(mockedRequest, null, mockedResponse);
        verify(mockedResponse, times(1))
                .sendRedirect(contains("service=startService"));

    }

}
