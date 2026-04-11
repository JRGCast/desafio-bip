package com.example.backend.controller;

import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.TransacaoResponseDTO;
import com.example.backend.dto.TransferenciaRequestDTO;
import com.example.backend.exception.GlobalExceptionHandler.ErrorResponse;
import com.example.backend.service.BeneficioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficios")
@RequiredArgsConstructor
@Tag(name = "Benefícios", description = "CRUD de benefícios e transferência de valores")
public class BeneficioController {

    private final BeneficioService beneficioService;

    @GetMapping
    @Operation(summary = "Listar todos os benefícios ativos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
            content = @Content(schema = @Schema(implementation = BeneficioResponseDTO.class)))
    public ResponseEntity<List<BeneficioResponseDTO>> listar() {
        return ResponseEntity.ok(beneficioService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar benefício por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício encontrado",
                content = @Content(schema = @Schema(implementation = BeneficioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(beneficioService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo benefício")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Benefício criado",
                content = @Content(schema = @Schema(implementation = BeneficioResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioResponseDTO> criar(@Valid @RequestBody BeneficioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(beneficioService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar benefício existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Benefício atualizado",
                content = @Content(schema = @Schema(implementation = BeneficioResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de concorrência (optimistic lock)",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BeneficioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody BeneficioRequestDTO dto) {
        return ResponseEntity.ok(beneficioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar benefício (soft-delete)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Benefício desativado",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        beneficioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Alterar status ativo/inativo de um benefício",
        description = """
            Executa um UPDATE direto na coluna `ativo` sem carregar a entidade completa.
            Bypass intencional do @Version: a operação é idempotente e de baixo risco concorrente.
            Envie `true` para ativar ou `false` para desativar.
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Status alterado com sucesso",
                content = @Content),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> alterarStatus(
            @PathVariable Long id,
            @RequestBody Boolean ativo) {
        beneficioService.alterarStatus(id, ativo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transferencia")
    @Operation(summary = "Transferir valor entre benefícios",
               description = "Usa optimistic locking via @Version. Em caso de conflito concorrente retorna 409.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso",
                content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Benefício não encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de concorrência",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Saldo insuficiente",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDTO> transferir(
            @Valid @RequestBody TransferenciaRequestDTO dto) {
        return ResponseEntity.ok(beneficioService.transferir(dto));
    }
}
