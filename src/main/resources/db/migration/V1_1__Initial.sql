CREATE TABLE `Users` (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    first_active TIMESTAMP NOT NULL DEFAULT 0,
    last_active TIMESTAMP NOT NULL DEFAULT 0,
    ban_id UUID,
    mute_id UUID,
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

-- Now that these tables exist, we can create them
ALTER TABLE `Users`
    ADD FOREIGN KEY(ban_id) REFERENCES Ban(id);

ALTER TABLE `Users`
    ADD FOREIGN KEY(mute_id) REFERENCES Mute(id);

CREATE TABLE Address (
    host VARCHAR(50) NOT NULL PRIMARY KEY,
    ban_id UUID REFERENCES Ban(id),
    mute_id UUID REFERENCES Mute(id),
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
    reason TEXT DEFAULT NULL
);

-- TODO: Indices on all user_id, expires_at, source_id, name columns