package com.javalovers.core.report.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PdfReportBuilder {

    private final PDDocument document;
    private final String title;
    private final String subtitle;
    private final List<String> headers;
    private final List<Float> columnWidths;
    private final List<List<String>> rows;
    
    private final PDType1Font fontRegular;
    private final PDType1Font fontBold;

    private static final float PAGE_WIDTH = 841.89f; // A4 Landscape
    private static final float PAGE_HEIGHT = 595.28f;
    private static final float MARGIN_LEFT = 40f;
    private static final float MARGIN_TOP = 40f;
    private static final float MARGIN_BOTTOM = 40f;
    private static final float ROW_HEIGHT = 20f;
    private static final float HEADER_HEIGHT = 24f;

    public PdfReportBuilder(String title, String subtitle, List<String> headers, List<Float> columnWidths, List<List<String>> rows) {
        this.document = new PDDocument();
        this.title = title;
        this.subtitle = subtitle;
        this.headers = headers;
        this.columnWidths = columnWidths;
        this.rows = rows;
        this.fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        this.fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    }

    public byte[] build() throws IOException {
        int pageNumber = 1;
        PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
        document.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(document, page);
        float currentY = PAGE_HEIGHT - MARGIN_TOP;

        // Desenhar Cabeçalho do Relatório
        currentY = drawReportHeader(cs, currentY, pageNumber);

        // Desenhar Cabeçalho da Tabela
        drawTableHeader(cs, currentY);
        currentY -= HEADER_HEIGHT;

        // Desenhar Linhas
        if (rows.isEmpty()) {
            cs.setNonStrokingColor(120 / 255f, 120 / 255f, 120 / 255f);
            cs.beginText();
            cs.setFont(fontRegular, 10);
            cs.newLineAtOffset(MARGIN_LEFT + 8, currentY - 18);
            cs.showText("Nenhum registro encontrado para os filtros selecionados.");
            cs.endText();
        } else {
            boolean alternate = false;
            for (List<String> row : rows) {
                if (currentY - ROW_HEIGHT < MARGIN_BOTTOM) {
                    // Fechar página atual
                    cs.close();

                    // Criar nova página
                    pageNumber++;
                    page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    currentY = PAGE_HEIGHT - MARGIN_TOP;

                    // Mini cabeçalho em páginas seguintes
                    currentY = drawSubsequentHeader(cs, currentY, pageNumber);
                    drawTableHeader(cs, currentY);
                    currentY -= HEADER_HEIGHT;
                }

                drawTableRow(cs, currentY, row, alternate);
                currentY -= ROW_HEIGHT;
                alternate = !alternate;
            }
        }

        cs.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }

    private float drawReportHeader(PDPageContentStream cs, float y, int pageNum) throws IOException {
        // Faixa de topo azul
        cs.setNonStrokingColor(41 / 255f, 128 / 255f, 185 / 255f); // #2980b9
        cs.addRect(MARGIN_LEFT, y - 26, PAGE_WIDTH - 2 * MARGIN_LEFT, 28);
        cs.fill();

        // Título da Organização e Título do Relatório
        cs.setNonStrokingColor(1f, 1f, 1f);
        cs.beginText();
        cs.setFont(fontBold, 14);
        cs.newLineAtOffset(MARGIN_LEFT + 12, y - 18);
        cs.showText("SANEM - " + sanitize(title).toUpperCase());
        cs.endText();

        y -= 36;

        // Data de geração e subtítulo
        cs.setNonStrokingColor(80 / 255f, 80 / 255f, 80 / 255f);
        cs.beginText();
        cs.setFont(fontRegular, 9);
        cs.newLineAtOffset(MARGIN_LEFT, y);
        String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        cs.showText("Gerado em: " + dateStr + (subtitle != null && !subtitle.isEmpty() ? " | " + sanitize(subtitle) : ""));
        cs.endText();

        // Paginação
        cs.beginText();
        cs.setFont(fontRegular, 9);
        cs.newLineAtOffset(PAGE_WIDTH - MARGIN_LEFT - 70, y);
        cs.showText("Pagina " + pageNum);
        cs.endText();

        return y - 16;
    }

    private float drawSubsequentHeader(PDPageContentStream cs, float y, int pageNum) throws IOException {
        cs.setNonStrokingColor(80 / 255f, 80 / 255f, 80 / 255f);
        cs.beginText();
        cs.setFont(fontBold, 10);
        cs.newLineAtOffset(MARGIN_LEFT, y);
        cs.showText("SANEM - " + sanitize(title) + " (cont.)");
        cs.endText();

        cs.beginText();
        cs.setFont(fontRegular, 9);
        cs.newLineAtOffset(PAGE_WIDTH - MARGIN_LEFT - 70, y);
        cs.showText("Pagina " + pageNum);
        cs.endText();

        return y - 14;
    }

    private void drawTableHeader(PDPageContentStream cs, float y) throws IOException {
        // Fundo cinza escuro para cabeçalho da tabela
        cs.setNonStrokingColor(52 / 255f, 73 / 255f, 94 / 255f); // #34495e
        cs.addRect(MARGIN_LEFT, y - HEADER_HEIGHT, PAGE_WIDTH - 2 * MARGIN_LEFT, HEADER_HEIGHT);
        cs.fill();

        cs.setNonStrokingColor(1f, 1f, 1f);
        float currentX = MARGIN_LEFT;

        for (int i = 0; i < headers.size(); i++) {
            float colWidth = columnWidths.get(i);
            String text = truncate(headers.get(i), colWidth - 8, fontBold, 9);

            cs.beginText();
            cs.setFont(fontBold, 9);
            cs.newLineAtOffset(currentX + 5, y - 16);
            cs.showText(text);
            cs.endText();

            currentX += colWidth;
        }
    }

    private void drawTableRow(PDPageContentStream cs, float y, List<String> row, boolean alternate) throws IOException {
        // Fundo zebrado suave
        if (alternate) {
            cs.setNonStrokingColor(245 / 255f, 247 / 255f, 250 / 255f);
            cs.addRect(MARGIN_LEFT, y - ROW_HEIGHT, PAGE_WIDTH - 2 * MARGIN_LEFT, ROW_HEIGHT);
            cs.fill();
        }

        // Linha inferior divisória
        cs.setStrokingColor(230 / 255f, 230 / 255f, 230 / 255f);
        cs.setLineWidth(0.5f);
        cs.moveTo(MARGIN_LEFT, y - ROW_HEIGHT);
        cs.lineTo(PAGE_WIDTH - MARGIN_LEFT, y - ROW_HEIGHT);
        cs.stroke();

        cs.setNonStrokingColor(40 / 255f, 40 / 255f, 40 / 255f);
        float currentX = MARGIN_LEFT;

        for (int i = 0; i < headers.size(); i++) {
            float colWidth = columnWidths.get(i);
            String rawVal = i < row.size() ? row.get(i) : "";
            String text = truncate(rawVal, colWidth - 8, fontRegular, 8.5f);

            cs.beginText();
            cs.setFont(fontRegular, 8.5f);
            cs.newLineAtOffset(currentX + 5, y - 14);
            cs.showText(text);
            cs.endText();

            currentX += colWidth;
        }
    }

    public static String sanitize(String text) {
        if (text == null) return "-";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").replaceAll("[^\\x20-\\x7E]", " ").trim();
    }

    private static String truncate(String text, float maxWidth, PDType1Font font, float fontSize) {
        String clean = sanitize(text);
        try {
            float width = font.getStringWidth(clean) / 1000f * fontSize;
            if (width <= maxWidth) {
                return clean;
            }
            while (clean.length() > 3) {
                clean = clean.substring(0, clean.length() - 1);
                if ((font.getStringWidth(clean + "...") / 1000f * fontSize) <= maxWidth) {
                    return clean + "...";
                }
            }
            return clean;
        } catch (Exception e) {
            return clean.length() > 30 ? clean.substring(0, 27) + "..." : clean;
        }
    }
}
