package br.com.predial.noticias;

import br.com.predial.noticias.config.LocalEnvironment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoticiasApplication {

    public static void main(String[] args) {
        LocalEnvironment.load();
        SpringApplication.run(NoticiasApplication.class, args);
    }
}
