package com.example.backend.controller;

import com.example.backend.dto.TransacaoResponseDTO;
import com.example.backend.exception.GlobalExceptionHandler.ErrorResponse;
import com.example.backend.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
@Tag(name = "Transações", description = "Extrato e recibos de transferências")
public class TransacaoController {

    private final TransacaoService transacaoService;

    @GetMapping
    @Operation(summary = "Extrato global — todas as transferências realizadas, mais recentes primeiro")
    @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso",
            content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class)))
    public ResponseEntity<List<TransacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(transacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recibo de uma transferência específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Recibo encontrado",
                content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Transação não encontrada",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(transacaoService.buscarPorId(id));
    }

    @GetMapping("/beneficio/{beneficioId}")
    @Operation(summary = "Extrato de um benefício — transações onde é origem ou destino")
    @ApiResponse(responseCode = "200", description = "Extrato retornado com sucesso",
            content = @Content(schema = @Schema(implementation = TransacaoResponseDTO.class)))
    public ResponseEntity<List<TransacaoResponseDTO>> listarPorBeneficio(
            @PathVariable Long beneficioId) {
        return ResponseEntity.ok(transacaoService.listarPorBeneficio(beneficioId));
    }
}
