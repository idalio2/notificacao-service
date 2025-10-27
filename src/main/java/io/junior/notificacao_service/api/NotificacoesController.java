package io.junior.notificacao_service.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacoesController {

    public record PedidoNotificacao(
            @NotBlank @Email String email,
            @NotBlank @Size(max = 500) String mensagem
    ) {}

    public record NotificacaoCriada(String id, String email, String estado, Instant criadoEm) {}

    private final Map<String, NotificacaoCriada> banco = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<NotificacaoCriada> criar(@Valid @RequestBody PedidoNotificacao pedido) {
        var id = UUID.randomUUID().toString();
        var criada = new NotificacaoCriada(id, pedido.email(), "ENFILEIRADA", Instant.now());
        banco.put(id, criada);
        return ResponseEntity
                .created(URI.create("/api/notificacoes/" + id))
                .body(criada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacaoCriada> byId(@PathVariable String id) {
        var n = banco.get(id);
        return (n == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(n);
    }

    @GetMapping("/health")
    public String health() { return "OK"; }
}
