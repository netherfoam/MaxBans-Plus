CREATE TABLE `Users` (
    id binary(16) NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    alias VARCHAR(50) NOT NULL,
    first_active DATETIME NOT NULL DEFAULT 0,
    last_active DATETIME NOT NULL DEFAULT 0
);

CREATE TABLE Mute (
    id binary(16) NOT NULL PRIMARY KEY,
    source_id binary(16) DEFAULT NULL REFERENCES Users(id),
    created DATETIME NOT NULL,
    expires_at DATETIME,
    revoked_at DATETIME,
    revoker_id binary(16) DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE TABLE Users_Mute (
    user_id binary(16) NOT NULL REFERENCES `Users`(id),
    mute_id binary(16) NOT NULL REFERENCES Mute(id),
    PRIMARY KEY(user_id, mute_id)
);

CREATE TABLE Ban (
    id binary(16) NOT NULL PRIMARY KEY,
    source_id binary(16) DEFAULT NULL REFERENCES Users(id),
    created DATETIME NOT NULL,
    expires_at DATETIME,
    revoked_at DATETIME,
    revoker_id binary(16) DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE TABLE Users_Ban (
    user_id binary(16) NOT NULL REFERENCES `Users`(id),
    ban_id binary(16) NOT NULL REFERENCES Ban(id),
    PRIMARY KEY(user_id, ban_id)
);

CREATE TABLE Address (
    host VARCHAR(50) NOT NULL PRIMARY KEY,
    ban_id binary(16) REFERENCES Ban(id),
    mute_id binary(16) REFERENCES Mute(id)
);

CREATE TABLE Address_Mute (
    host VARCHAR(15) NOT NULL REFERENCES Address(host),
    mute_id binary(16) NOT NULL REFERENCES Mute(id),
    PRIMARY KEY(host, mute_id)
);

CREATE TABLE Address_Ban (
    host VARCHAR(15) NOT NULL REFERENCES Address(host),
    ban_id binary(16) NOT NULL REFERENCES Ban(id),
    PRIMARY KEY(host, ban_id)
);

CREATE TABLE Address_User (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    user_id binary(16) NOT NULL REFERENCES Users(id),
    first_active DATETIME NOT NULL DEFAULT 0,
    last_active DATETIME NOT NULL DEFAULT 0,
    PRIMARY KEY(address, user_id)
);

CREATE TABLE Warning (
    id binary(16) NOT NULL PRIMARY KEY,
    user_id binary(16) NOT NULL REFERENCES Users(id),
    source_id binary(16) DEFAULT NULL REFERENCES Users(id),
    created DATETIME NOT NULL,
    expires_at DATETIME,
    revoked_at DATETIME,
    revoker_id binary(16) DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE INDEX users_alias ON Users(alias);
CREATE INDEX users_mute_user_id ON Users_Mute(user_id);
CREATE INDEX users_ban_user_id ON Users_Ban(user_id);

CREATE INDEX warning_user_id ON Warning(user_id);
