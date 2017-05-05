CREATE TABLE `Users` (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    last_active TIMESTAMP NOT NULL DEFAULT 0
);

CREATE TABLE Address (
    host VARCHAR(50) NOT NULL PRIMARY KEY,
    last_active TIMESTAMP NOT NULL DEFAULT 0
);

CREATE TABLE Address_Users (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    user_id UUID NOT NULL REFERENCES Users(id),
    PRIMARY KEY(address, user_id)
);

CREATE TABLE Mute (
    id UUID NOT NULL PRIMARY KEY,
    source_id UUID DEFAULT NULL REFERENCES Users(id),
    created TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    reason TEXT DEFAULT NULL
);

CREATE TABLE Ban (
    id UUID NOT NULL PRIMARY KEY,
    source_id UUID DEFAULT NULL REFERENCES Users(id),
    created TIMESTAMP NOT NULL,
    expires_at TIMESTAMP,
    reason TEXT DEFAULT NULL
);

CREATE TABLE Address_Ban (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    ban_id UUID NOT NULL REFERENCES Ban(id),
    PRIMARY KEY (address, ban_id)
);

CREATE TABLE Address_Mute (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    mute_id UUID NOT NULL REFERENCES Mute(id),
    PRIMARY KEY (address, mute_id)
);

CREATE TABLE Users_Ban (
    user_id UUID NOT NULL REFERENCES Users(id),
    ban_id UUID NOT NULL REFERENCES Ban(id),
    PRIMARY KEY (user_id, ban_id)
);

CREATE TABLE Users_Mute (
    user_id UUID NOT NULL REFERENCES `Users`(id),
    mute_id UUID NOT NULL REFERENCES Mute(id),
    PRIMARY KEY (user_id, mute_id)
);
