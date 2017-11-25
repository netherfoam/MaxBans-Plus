# MaxBans Plus
This is the MaxBans Plus repository. It contains the source code for compiling the MaxBans Plus
project. If you're looking for distributable JAR download, you can find it
[here](https://www.spigotmc.org/resources/maxbans-plus.41392/).

If you've got questions, feel free to raise an issue through the GitHub interface. If you're
looking for documentation, the [Spigot](https://www.spigotmc.org/resources/maxbans-plus.41392/)
project might be able to help you with:
* Permissions
* Commands
* Configuration

**Notice**: This is not `MaxBans` - This is `MaxBans Plus` -- it is a rewrite of the original 
project. If you came here from `Bukkit`, then you likely want to view the 
[old repository](https://github.com/netherfoam/MaxBans) which is maintained by Fabio12.

## Documentation
Here's a brief overview of the usage of the plugin.

### Installation
* Download the JAR from [here](https://www.spigotmc.org/resources/maxbans-plus.41392/)
* Place the JAR in `plugins/`
* Start your server
* Optional:
  * Stop the server
  * Edit the config files in `plugins/MaxBansPlus/`

The configuration explanation can be found [here](config.md).

### Commands
These are the commands available to anyone with permission. The permission convention is:
`maxbans.COMMAND_NAME`. For example, to allow a user to unmute someone, it's `maxbans.unmute`.

Here's the command list:
* /ban <user> [duration] [message]
* /mute <user> [duration] [message]
* /ipban <user|address> [duration] [message]
* /ipmute <user|address> [duration] [message]
* /warn <user> [message]
* /unban <user|address>
* /unmute <user|address>
* /kick <user> [message]
* /iplookup <user>
* /lockdown <ALL|JOIN|NEW|OFF> [message]
* /history [page] [user]

**Note**: Arguments inside `<>` are required, while ones inside `[]` are optional. 

#### Examples
Some examples of the above commands:
* /ban Merlin 5 hours Wizards are not allowed
* /mute Harry Silencio!
* /ipban Circe No witches, either!
* /warn Witch You look like a witch!

### Permissions
Here's the comprehensive list of permissions:

Permission | Description
-----------|------------
maxbans.ban|Mute and unmute commands
maxbans.mute|Ban and unban commands
maxbans.ipban|IP ban command
maxbans.ipmute|IP mute command
maxbans.warn|Warn command
maxbans.kick|Kick command
maxbans.iplookup|/lookup command
maxbans.broadcast|See broadcast messages, eg. User was banned. This is given by default.
maxbans.mod|See alerts when players try to join / chat but are denied because they're not allowed to
maxbans.silent|See when players use silent maxbans commands.
maxbans.history|View recent punishments dealt

### Notifications
MaxBans will notify you when things occur via chat. There are three types of permissions
to control this:

Broadcast: **maxbans.broadcast** is the permission node required to see server-wide messages from MaxBans.
For example, if a user was banned, everyone with this permission who is currently online will be told that
the user was banned. Broadcasting can be disabled by using the `-s` (`s` is for `silent`) flag in your command.
This is given to players by default, but can be taken away through permission setup.

Silent: **maxbans.silent** is the permission node required to see hidden messages whenever a command with `-s`
is run. Moderators usually want this.

Mod: **maxbans.mod** is the permission to alert moderators when activity occurs on the server. For example,
it will notify moderators when a banned player tries to join, or when a muted player tries to chat. This does
its best to not spam moderators, and to block messages that when they're too spammy.
