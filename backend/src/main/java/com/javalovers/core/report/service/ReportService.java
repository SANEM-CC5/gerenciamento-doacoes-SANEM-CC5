package com.javalovers.core.report.service;

import com.javalovers.core.beneficiary.domain.dto.request.BeneficiaryFilterDTO;
import com.javalovers.core.beneficiary.domain.dto.response.BeneficiaryDTO;
import com.javalovers.core.beneficiary.service.BeneficiaryService;
import com.javalovers.core.beneficiarystatus.BeneficiaryStatus;
import com.javalovers.core.donation.domain.dto.response.DonationDTO;
import com.javalovers.core.donation.service.DonationService;
import com.javalovers.core.item.domain.dto.request.ItemFilterDTO;
import com.javalovers.core.item.domain.dto.response.ItemDTO;
import com.javalovers.core.item.service.ItemService;
import com.javalovers.core.report.util.CsvReportBuilder;
import com.javalovers.core.report.util.PdfReportBuilder;
import com.javalovers.core.withdrawal.domain.dto.request.WithdrawalFilterDTO;
import com.javalovers.core.withdrawal.domain.dto.response.WithdrawalDTO;
import com.javalovers.core.withdrawal.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BeneficiaryService beneficiaryService;
    private final ItemService itemService;
    private final DonationService donationService;
    private final WithdrawalService withdrawalService;

    private String formatDate(Date date) {
        if (date == null) return "-";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String translateStatus(BeneficiaryStatus status) {
        if (status == null) return "-";
        return switch (status) {
            case APPROVED -> "Aprovado";
            case PENDING -> "Pendente";
            case REJECTED -> "Rejeitado";
        };
    }

    private ItemFilterDTO emptyItemFilter() {
        return new ItemFilterDTO(null, null, null, null, null, null, null, null);
    }

    // ==========================================
    // 1. BENEFICIÁRIOS
    // ==========================================

    public byte[] generateBeneficiariesCsv(BeneficiaryFilterDTO filter) {
        List<BeneficiaryDTO> list = beneficiaryService.listAsDTO(filter != null ? filter : new BeneficiaryFilterDTO(null, null, null, null, null));
        
        List<String> headers = List.of("ID", "Nome Completo", "CPF", "Telefone", "Dados Socioeconomicos", "Status", "Limite Mensal", "Retiradas no Mes", "Limite Restante");
        
        List<List<String>> rows = list.stream().map(b -> List.of(
                String.valueOf(b.beneficiaryId() != null ? b.beneficiaryId() : ""),
                b.fullName() != null ? b.fullName() : "",
                b.cpf() != null ? b.cpf() : "",
                b.phone() != null ? b.phone() : "",
                b.socioeconomicData() != null ? b.socioeconomicData() : "",
                translateStatus(b.beneficiaryStatus()),
                String.valueOf(b.withdrawalLimit() != null ? b.withdrawalLimit() : "-"),
                String.valueOf(b.currentWithdrawalsThisMonth() != null ? b.currentWithdrawalsThisMonth() : 0),
                String.valueOf(b.remainingWithdrawals() != null ? b.remainingWithdrawals() : 0)
        )).toList();

        return new CsvReportBuilder()
                .addHeader(headers)
                .addRows(rows)
                .build();
    }

    public byte[] generateBeneficiariesPdf(BeneficiaryFilterDTO filter) throws IOException {
        List<BeneficiaryDTO> list = beneficiaryService.listAsDTO(filter != null ? filter : new BeneficiaryFilterDTO(null, null, null, null, null));

        List<String> headers = List.of("ID", "Nome Completo", "CPF", "Telefone", "Status", "Limite", "Utilizado", "Restante");
        List<Float> widths = List.of(45f, 230f, 110f, 105f, 90f, 60f, 60f, 60f);

        List<List<String>> rows = list.stream().map(b -> List.of(
                String.valueOf(b.beneficiaryId() != null ? b.beneficiaryId() : ""),
                b.fullName() != null ? b.fullName() : "-",
                b.cpf() != null ? b.cpf() : "-",
                b.phone() != null ? b.phone() : "-",
                translateStatus(b.beneficiaryStatus()),
                String.valueOf(b.withdrawalLimit() != null ? b.withdrawalLimit() : "-"),
                String.valueOf(b.currentWithdrawalsThisMonth() != null ? b.currentWithdrawalsThisMonth() : 0),
                String.valueOf(b.remainingWithdrawals() != null ? b.remainingWithdrawals() : 0)
        )).toList();

        return new PdfReportBuilder("Relatorio de Beneficiarios", "Total: " + list.size() + " registros", headers, widths, rows).build();
    }

    // ==========================================
    // 2. ESTOQUE / ITENS (COM ATRIBUTOS RF16 - TIME 5)
    // ==========================================

    public byte[] generateInventoryCsv(ItemFilterDTO filter) {
        List<ItemDTO> list = itemService.generateItemDTOList(itemService.list(filter != null ? filter : emptyItemFilter()));

        List<String> headers = List.of("ID", "Descricao", "Codigo/Tag", "Categoria", "Tamanho", "Estado de Conservacao", "Genero", "Quantidade em Estoque");

        List<List<String>> rows = list.stream().map(i -> List.of(
                String.valueOf(i.itemId() != null ? i.itemId() : ""),
                i.description() != null ? i.description() : "",
                i.tagCode() != null ? i.tagCode() : "",
                i.category() != null && i.category().name() != null ? i.category().name() : "-",
                i.size() != null && !i.size().isBlank() ? i.size() : "-",
                i.conservationState() != null && !i.conservationState().isBlank() ? i.conservationState() : "-",
                i.gender() != null && !i.gender().isBlank() ? i.gender() : "-",
                String.valueOf(i.stockQuantity() != null ? i.stockQuantity() : 0)
        )).toList();

        return new CsvReportBuilder()
                .addHeader(headers)
                .addRows(rows)
                .build();
    }

    public byte[] generateInventoryPdf(ItemFilterDTO filter) throws IOException {
        List<ItemDTO> list = itemService.generateItemDTOList(itemService.list(filter != null ? filter : emptyItemFilter()));

        List<String> headers = List.of("ID", "Descricao", "Codigo/Tag", "Categoria", "Tam.", "Estado", "Genero", "Estoque");
        List<Float> widths = List.of(40f, 195f, 95f, 110f, 50f, 120f, 80f, 70f);

        long totalStock = list.stream().mapToLong(i -> i.stockQuantity() != null ? i.stockQuantity() : 0).sum();

        List<List<String>> rows = list.stream().map(i -> List.of(
                String.valueOf(i.itemId() != null ? i.itemId() : ""),
                i.description() != null ? i.description() : "-",
                i.tagCode() != null ? i.tagCode() : "-",
                i.category() != null && i.category().name() != null ? i.category().name() : "-",
                i.size() != null && !i.size().isBlank() ? i.size() : "-",
                i.conservationState() != null && !i.conservationState().isBlank() ? i.conservationState() : "-",
                i.gender() != null && !i.gender().isBlank() ? i.gender() : "-",
                String.valueOf(i.stockQuantity() != null ? i.stockQuantity() : 0)
        )).toList();

        return new PdfReportBuilder("Relatorio de Estoque", "Itens: " + list.size() + " | Saldo Total em Estoque: " + totalStock, headers, widths, rows).build();
    }

    // ==========================================
    // 3. DOAÇÕES
    // ==========================================

    public byte[] generateDonationsCsv(Date startDate, Date endDate, String donorName, String attendantName, String itemName) {
        List<DonationDTO> list = donationService.findAllFiltered(startDate, endDate, donorName, attendantName, itemName);

        List<String> headers = List.of("ID", "Data", "Doador", "Atendente", "Itens Recebidos", "Quantidade Total");

        List<List<String>> rows = list.stream().map(d -> {
            String dateStr = formatDate(d.donationDate());
            String donor = d.donor() != null && d.donor().name() != null ? d.donor().name() : "-";
            String attendant = d.receiverUser() != null && d.receiverUser().name() != null ? d.receiverUser().name() : "-";

            String itemsStr = (d.items() == null || d.items().isEmpty()) ? "-" :
                    d.items().stream()
                            .map(it -> (it.item() != null ? it.item().description() : "Item") + " (" + (it.quantity() != null ? it.quantity() : 0) + ")")
                            .collect(Collectors.joining(", "));

            int totalQtd = d.items() == null ? 0 : d.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum();

            return List.of(
                    String.valueOf(d.donationId() != null ? d.donationId() : ""),
                    dateStr,
                    donor,
                    attendant,
                    itemsStr,
                    String.valueOf(totalQtd)
            );
        }).toList();

        return new CsvReportBuilder()
                .addHeader(headers)
                .addRows(rows)
                .build();
    }

    public byte[] generateDonationsPdf(Date startDate, Date endDate, String donorName, String attendantName, String itemName) throws IOException {
        List<DonationDTO> list = donationService.findAllFiltered(startDate, endDate, donorName, attendantName, itemName);

        List<String> headers = List.of("ID", "Data", "Doador", "Atendente", "Itens Doados", "Qtd Total");
        List<Float> widths = List.of(45f, 85f, 180f, 140f, 230f, 80f);

        int totalItens = list.stream()
                .mapToInt(d -> d.items() == null ? 0 : d.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum())
                .sum();

        List<List<String>> rows = list.stream().map(d -> {
            String dateStr = formatDate(d.donationDate());
            String donor = d.donor() != null && d.donor().name() != null ? d.donor().name() : "-";
            String attendant = d.receiverUser() != null && d.receiverUser().name() != null ? d.receiverUser().name() : "-";

            String itemsStr = (d.items() == null || d.items().isEmpty()) ? "-" :
                    d.items().stream()
                            .map(it -> (it.item() != null ? it.item().description() : "Item") + " (" + (it.quantity() != null ? it.quantity() : 0) + ")")
                            .collect(Collectors.joining(", "));

            int totalQtd = d.items() == null ? 0 : d.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum();

            return List.of(
                    String.valueOf(d.donationId() != null ? d.donationId() : ""),
                    dateStr,
                    donor,
                    attendant,
                    itemsStr,
                    String.valueOf(totalQtd)
            );
        }).toList();

        return new PdfReportBuilder("Relatorio de Doacoes", "Total Doacoes: " + list.size() + " | Total Itens: " + totalItens, headers, widths, rows).build();
    }

    // ==========================================
    // 4. RETIRADAS
    // ==========================================

    public byte[] generateWithdrawalsCsv(WithdrawalFilterDTO filter) {
        List<WithdrawalDTO> list = withdrawalService.generateWithdrawalDTOList(
                withdrawalService.list(filter != null ? filter : new WithdrawalFilterDTO(null, null, null, null, null, null, null))
        );

        List<String> headers = List.of("ID", "Data", "Beneficiario", "Atendente", "Itens Retirados", "Quantidade Total");

        List<List<String>> rows = list.stream().map(w -> {
            String dateStr = formatDate(w.withdrawalDate());
            String ben = w.beneficiary() != null && w.beneficiary().fullName() != null ? w.beneficiary().fullName() : "-";
            String attendant = w.appUser() != null && w.appUser().name() != null ? w.appUser().name() : "-";

            String itemsStr = (w.items() == null || w.items().isEmpty()) ? "-" :
                    w.items().stream()
                            .map(it -> (it.itemDTO() != null ? it.itemDTO().description() : "Item") + " (" + (it.quantity() != null ? it.quantity() : 0) + ")")
                            .collect(Collectors.joining(", "));

            int totalQtd = w.items() == null ? 0 : w.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum();

            return List.of(
                    String.valueOf(w.withdrawalId() != null ? w.withdrawalId() : ""),
                    dateStr,
                    ben,
                    attendant,
                    itemsStr,
                    String.valueOf(totalQtd)
            );
        }).toList();

        return new CsvReportBuilder()
                .addHeader(headers)
                .addRows(rows)
                .build();
    }

    public byte[] generateWithdrawalsPdf(WithdrawalFilterDTO filter) throws IOException {
        List<WithdrawalDTO> list = withdrawalService.generateWithdrawalDTOList(
                withdrawalService.list(filter != null ? filter : new WithdrawalFilterDTO(null, null, null, null, null, null, null))
        );

        List<String> headers = List.of("ID", "Data", "Beneficiario", "Atendente", "Itens Retirados", "Qtd Total");
        List<Float> widths = List.of(45f, 85f, 180f, 140f, 230f, 80f);

        int totalItens = list.stream()
                .mapToInt(w -> w.items() == null ? 0 : w.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum())
                .sum();

        List<List<String>> rows = list.stream().map(w -> {
            String dateStr = formatDate(w.withdrawalDate());
            String ben = w.beneficiary() != null && w.beneficiary().fullName() != null ? w.beneficiary().fullName() : "-";
            String attendant = w.appUser() != null && w.appUser().name() != null ? w.appUser().name() : "-";

            String itemsStr = (w.items() == null || w.items().isEmpty()) ? "-" :
                    w.items().stream()
                            .map(it -> (it.itemDTO() != null ? it.itemDTO().description() : "Item") + " (" + (it.quantity() != null ? it.quantity() : 0) + ")")
                            .collect(Collectors.joining(", "));

            int totalQtd = w.items() == null ? 0 : w.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum();

            return List.of(
                    String.valueOf(w.withdrawalId() != null ? w.withdrawalId() : ""),
                    dateStr,
                    ben,
                    attendant,
                    itemsStr,
                    String.valueOf(totalQtd)
            );
        }).toList();

        return new PdfReportBuilder("Relatorio de Retiradas", "Total Retiradas: " + list.size() + " | Total Itens: " + totalItens, headers, widths, rows).build();
    }

    // ==========================================
    // 5. GERAL (VISÃO CONSOLIDADA)
    // ==========================================

    public byte[] generateGeneralCsv() {
        List<BeneficiaryDTO> beneficiaries = beneficiaryService.listAsDTO(new BeneficiaryFilterDTO(null, null, null, null, null));
        List<ItemDTO> items = itemService.generateItemDTOList(itemService.list(emptyItemFilter()));
        List<DonationDTO> donations = donationService.findAll();
        List<WithdrawalDTO> withdrawals = withdrawalService.generateWithdrawalDTOList(withdrawalService.list(new WithdrawalFilterDTO(null, null, null, null, null, null, null)));

        long totalEstoque = items.stream().mapToLong(i -> i.stockQuantity() != null ? i.stockQuantity() : 0).sum();
        int totalItensDoados = donations.stream().mapToInt(d -> d.items() == null ? 0 : d.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum()).sum();
        int totalItensRetirados = withdrawals.stream().mapToInt(w -> w.items() == null ? 0 : w.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum()).sum();

        return new CsvReportBuilder()
                .addComment("RELATORIO GERAL CONSOLIDADO SANEM")
                .addHeader(List.of("Indicador", "Valor"))
                .addRow(List.of("Total de Itens em Estoque", String.valueOf(totalEstoque)))
                .addRow(List.of("Total de Itens Recebidos (Doacoes)", String.valueOf(totalItensDoados)))
                .addRow(List.of("Total de Itens Retirados", String.valueOf(totalItensRetirados)))
                .addRow(List.of("Total de Beneficiarios Cadastrados", String.valueOf(beneficiaries.size())))
                .addRow(List.of("Total de Doacoes Registradas", String.valueOf(donations.size())))
                .addRow(List.of("Total de Retiradas Registradas", String.valueOf(withdrawals.size())))
                .build();
    }

    public byte[] generateGeneralPdf() throws IOException {
        List<BeneficiaryDTO> beneficiaries = beneficiaryService.listAsDTO(new BeneficiaryFilterDTO(null, null, null, null, null));
        List<ItemDTO> items = itemService.generateItemDTOList(itemService.list(emptyItemFilter()));
        List<DonationDTO> donations = donationService.findAll();
        List<WithdrawalDTO> withdrawals = withdrawalService.generateWithdrawalDTOList(withdrawalService.list(new WithdrawalFilterDTO(null, null, null, null, null, null, null)));

        long totalEstoque = items.stream().mapToLong(i -> i.stockQuantity() != null ? i.stockQuantity() : 0).sum();
        int totalItensDoados = donations.stream().mapToInt(d -> d.items() == null ? 0 : d.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum()).sum();
        int totalItensRetirados = withdrawals.stream().mapToInt(w -> w.items() == null ? 0 : w.items().stream().mapToInt(it -> it.quantity() != null ? it.quantity() : 0).sum()).sum();

        List<String> headers = List.of("Indicador Gerencial", "Quantidade / Valor", "Descricao");
        List<Float> widths = List.of(280f, 160f, 320f);

        List<List<String>> rows = List.of(
                List.of("Saldo Total em Estoque", String.valueOf(totalEstoque), "Soma das quantidades de todos os itens cadastrados"),
                List.of("Total de Itens Doados", String.valueOf(totalItensDoados), "Itens recebidos atraves de todas as doacoes registradas"),
                List.of("Total de Itens Retirados", String.valueOf(totalItensRetirados), "Itens entregues a beneficiarios atendidos"),
                List.of("Total de Beneficiarios", String.valueOf(beneficiaries.size()), "Pessoas e familias cadastradas no sistema"),
                List.of("Total de Doacoes Registradas", String.valueOf(donations.size()), "Quantidade de registros de entrada de doacao"),
                List.of("Total de Atendimentos / Retiradas", String.valueOf(withdrawals.size()), "Quantidade de atendimentos com retiradas realizadas")
        );

        return new PdfReportBuilder("Relatorio Geral Consolidado", "Visao Executiva do Brecho Beneficente SANEM", headers, widths, rows).build();
    }
}
