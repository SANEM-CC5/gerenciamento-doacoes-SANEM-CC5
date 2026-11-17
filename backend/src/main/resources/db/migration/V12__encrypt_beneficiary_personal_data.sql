-- =====================================================================
-- V12: Criptografia de dados pessoais de beneficiários (RNF12 / LGPD).
--
-- CPF/CRNM, telefone e dados socioeconômicos passam a ser gravados
-- criptografados pela aplicação (AES-256-GCM, formato "enc:v1:...").
--
-- 1. cpf e phone viram TEXT: o valor cifrado é maior que o original.
-- 2. A restrição UNIQUE em cpf deixa de funcionar com criptografia
--    (o mesmo CPF gera cifras diferentes). Ela é substituída por uma
--    restrição UNIQUE no índice cego cpf_hash (HMAC-SHA256 do documento).
--
-- Os registros existentes em texto puro são criptografados pela própria
-- aplicação na inicialização (PersonalDataEncryptionMigrator), pois a
-- chave de criptografia não deve ficar no banco nem nos scripts SQL.
-- =====================================================================

ALTER TABLE beneficiary ALTER COLUMN cpf TYPE TEXT;
ALTER TABLE beneficiary ALTER COLUMN phone TYPE TEXT;

ALTER TABLE beneficiary DROP CONSTRAINT IF EXISTS uq_beneficiary_cpf;

ALTER TABLE beneficiary ADD COLUMN cpf_hash VARCHAR(64) NULL;
ALTER TABLE beneficiary ADD CONSTRAINT uq_beneficiary_cpf_hash UNIQUE (cpf_hash);

COMMENT ON COLUMN beneficiary.cpf IS 'RNF12: documento criptografado (AES-256-GCM, prefixo enc:v1:)';
COMMENT ON COLUMN beneficiary.phone IS 'RNF12: telefone criptografado (AES-256-GCM, prefixo enc:v1:)';
COMMENT ON COLUMN beneficiary.socioeconomic_data IS 'RNF12: dados socioeconômicos criptografados (podem conter dado sensível)';
COMMENT ON COLUMN beneficiary.cpf_hash IS 'RNF12: índice cego HMAC-SHA256 do documento normalizado (busca exata e unicidade)';
