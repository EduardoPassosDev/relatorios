package com.relatorios.relatorios.service;

import com.relatorios.relatorios.dto.FiltroRelatorioDTO;
import com.relatorios.relatorios.model.Venda;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final VendaService vendaService;

    @Value("${app.temp.dir:/tmp/relatorios}")
    private String tempDir;

    /**
     * Gera um arquivo XLS com as vendas filtradas
     * @param filtro Filtro aplicado
     * @return Arquivo XLS gerado
     * @throws IOException Se houver erro ao gerar o arquivo
     */
    public File gerarRelatorioXLS(FiltroRelatorioDTO filtro) throws IOException {
        log.info("Iniciando geração de relatório XLS com filtro: {}", filtro);

        // Busca todas as vendas com o filtro
        List<Venda> vendas = vendaService.buscarTodasVendasComFiltro(filtro);
        log.info("Total de vendas encontradas: {}", vendas.size());

        // Cria o workbook (arquivo Excel)
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relatório de Vendas");

        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        // Linha 0: Título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE VENDAS");
        titleCell.setCellStyle(headerStyle);

        // Linha 1: Filtros aplicados
        Row filterRow = sheet.createRow(1);
        Cell filterCell = filterRow.createCell(0);
        filterCell.setCellValue("Filtro: " + filtro.getDescricaoFiltro());

        // Linha 2: Data de geração
        Row dateRow = sheet.createRow(2);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        // Linha 4: Cabeçalhos das colunas
        Row headerRow = sheet.createRow(4);
        String[] headers = {"ID", "Vendedor", "Cliente", "Unidade", "Valor", "Data da Venda"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Linhas de dados
        int rowNum = 5;
        BigDecimal total = BigDecimal.ZERO;

        for (Venda venda : vendas) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(venda.getId());
            row.createCell(1).setCellValue(venda.getVendedor());
            row.createCell(2).setCellValue(venda.getCliente());
            row.createCell(3).setCellValue(venda.getUnidade());

            Cell valorCell = row.createCell(4);
            valorCell.setCellValue(venda.getValorVenda().doubleValue());
            valorCell.setCellStyle(currencyStyle);

            Cell dataCell = row.createCell(5);
            dataCell.setCellValue(venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            dataCell.setCellStyle(dateStyle);

            total = total.add(venda.getValorVenda());
        }

        // Linha de total
        Row totalRow = sheet.createRow(rowNum + 1);
        Cell totalLabelCell = totalRow.createCell(3);
        totalLabelCell.setCellValue("TOTAL:");
        totalLabelCell.setCellStyle(headerStyle);

        Cell totalValueCell = totalRow.createCell(4);
        totalValueCell.setCellValue(total.doubleValue());
        totalValueCell.setCellStyle(currencyStyle);

        // Ajusta largura das colunas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Salva o arquivo
        File directory = new File(tempDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filename = "relatorio_vendas_" + System.currentTimeMillis() + ".xlsx";
        File file = new File(directory, filename);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }

        workbook.close();

        log.info("Relatório XLS gerado com sucesso: {}", file.getAbsolutePath());
        return file;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("R$ #,##0.00"));
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}