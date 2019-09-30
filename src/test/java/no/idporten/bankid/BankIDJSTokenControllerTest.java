package no.idporten.bankid;

import no.idporten.bankid.config.CacheConfiguration;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.cache.type=jcache"})
@ContextConfiguration(classes = {BankidApplication.class, CacheConfiguration.class})
public class BankIDJSTokenControllerTest {

    private MockMvc mockMvc;

    @Autowired
    BankIDCache bankIDCache;

    @Autowired
    private WebApplicationContext springContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(springContext).build();
    }

    @Test
    public void handleAuthorizationCodeGrant() throws Exception {
        String uuid = "fc897796-58da-4f68-91fb-f62b972fe323";
        String sid = "ASDF24513";
        String ssn = "23079422487";
        byte[] ocsp = "ocsp osv greier skikkelig lang".getBytes();
        bankIDCache.putSID(uuid, sid);
        bankIDCache.putSSN(sid, ssn);
        bankIDCache.putOCSP(sid, ocsp);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", uuid);
        mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonObject.toString())
                .param("code", uuid))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.ssn").value(containsString(ssn)))
                .andExpect(jsonPath("$.ocsp").value(containsString(Base64.encodeBase64String(ocsp))));
        assertNull(bankIDCache.getSSN(sid));
        assertNull(bankIDCache.getSID(uuid));

    }
}