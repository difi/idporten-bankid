package no.idporten.bankid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;


@RequestMapping
@Controller
public class BankIDResponseController {

    @Value("${idporten.redirecturl}")
    private String redirectUrl;

    private RestTemplate restTemplate;

    @Autowired
    public BankIDResponseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/authorizationCode")
    public void receiveResponse(@RequestParam String uuid) throws URISyntaxException {
        String url = UriComponentsBuilder.newInstance()
                .uri(new URI(redirectUrl))
                .queryParam("code", uuid)
                .build()
                .toUriString();
        restTemplate.postForEntity(url, "", String.class);
    }

}
