package com.javalovers.core.appuser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... args) throws Exception {
    log.info("Iniciando verificação do banco de dados...");

    // Verificar se existem usuários
    Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM app_user", Integer.class);

    if (userCount == 0) {
      log.info("Banco vazio detectado. Executando scripts de inicialização...");
      executeScript("db/script-create-tables.sql");
      executeScript("db/script-insert-values.sql");
      log.info("Scripts de inicialização executados com sucesso!");
    } else {
      log.info("Banco já possui {} usuários. Pulando inicialização.", userCount);
    }

    // Verificar e adicionar coluna issue_date na tabela card se não existir
    checkAndAddIssueDateColumn();

    // RF16: Verificar e adicionar colunas na tabela item se não existirem
    checkAndAddRF16Columns();
  }

  private void checkAndAddIssueDateColumn() {
    try {
      // Verificar se a coluna issue_date existe na tabela card
      String checkColumnSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
          "WHERE TABLE_SCHEMA = DATABASE() " +
          "AND TABLE_NAME = 'card' " +
          "AND COLUMN_NAME = 'issue_date'";

      Integer columnExists = jdbcTemplate.queryForObject(checkColumnSql, Integer.class);

      if (columnExists == null || columnExists == 0) {
        log.info("Coluna issue_date não encontrada na tabela card. Adicionando...");
        jdbcTemplate.execute("ALTER TABLE card ADD COLUMN issue_date TIMESTAMP(6) NULL");
        log.info("Coluna issue_date adicionada com sucesso!");
      } else {
        log.debug("Coluna issue_date já existe na tabela card.");
      }
    } catch (Exception e) {
      log.warn("Erro ao verificar/adicionar coluna issue_date: {}", e.getMessage());
      // Não lançar exceção para não impedir a inicialização da aplicação
    }
  }

  private void checkAndAddRF16Columns() {
    try {
      checkAndAddColumn("item", "category_id", "INT NULL");
      checkAndAddColumn("item", "size", "VARCHAR(50) NULL");
      checkAndAddColumn("item", "conservation_state", "VARCHAR(50) NULL");
      checkAndAddColumn("item", "gender", "VARCHAR(50) NULL");

      // Verificar e adicionar foreign key fk_item_category se a coluna existir
      String checkFkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS " +
          "WHERE CONSTRAINT_SCHEMA = DATABASE() " +
          "AND TABLE_NAME = 'item' " +
          "AND CONSTRAINT_NAME = 'fk_item_category' " +
          "AND CONSTRAINT_TYPE = 'FOREIGN KEY'";

      Integer fkExists = jdbcTemplate.queryForObject(checkFkSql, Integer.class);
      if (fkExists == null || fkExists == 0) {
        try {
          jdbcTemplate.execute("ALTER TABLE item ADD CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON UPDATE RESTRICT ON DELETE SET NULL");
          log.info("Foreign key fk_item_category adicionada com sucesso!");
        } catch (Exception e) {
          log.warn("Aviso ao adicionar foreign key fk_item_category: {}", e.getMessage());
        }
      }
    } catch (Exception e) {
      log.warn("Erro ao verificar/adicionar colunas do RF16: {}", e.getMessage());
    }
  }

  private void checkAndAddColumn(String tableName, String columnName, String columnType) {
    try {
      String checkColumnSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
          "WHERE TABLE_SCHEMA = DATABASE() " +
          "AND TABLE_NAME = ? " +
          "AND COLUMN_NAME = ?";

      Integer columnExists = jdbcTemplate.queryForObject(checkColumnSql, Integer.class, tableName, columnName);

      if (columnExists == null || columnExists == 0) {
        log.info("Coluna {} não encontrada na tabela {}. Adicionando...", columnName, tableName);
        jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        log.info("Coluna {} adicionada com sucesso na tabela {}!", columnName, tableName);
      }
    } catch (Exception e) {
      log.warn("Erro ao verificar/adicionar coluna {} na tabela {}: {}", columnName, tableName, e.getMessage());
    }
  }

  private void executeScript(String scriptPath) {
    try {
      String script = Files.readString(Paths.get(scriptPath));
      String[] statements = script.split(";");

      for (String statement : statements) {
        statement = statement.trim();
        if (!statement.isEmpty() && !statement.startsWith("--")) {
          try {
            jdbcTemplate.execute(statement);
          } catch (Exception e) {
            log.warn("Erro ao executar statement: {}", statement.substring(0, Math.min(50, statement.length())));
          }
        }
      }
    } catch (IOException e) {
      log.error("Erro ao ler script: {}", scriptPath, e);
    }
  }
}
