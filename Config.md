The default configuration:

```yml
database:
   driver: "org.h2.Driver"
   url: "jdbc:h2:./plugins/MaxBansPlus/storage"
   user: "root"
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

We'll walk step by step through it below. Firstly, getting set up with a database:

```yml
database:
   driver: "org.h2.Driver"
   url: "jdbc:h2:./plugins/MaxBansPlus/storage"
   user: "root"
   password: "password"
   show-sql: false
```

The options are as follows:
Option | Description
-------|------------
driver | The database driver to use. Common ones are `com.mysql.jdbc.Driver` and `org.h2.Driver`
url    | The JDBC url for the driver. Varies depending on the driver
user   | The user to connect to the database with
password | The password to use for the database
show-sql | Whether SQL statements should be printed to the console. This is a debugging tool.

```yml
offline: false
```

This allows you to override your server's online/offline mode. By default, your servers online/offline mode is if this
value is missing. Setting it to true means that player names will uniquely identify people, setting it to false means
that player UUID's will uniquely identify people.

```yml
warnings:
   penalties:
     1: "tell {{name}} This is your first warning"
     2: "tell {{name}} This is your final warning"
     3: "ban {{name}} 1 hour Warning limit reached:\n{{reason|No reason given}}"
   strikes: 3
   duration: "3 days"
```

Option | Description
-------|------------
penalties | The list of penalties after each strike is received against a player. The value may be a single command string, or list of commands as strings.
strikes | Maximum number of strikes. If this is omitted, the maximum penalty listed will be used instead. Eg, if 5 penalties exist, then this value will be 5 unless overridden. Must be >= maximum penalty.
duration | The time that warnings are valid for, before they're forgiven.

```yml
chat-commands: []
```

This is the list of commands which are treated as chat commands. Eg, you may want to mark commands from other plugins in here,
or make the `/tell` command adhere to mutes.