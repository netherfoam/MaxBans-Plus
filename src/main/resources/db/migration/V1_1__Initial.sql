CREATE TABLE `Users` (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    alias VARCHAR(50) NOT NULL,
    first_active TIMESTAMP NOT NULL DEFAULT 0,
    last_active TIMESTAMP NOT NULL DEFAULT 0
);

CREATE TABLE Mute (
    id UUID NOT NULL PRIMARY KEY,
    source_id UUID DEFAULT NULL REFERENCES Users(id),
    created TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    revoker_id UUID DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE TABLE Users_Mute (
    user_id UUID NOT NULL REFERENCES `Users`(id),
    mute_id UUID NOT NULL REFERENCES Mute(id),
    PRIMARY KEY(user_id, mute_id)
);

CREATE TABLE Ban (
    id UUID NOT NULL PRIMARY KEY,
    source_id UUID DEFAULT NULL REFERENCES Users(id),
    created TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    revoker_id UUID DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE TABLE Users_Ban (
    user_id UUID NOT NULL REFERENCES `Users`(id),
    ban_id UUID NOT NULL REFERENCES Ban(id),
    PRIMARY KEY(user_id, ban_id)
);

CREATE TABLE Address (
    host VARCHAR(50) NOT NULL PRIMARY KEY,
    ban_id UUID REFERENCES Ban(id),
    mute_id UUID REFERENCES Mute(id)
);

CREATE TABLE Address_Mute (
    host VARCHAR(15) NOT NULL REFERENCES Address(host),
    mute_id UUID NOT NULL REFERENCES Mute(id),
    PRIMARY KEY(host, mute_id)
);

CREATE TABLE Address_Ban (
    host VARCHAR(15) NOT NULL REFERENCES Address(host),
    ban_id UUID NOT NULL REFERENCES Ban(id),
    PRIMARY KEY(host, ban_id)
);

CREATE TABLE Address_User (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    user_id UUID NOT NULL REFERENCES Users(id),
    first_active TIMESTAMP NOT NULL DEFAULT 0,
    last_active TIMESTAMP NOT NULL DEFAULT 0,
    PRIMARY KEY(address, user_id)
);

CREATE TABLE Warning (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES Users(id),
    source_id UUID DEFAULT NULL REFERENCES Users(id),
    created TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    revoked_at TIMESTAMP,
    revoker_id UUID DEFAULT NULL REFERENCES Users(id),
    reason TEXT DEFAULT NULL
);

CREATE INDEX users_alias ON Users(alias);
CREATE INDEX users_mute_user_id ON Users_Mute(user_id);
CREATE INDEX users_ban_user_id ON Users_Ban(user_id);

CREATE INDEX warning_user_id ON Warning(user_id);
