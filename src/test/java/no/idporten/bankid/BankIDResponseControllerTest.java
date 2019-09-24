package no.idporten.bankid;

import no.bbs.server.exception.BIDException;
import no.bbs.server.implementation.BIDFacade;
import no.bbs.server.vos.InitSessionInfo;
import no.idporten.bankid.config.CacheConfiguration;
import no.idporten.bankid.util.BankIDProperties;
import no.idporten.domain.auth.AuthType;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.net.URI;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.cache.type=jcache", "idporten.redirecturl=https://test.difi.no/redirect"})
@ContextConfiguration(classes = {BankidApplication.class, CacheConfiguration.class})
public class BankIDResponseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext springContext;

    @MockBean
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(springContext).build();
    }

    @Test
    public void handleAuthorizationCode() throws Exception {
        String uuid = "fc897796-58da-4f68-91fb-f62b972fe323";
        String json = "{\"uuid\": \"" + uuid + "\"}";
        String url = "https://test.difi.no/redirect?code=" + uuid;
        mockMvc.perform(post("/authorizationCode")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content(json)
                .param("uuid", uuid))
                .andExpect(status().is2xxSuccessful());
        verify(restTemplate, times(1)).postForEntity(url, "", String.class);
    }

}
