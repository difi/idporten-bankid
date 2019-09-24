package no.idporten.bankid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ServletComponentScan
public class BankidApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankidApplication.class, args);
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("idporten");
        messageSource.setUseCodeAsDefaultMessage(true);

        return messageSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
