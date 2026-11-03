ALTER TABLE item
    ADD COLUMN size VARCHAR(30) NULL,
    ADD COLUMN gender VARCHAR(30) NULL,
    ADD COLUMN type VARCHAR(80) NULL,
    ADD COLUMN condition_status VARCHAR(40) NULL,
    ADD COLUMN location VARCHAR(120) NULL;

UPDATE item
SET location = 'Medianeira'
WHERE location IS NULL;
