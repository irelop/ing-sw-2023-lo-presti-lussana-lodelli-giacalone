#Prova Finale Software Engineering 2022/2023
![](https://www.craniocreations.it/storage/media/products/54/112/My_Shelfie_box_ITA-ENG.png)
>You have just taken home your new bookshelf and now it is time to put your favorite items in the display: books, boardgames, portraits... Who will show the best organized shelfie?

##My Shelfie
This repository contains the Java Implementation of the game board *My Shelfie* (Cranio Creations Srl), developed for the Final Project 2022/2023 of the Software Engineering course.


## Group 25 [SAN PIETRO]

Members :fa-user: | Github Nickname :fa-github: | Institutional Mail :fa-envelope-o:
------------- | ------------------| -------------------
Irene Lo Presti  | @irelop | irene.lopresti@mail.polimi.it
Matteo Lussana | @matteolussana | matteo.lussana@mail.polimi.it
Riccardo Lodelli | @ilrechh | riccardo.lodelli@mail.polimi.it
Andrea Giacalone | @andreagiacalone| andrea1.giacalone@mail.polimi.it


##Project Features
Feature | Impl
------------- | ------------------
Complete Rules |:tw-2705:
Socket |:tw-2705:
RMI |:tw-2705:
TUI |:tw-2705:
GUI |:tw-2705:
Multiple Parallel Games |:tw-2705:
Persistence |:tw-2705:
Disconnection Resilience |:tw-2705:
Chat |:tw-2705:


##Project Details
:fa-book: **Complete Rules** : the game implements the complete set of rules, including the 
employment of common goal cards, whose achievement gives the player the possibility
to increase even more his/her personal score.
*DISCLAIMER* : these goals are implemented following the italian rules available on the [Cranio Website](https://www.craniocreations.it/prodotto/my-shelfie)

:fa-exchange: **Socket** : the client-server communication is implemented employing
the TCP/IP Stream Socket network protocol.
*DISCLAIMER* : in this project the number of port is already set to 9999.

:fa-random:  **RMI** : the client-server communication is also implemented employing the RMI network protocol.
*DISCLAIMER* : in this project the number of port where the RMI is already set to 1099.

:fa-keyboard-o:  **TUI** : the user can choose to interact with a Text User Interface.

:fa-gamepad:  **GUI** : the user can choose to interact with a Graphic User Interface implemented with *JavaFx*.

Following here, the description of each FA.

:fa-road:  **Multiple Parallel Games** : the server can manage multiple parallel games, each one separated from the others. When a player wants to connect to game whose lobby is already full, the server will create a new one.

:fa-floppy-o:  **Persistence** : when starting the game and at the end of each turn, the server saves the game status on txt files, in particular:
- a txt file is created and managed for each player.
- a txt file is created and managed for each game.
If a player disconnects, he/she can reconnect and retrieve his/her game status loading this data.
For further info, you can check it out how game data are stored [here](https://github.com/irelop/ing-sw-2023-lo-presti-lussana-lodelli-giacalone/blob/main/MyShelfie/info/safeTxtExample.txt)

*DISCLAIMER* : only when a game finishes, all txt files related to that game are deleted, otherwise the server continues to keep track of them. This feature has the purpose to retrieve players and games after a crash of the server or a disconnection of the player.

:fa-heartbeat:  **Disconnection Resilience** : when connection drops or the client crashes, players can reconnected to their game and continue from where they left. In the meanwhile, other players can still play skipping the disconnected player rounds.
If there is only player connected to the game, a countdown timer of 30 seconds will be set and, if no reconnections notifies it, the game will end declaring the last player connected as the winner.

:fa-comments:  **Chat** : each one of the connected players can send public or private messages to the others.
*DISCLAIMER* : due to formatting reasons and limits of the TUI, the chat can be accessed only at the end of a turn, with a maximum limit of 3 available messages which can be send, avoiding possible time wasting of a player. In GUI, this processed is implemented in real-time and messages can be sent and shown in every moment.

##JAR Instructions

###MyShelfie_server.jar
In order to properly execute the **MyShelfie_server.jar**, you can insert in the command line console the following command:
`java -jar .\MyShelfie_server.jar`
The server will be ready to listen for new connections.


###MyShelfie_client.jar
*DISCLAIMER* : in order to dive into a full good experience with the **Text User Interface** we suggest the user to follow these few steps:
- Run intl.cpl (which opens the regional settings in Control Panel);
- Activate the *Administrative* tab;
- Click on the *Change System Locale* button;
- Enable the *Unicode UTF-8* option.
This sets the ANSI code page to 65001, the UTF-8 code page in order to make all future console window show correct colours and symbols.
For further informations, check it out [here](https://stackoverflow.com/questions/57131654/using-utf-8-encoding-chcp-65001-in-command-prompt-windows-powershell-window)
We strongly recommend the use of a more recent version of PowerShell, changing the background colour to a different one than black in order to experience a proper visualization.

You are now able to execute your **MyShelfie_client.jar** through the comand:
`java -jar .\MyShelfie_client.jar`
Enjoy your game!


##Credits 
We would like to thank our professor Pierluigi San Pietro, our project managers Alessandro Bertani and Tommaso Paladini, our tutors Silvia Marino and Patrick Niantcho, who have helped us during the project development.
