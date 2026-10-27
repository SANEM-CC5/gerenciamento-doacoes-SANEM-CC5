package com.javalovers.core.report.controller;

import com.javalovers.core.beneficiary.domain.dto.request.BeneficiaryFilterDTO;
import com.javalovers.core.item.domain.dto.request.ItemFilterDTO;
import com.javalovers.core.report.service.ReportService;
import com.javalovers.core.withdrawal.domain.dto.request.WithdrawalFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ==========================================
    // 1. BENEFICIÁRIOS
    // ==========================================

    @GetMapping("/beneficiaries/csv")
    public ResponseEntity<byte[]> exportBeneficiariesCsv(BeneficiaryFilterDTO filter) {
        byte[] csv = reportService.generateBeneficiariesCsv(filter);
        return buildCsvResponse(csv, "relatorio_beneficiarios.csv");
    }

    @GetMapping("/beneficiaries/pdf")
    public ResponseEntity<byte[]> exportBeneficiariesPdf(BeneficiaryFilterDTO filter) throws IOException {
        byte[] pdf = reportService.generateBeneficiariesPdf(filter);
        return buildPdfResponse(pdf, "relatorio_beneficiarios.pdf");
    }

    // ==========================================
    // 2. ESTOQUE
    // ==========================================

    @GetMapping("/inventory/csv")
    public ResponseEntity<byte[]> exportInventoryCsv(ItemFilterDTO filter) {
        byte[] csv = reportService.generateInventoryCsv(filter);
        return buildCsvResponse(csv, "relatorio_estoque.csv");
    }

    @GetMapping("/inventory/pdf")
    public ResponseEntity<byte[]> exportInventoryPdf(ItemFilterDTO filter) throws IOException {
        byte[] pdf = reportService.generateInventoryPdf(filter);
        return buildPdfResponse(pdf, "relatorio_estoque.pdf");
    }

    // ==========================================
    // 3. DOAÇÕES
    // ==========================================

    @GetMapping("/donations/csv")
    public ResponseEntity<byte[]> exportDonationsCsv(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String donorName,
            @RequestParam(required = false) String attendantName,
            @RequestParam(required = false) String itemName
    ) {
        byte[] csv = reportService.generateDonationsCsv(startDate, endDate, donorName, attendantName, itemName);
        return buildCsvResponse(csv, "relatorio_doacoes.csv");
    }

    @GetMapping("/donations/pdf")
    public ResponseEntity<byte[]> exportDonationsPdf(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String donorName,
            @RequestParam(required = false) String attendantName,
            @RequestParam(required = false) String itemName
    ) throws IOException {
        byte[] pdf = reportService.generateDonationsPdf(startDate, endDate, donorName, attendantName, itemName);
        return buildPdfResponse(pdf, "relatorio_doacoes.pdf");
    }

    // ==========================================
    // 4. RETIRADAS
    // ==========================================

    @GetMapping("/withdrawals/csv")
    public ResponseEntity<byte[]> exportWithdrawalsCsv(WithdrawalFilterDTO filter) {
        byte[] csv = reportService.generateWithdrawalsCsv(filter);
        return buildCsvResponse(csv, "relatorio_retiradas.csv");
    }

    @GetMapping("/withdrawals/pdf")
    public ResponseEntity<byte[]> exportWithdrawalsPdf(WithdrawalFilterDTO filter) throws IOException {
        byte[] pdf = reportService.generateWithdrawalsPdf(filter);
        return buildPdfResponse(pdf, "relatorio_retiradas.pdf");
    }

    // ==========================================
    // 5. GERAL
    // ==========================================

    @GetMapping("/general/csv")
    public ResponseEntity<byte[]> exportGeneralCsv() {
        byte[] csv = reportService.generateGeneralCsv();
        return buildCsvResponse(csv, "relatorio_geral.csv");
    }

    @GetMapping("/general/pdf")
    public ResponseEntity<byte[]> exportGeneralPdf() throws IOException {
        byte[] pdf = reportService.generateGeneralPdf();
        return buildPdfResponse(pdf, "relatorio_geral.pdf");
    }

    // ==========================================
    // HELPERS DE RESPOSTA HTTP
    // ==========================================

    private ResponseEntity<byte[]> buildCsvResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8");
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
    }

    private ResponseEntity<byte[]> buildPdfResponse(byte[] content, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
        headers.setContentLength(content.length);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}
