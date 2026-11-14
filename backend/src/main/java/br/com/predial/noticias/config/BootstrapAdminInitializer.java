package br.com.predial.noticias.config;

import br.com.predial.noticias.entity.User;
import br.com.predial.noticias.entity.UserRole;
import br.com.predial.noticias.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BootstrapAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String email;
    private final String password;

    public BootstrapAdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.email:}") String email,
            @Value("${app.bootstrap-admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        boolean usernameProvided = !username.isBlank();
        boolean emailProvided = !email.isBlank();
        boolean passwordProvided = !password.isBlank();
        if (!usernameProvided && !emailProvided && !passwordProvided) {
            return;
        }
        if (!usernameProvided || !emailProvided || !passwordProvided) {
            throw new IllegalStateException(
                    "Configure BOOTSTRAP_ADMIN_USERNAME, BOOTSTRAP_ADMIN_EMAIL e BOOTSTRAP_ADMIN_PASSWORD juntos."
            );
        }
        if (password.length() < 12) {
            throw new IllegalStateException("A senha inicial do administrador deve ter pelo menos 12 caracteres.");
        }

        User existing = userRepository.findByUsername(username).orElse(null);
        if (existing != null) {
            if (existing.getRole() != UserRole.ADMIN || !existing.getEmail().equalsIgnoreCase(email)) {
                throw new IllegalStateException("O usuário configurado para bootstrap já existe com dados diferentes.");
            }
            return;
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("O e-mail configurado para bootstrap já está em uso.");
        }

        userRepository.save(new User(
                username,
                email,
                passwordEncoder.encode(password),
                UserRole.ADMIN,
                true
        ));
    }
}
