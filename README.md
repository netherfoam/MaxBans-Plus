# Configuration

The configuration explanation can be found [here](Config.md).

# Commands

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

#### Examples

* /ban Merlin 5 hours Wizards are not allowed
* /mute Harry Silencio!
* /ipban Circe No witches, either!
* /warn Witch You look like a witch!

# Permissions

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