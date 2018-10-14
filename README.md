# PolarizedBot
[![CircleCI status](https://img.shields.io/circleci/project/github/PolarizedIons/PolarizedBot/master.svg?style=for-the-badge&logo=circleci)](https://circleci.com/gh/PolarizedIons/PolarizedBot/tree/master)
[![GitHub release](https://img.shields.io/github/release/polarizedions/polarizedbot.svg?style=for-the-badge&logo=github&logoColor=white&colorB=purple&label=latest)](https://github.com/PolarizedIons/PolarizedBot/releases/latest)
[![Add to Discord](https://img.shields.io/badge/discord-add%20bot-%237289DA.svg?style=for-the-badge&logo=discord)](https://discordapp.com/oauth2/authorize?client_id=382140849556815872&scope=bot)

A work in progress discord bot


## How to use

Click the `Add Bot` button above ([or click here](https://discordapp.com/oauth2/authorize?client_id=382140849556815872&scope=bot))
to add the bot to your discord guild. You must have the `Manage Server` permission to do so. 

The default command prefix is `!`, you can change it via `!guild set prefix <your prefix here>`. You must be a guild admin to do this. 
See the bot permissions explanation below for more details. 

There are also a few "autoresponders" active by default. These can be disabled via the `!guild disable responder ...` command. 
See the command explanation below for more details.


## Bot permissions

There are essentially three ranks that the bot assigns a discord user. `Bot Owner`, `Guild Admin`, and `Default` in that order.
Only some commands are available to `Default` rank users, while most commands, including the `!guild` command, can be performed
by the `Guild Admin` rank. 

The bot will automatically assign `Guild Admin` to the server owner when the bot joins. They can then assign ranks to other
members via the `!guild set rank ...` command. NOTE that the `Bot Owner` rank CANNOT be assigned via the command. It is a bot
config setting, and there may only be one `Bot Owner`.


## Commands

Below is a list of all commands the bot currently has, along with their usages, required permissions, and a brief description.

```java
// TODO
```


## Announcers

Announcers are ways to notify you that an event has happened, whether someone tweeted, went live, a game released an update, or more.
You can enable/disable announcers via the `!announce` command.

Currently, there are only two announcers implemented, more will be added in the future.
 
### Minecraft
Notifies you when a new Minecraft (Java Edition) snapshot or full release is made available to the launcher.

### GW2
Notifies you when a new update was posted to the Guild Wars 2 forum


## Autoresponders

Autoresponders are basically what they say, they are ways the bot automatically responds to certain messages, without
invoking a specific command. Autoresponders can be enabled/disabled via the `!guild disable responder ...` command. 

Currently there are two implemented, both enabled by default:

### Temperature
Converts temperatures (Celsius & Fahrenheit) to their respective counterparts. Useful when you have a server with people from
different background that use different units.

### Measurement
Converts between different units of measurement (Imperial <--> Metric), eg. in,ft,yd,mi <--> mm,cm,m,km. Useful when you
 have a server with people from different background that use different units.

## Setting it up yourself
Setting up your own version of the bot should be straight forward. Once you have cloned the repo, run it once to generate
the config files: 

```bash
java -jar polarizedbot-0.2.5-jar-with-dependencies.jar
``` 

This should have created a folder called `config`, with a file in it called `bot.json`. It will also have errored, saying 
you need to provide a owner id & bot token. The `bot.json` is what is referred to as the "Global config" (in contrast to "Guild configs",
which only apply to a specific server.)

Go ahead, and if you haven't already, create a Discord Bot by going [here](https://discordapp.com/developers/applications), 
and clicking "Create application". Once that is done, add a bot user to that application. Once that is done, you have the 
first piece of needed information: your bot token, labeled just as "token" on the bot page. 
 
Next, you'll need your user id. [Enable Developer Mode](https://support.discordapp.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-)
and copy your own User ID, as described in the help article.

Your config should now look something like this, where `...` indicates left out information:
```json
{
  "owner": "123456789987654321",
  "botToken": "123abc654fed.798hji963jfc",
  ...
}
```

You can go ahead and customize the other settings if you'd like, and start the bot again with the same command above.

### Commandline arguments
Commandline arguments are added to the end of the launching command to modify the bot's behaviors, eg:
```bash
java -jar polarizedbot-0.2.5-jar-with-dependencies.jar --config botdata
```

#### --log <log level>
Sets the log level
example: `--log debug`, `--log info`, `--log warn`, `--log error`

#### --config <directory name>
Sets the directory used for config files
example `--config ../../bot_config`, `--config botconfigstuff`
