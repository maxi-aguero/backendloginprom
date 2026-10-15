package com.apress.spring;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setStatus(HttpStatus.NOT_FOUND.value());
            errorResponse.setError("Not Found");
            errorResponse.setMessage("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        System.out.println("👉 Password recibido: " + request.getPassword());
        System.out.println("👉 Password en BD (hash): " + user.getPassword());

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername(), user.getRole());
            System.out.println("✅ Token generado: " + token);

            LoginResponse successResponse = new LoginResponse();

            successResponse.setStatus(HttpStatus.OK.value());
            successResponse.setMessage("Login successful");

            successResponse.setToken(token);
            successResponse.setUsername(user.getUsername());
            successResponse.setRole(user.getRole());

            return ResponseEntity.ok(successResponse);
        }


        LoginResponse errorResponse = new LoginResponse();
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Endpoint de prueba para ver el token recibido en las llamadas
    @GetMapping("/check")
    public String checkToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("📥 Token recibido en request: " + token);
            String username = JwtUtil.validateToken(token);
            return "Token válido para usuario: " + username;
        }
        return "No se envió token";
    }

    //  /auth/register
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {

            RegisterResponse errorResponse = new RegisterResponse();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Bad Request");
            errorResponse.setMessage("Usuario ya existe");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            RegisterResponse errorResponse = new RegisterResponse();
            errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            errorResponse.setError("Bad Request");
            errorResponse.setMessage("Email ya registrado");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Crear usuario (forzar rol USER para evitar confusión con ADMIN)
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");  // ⚠️ nunca tomar el rol del request directamente
        //podemos pensar que siemmpre es user y que cuando setea pasa a ser admin
        user.setEmail(request.getEmail());

        userRepository.save(user);

        // Respuesta de éxito
        RegisterResponse successResponse = new RegisterResponse();
        successResponse.setStatus(HttpStatus.CREATED.value());
        successResponse.setMessage("Usuario registrado con éxito");
        successResponse.setUsername(user.getUsername());
        successResponse.setEmail(user.getEmail());
        successResponse.setRole(user.getRole());

        return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
    }



    @PostMapping("/recovery-password")
    public ResponseEntity<RecoveryResponse> recoveryPassword(@RequestBody RecoveryRequest request) {
        RecoveryResponse response = new RecoveryResponse();

        UserEntity user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setError("Not Found");
            response.setMessage("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String token = tokenService.createToken(user);
        String link = "http://localhost:5173/reset-password?token=" + token;

        String htmlBody = "<p>Haz clic en el siguiente enlace para recuperar tu contraseña:</p>"
                + "<a href='" + link + "'>Recuperar contraseña</a>";

        try {
            emailService.sendRecoveryEmail(user.getEmail(), "Recuperación de contraseña", htmlBody);
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("Correo enviado con éxito");
            response.setEmail(user.getEmail());
            response.setToken(token); // opcional
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Error");
            response.setMessage("Error al enviar correo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<RecoveryResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        RecoveryResponse response = new RecoveryResponse();

        UserEntity user = tokenService.validateToken(request.getToken());
        if (user == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setError("Bad Request");
            response.setMessage("Token inválido o expirado");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenService.invalidateToken(request.getToken());

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Contraseña actualizada con éxito");
        response.setEmail(user.getEmail());
        return ResponseEntity.ok(response);
    }


}
