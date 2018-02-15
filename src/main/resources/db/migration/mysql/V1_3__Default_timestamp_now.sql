--
-- There was previously a bug in MySQL 5.7 where timestamps defaulted to zero, but MySQL 5.7 doesn't support that.
-- So, this version changes the defaults to now() instead for every MySQL version. There is a workaround inside of
-- FlywayUtil.java which modifies V1 (Initial) so that it repairs the incorrect checksum on V1 if needed, so that
-- it does not a) fail migration and b) fail to execute because 0 is an invalid timestamp
--

ALTER TABLE Users MODIFY COLUMN first_active DATETIME NOT NULL DEFAULT now();
ALTER TABLE Users MODIFY COLUMN last_active DATETIME NOT NULL DEFAULT now();

ALTER TABLE Address_User MODIFY COLUMN first_active DATETIME NOT NULL DEFAULT now();
ALTER TABLE Address_User MODIFY COLUMN last_active DATETIME NOT NULL DEFAULT now();
