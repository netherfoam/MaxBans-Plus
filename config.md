This is a walk-through for each configuration section in the MaxBansPlus `config.yml` file.

## Default
```yml
database:
   driver: "org.h2.Driver"
   url: "jdbc:h2:./plugins/MaxBansPlus/storage"
   username: "root"
   password: "password"
   show-sql: false

offline: false

warnings:
   penalties:
     1: "tell {{name}} This is your first warning"
     2: "tell {{name}} This is your final warning"
     3: "ban {{name}} 1 hour Warning limit reached:\n{{reason|No reason given}}"
   strikes: 3
   duration: "3 days"

chat-commands: []
```

We'll walk step by step through it below.

## Database
```yml
database:
   driver: "org.h2.Driver"
   url: "jdbc:h2:./plugins/MaxBansPlus/storage"
   username: "root"
   password: "password"
   show-sql: false
```

This specifes where bans are stored. By default, the H2 configuration stores the bans in the plugin folder under a flat-file
called `storage.mv.db`.

The options are as follows:

Option   | Description
---------|------------
driver   | The database driver to use. Common ones are `com.mysql.jdbc.Driver` and `org.h2.Driver`
url      | The JDBC url for the driver. Varies depending on the driver
username | The user to connect to the database with
password | The password to use for the database
show-sql | Whether SQL statements should be printed to the console. This is a debugging tool.


#### MySQL:
```yml
database:
   driver: "com.mysql.jdbc.Driver"
   url: "jdbc:mysql://localhost:3306/maxbansplus"
   username: "root"
   password: "password"
```

## Offline Mode

Offline mode allows you to override the way players are identified. A server which is online identifies players by their
`UUID`. An offline server identifies players by their `name`. Therefore, if this setting is incorrect, a player may be
able to bypass bans by changing their name.
```yml
offline: false
```
By default, your servers online/offline mode is used if this value is missing.

## Warnings
```yml
warnings:
   penalties:
     1: "tell {{name}} This is your first warning"
     2: "tell {{name}} This is your final warning"
     3: "ban {{name}} 1 hour Warning limit reached:\n{{reason|No reason given}}"
   strikes: 3
   duration: "3 days"
```

This section allows you to configure how warnings work. Each new warning a player acquires may trigger the console to
execute command(s).

Option | Description
-------|------------
penalties | The list of penalties after each strike is received against a player. The value may be a single command string, or list of commands as strings.
strikes | Maximum number of strikes. If this is omitted, the maximum penalty listed will be used instead. Eg, if 5 penalties exist, then this value will be 5 unless overridden. Must be >= maximum penalty.
duration | The time that warnings are valid for, before they're forgiven.


#### Example

This example gives the player a single warning where no penalty is incurred. The second warning strikes the player with
(with lightning, assuming you've got a plugin that has that command), kills them and tells them why they were killed.

The warnings after that, cause the player to be banned. Each new warning is more severe than the last.
```yml
warnings:
   penalties:
     2: ["strike {{name}}", "kill {{name}}", "tell {{name}} You've been punished for acquiring two warnings!"]
     3: "ban {{name}} 1 hour Warning limit reached:\n{{reason|No reason given}}"
     4: "ban {{name}} 1 day Warning limit reached:\n{{reason|No reason given}}"
     5: "ban {{name}} 1 week Warning limit reached:\n{{reason|No reason given}}"
   strikes: 5
   duration: "3 days"
```

```yml
chat-commands: []
```

This is the list of commands which are treated as chat commands. Eg, you may want to mark commands from other plugins in here,
or make the `/tell` command adhere to mutes.
