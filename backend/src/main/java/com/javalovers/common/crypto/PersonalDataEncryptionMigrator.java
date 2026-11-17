package com.javalovers.common.crypto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

/**
 * RNF12 – Criptografa, na inicialização, os dados pessoais de beneficiários que ainda estão
 * em texto puro (registros gravados antes do RNF12 e o seed de desenvolvimento).
 *
 * É idempotente: na segunda execução não encontra nada para migrar. Roda depois do Flyway
 * (que cria a coluna cpf_hash na V12) e numa única transação. Nunca registra os valores em log.
 */
@Slf4j
@Component
@Order(10)
@RequiredArgsConstructor
public class PersonalDataEncryptionMigrator implements ApplicationRunner {

    private static final String PENDING_QUERY =
            "SELECT beneficiary_id, cpf, phone, socioeconomic_data FROM beneficiary " +
            "WHERE cpf_hash IS NULL " +
            "   OR cpf NOT LIKE 'enc:v1:%' " +
            "   OR (phone IS NOT NULL AND phone NOT LIKE 'enc:v1:%') " +
            "   OR (socioeconomic_data IS NOT NULL AND socioeconomic_data NOT LIKE 'enc:v1:%')";

    private static final String UPDATE_SQL =
            "UPDATE beneficiary SET cpf = ?, phone = ?, socioeconomic_data = ?, cpf_hash = ? " +
            "WHERE beneficiary_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private final DataEncryptionService encryption;

    @Override
    public void run(ApplicationArguments args) {
        Integer migrated = transactionTemplate.execute(status -> {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(PENDING_QUERY);
            for (Map<String, Object> row : rows) {
                // decrypt() devolve o próprio valor quando ele ainda está em texto puro
                String cpf = encryption.decrypt((String) row.get("cpf"));
                String phone = encryption.decrypt((String) row.get("phone"));
                String socioeconomicData = encryption.decrypt((String) row.get("socioeconomic_data"));

                jdbcTemplate.update(UPDATE_SQL,
                        encryption.encrypt(cpf),
                        encryption.encrypt(phone),
                        encryption.encrypt(socioeconomicData),
                        encryption.documentIndex(cpf),
                        row.get("beneficiary_id"));
            }
            return rows.size();
        });

        if (migrated != null && migrated > 0) {
            log.info("RNF12: dados pessoais de {} beneficiário(s) foram criptografados.", migrated);
        } else {
            log.debug("RNF12: nenhum dado pessoal de beneficiário pendente de criptografia.");
        }
    }
}
