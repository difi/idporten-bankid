package no.idporten.bankid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;


@RequestMapping
@Controller
@Slf4j
public class BankIDResponseController {

    @Value("${idporten.redirecturl}")
    private String redirectUrl;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/authorizationCode")
    public void receiveResponse(HttpServletRequest request,
                                @RequestParam String uuid,
                                HttpServletResponse response) throws URISyntaxException, IOException {
        String url = UriComponentsBuilder.newInstance()
                .uri(new URI((String) request.getSession().getAttribute("redirectUrl")))
                .queryParam("code", uuid)
                .queryParam("ForceAuth", request.getSession().getAttribute("forceAuth"))
                .queryParam("gx_charset", request.getSession().getAttribute("gx_charset"))
                .queryParam("locale", request.getSession().getAttribute("locale"))
                .queryParam("goto", request.getSession().getAttribute("goto"))
                .queryParam("service", request.getSession().getAttribute("service"))
                .build()
                .toUriString();
        renderHelpingPage(response, url);
    }

    private void renderHelpingPage(HttpServletResponse response, String url) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append(top(url));
        result.append(footer());
        response.setContentType(getContentType());
        response.getWriter().append(result);
    }

    private String top(String url) {
        String result = "<html>" +
                "<head><title>Submit This Form</title></head>" +
                "<body onload=\"javascript:document.forms[0].submit()\">" +
                "<form method=\"post\" action=\"" + url + "\">";
        return result;
    }

    private String footer() {
        String result = "<noscript><input type=\"submit\" value=\"Click to redirect\"></noscript>" +
                "</form>" +
                "</body>" +
                "</html>";
        return result;
    }

    public String getContentType() {
        return "text/html";
    }


}
