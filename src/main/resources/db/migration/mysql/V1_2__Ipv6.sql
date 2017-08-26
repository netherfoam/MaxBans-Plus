--
-- Enables restrictions on IPV6 addresses. Very little is actually needed to support IPV6 on our end.
-- The API has always reported, ambiguously, IPV6 or IPV4 addresses and tried to store them.
-- In order to successfully store IPV6, we need 39 characters instead of 15.
--

ALTER TABLE Address MODIFY COLUMN host varchar(39);
ALTER TABLE Address_Ban MODIFY COLUMN host varchar(39);
ALTER TABLE Address_Mute MODIFY COLUMN host varchar(39);
ALTER TABLE Address_User MODIFY COLUMN address varchar(39);