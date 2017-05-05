CREATE TABLE `Users` (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
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

-- TODO: Alter users, make a FK to mute and ban

CREATE TABLE Address (
    host VARCHAR(50) NOT NULL PRIMARY KEY,
    last_active TIMESTAMP NOT NULL DEFAULT 0, -- TODO: make this a property of the relation
    ban_id UUID NOT NULL REFERENCES Ban(id),
    mute_id UUID NOT NULL REFERENCES Mute(id),
);

-- TODO: Will want a timestamp field on this
CREATE TABLE Address_Users (
    address VARCHAR(15) NOT NULL REFERENCES Address(host),
    user_id UUID NOT NULL REFERENCES Users(id),
    PRIMARY KEY(address, user_id)
);




