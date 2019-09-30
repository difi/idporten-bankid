package no.idporten.bankid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Validerer code challenge og returnerer token med fødselsnummer
 */
@Controller
@RequestMapping(value = "/token")
@Slf4j
@RequiredArgsConstructor
public class BankIDJSTokenController {

    private final BankIDCache bankIDCache;
    private final static int expirySeconds = 600;

    @PostMapping(
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity handleAuthorizationCodeGrant(@RequestParam(value = "code", required = false) String code) {
        log.error("SID ++-" + code);
        String sid = bankIDCache.getSID(code);
        if (sid == null) {
            return ResponseEntity.notFound().build();
        }
        String ssn = bankIDCache.getSSN(sid);
        byte[] ocsp = bankIDCache.getOCSP(sid);

        final TokenResponse tokenResponse = new TokenResponse(ssn,
                Base64.encodeBase64String(ocsp), expirySeconds);

        bankIDCache.removeSession(sid);
        bankIDCache.removeUuidSID(code);
        return ResponseEntity.ok(tokenResponse);
    }



}
