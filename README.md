# Java parking system

>Project's specifications are taken from the 2019 exam of the course "Sistemi Distribuiti" and can be found in the file assignment.pdf in italian languages.

This project implements a simple parking system that allows motorists to park and retrieve the car automatically by issuing a unique ticket. The project was carried out in 3 different versions 

## Basic version 

It is the basic version of the program. It allows customers to park their car in one of the car parks managed by the platform. In this case customers does not know all the possible parking spaces but only the one in which he intends to park. The class [Automobilista](./CarParks/src/carParks/Automobilista.java) is made as a Thread and the concurrent with other object of the same class is managed.<br />
In the event of a parking request, if the parking is full, the driver will remain waiting for a space to become available 
  * [Car](./CarParks/src/carParks/Automobile.java): Contains the basic properties of a car. Each driver as a car!
  * [Driver](./CarParks/src/carParks/Automobilista.java): it acts as a thread and allows you to park and cyclically pick up a car in a state of concurrency with other threads of the same type. In this version the driver has his own specific parking instance that acts as a destination and with which he interacts directly.
  * [Parking place](./CarParks/src/carParks/Posto.java): Contains the basic properties of each single place of a parking site. Each place in this version could be taken or not.
  * [Parking system](./CarParks/src/carParks/Parcheggio.java): Contains a list of parking place, allows to manage the parking and pick up of cars based on available seats.
  * [Valet](./CarParks/src/carParks/Parcheggiatore.java): Manages the actual delivery and pick up of the car. A car park can have one or more valets.
  * [main](./CarParks/src/carParks/MainVersioneBase.java): Simulates the use of the classes just described according to the basic version of the program 

## Upgrade 1

It introduces the parking management server and therefore establishes a socket between client and server for communication. In addition to the methods of the basic version, it allows customers to know which car parks are managed by the system, know the remaining free spaces and select a car park from those available. Moreover manages the change of parking availability by communicate new information about place to the server. 

#### client side:
  * [Car](./CarParks/src/carParks/Automobile.java): Contains the basic properties of a car. Each driver as a car. (As for basic version)
  * [Parking place](./CarParks/src/carParks/Posto.java): Contains the basic properties of each single place of a parking site. Each place in this version could be taken or not. (As for basic version)
  * [Valet](./CarParks/src/carParks/Parcheggiatore.java): Manages the actual delivery and pick up of the car. A car park can have one or more valets. (As for basic version)
  * [Driver](./CarParks/src/carParks/Automobilista.java): In this version it works as a client and communicates with the server through a socket to park the car, know the parking spaces and available spaces. Unlike the previous version, the driver does NOT contain the instance of the chosen parking space (Start from row 140)
  * [Parking system](./CarParks/src/carParks/Parcheggio.java): Contains a list of parking place, allows to manage the parking and pick up of cars based on available seats. It is managed by the server in this case.
  * [main](./CarParks/src/carParks/MainUpgrade1.java): Simulates the use of the classes just described according to the upgrade_1 version of the program.
#### server side:  
  * [server](./CarParks/src/server/Server.java): Start a new server socket and accept new client connection. Each client request is delegated to the supporting class [Client handler](./CarParks/src/server/Server.java).
  * [Client handler](./CarParks/src/server/Server.java): manages the requests that arrive from clients. 
  * [Parking system](./CarParks/src/server/GestioneParcheggi.java): It contains the list of available parking spaces and implements the functions for their management.
  
## Upgrade 2

Starting from upgrade 1, it allows the driver not only to know the parking spaces and available spaces but also to reserve a parking place. After a place has been reserved, it will already be occupied by other motorists, it is also possible to cancel the reservation of a seat. 

#### client side:
  * [Car](./CarParks/src/carParks/Automobile.java): Contains the basic properties of a car. Each driver as a car. (As for basic version)
  * [Valet](./CarParks/src/carParks/Parcheggiatore.java): Manages the actual delivery and pick up of the car. A car park can have one or more valets. (As for basic version)
  * [Parking place](./CarParks/src/carParks/Posto.java): Contains the basic properties of each single place of a parking site. Each place in this version could be taken or not and also reserved or not!
  * [Driver](./CarParks/src/carParks/Automobilista.java): Same as upgrade 1 but allows you to manage parking place reservations.
  * [Parking system](./CarParks/src/carParks/Parcheggio.java): Same as upgrade 1 but allows you to manage parking place reservations.
  * [main](./CarParks/src/carParks/MainUpgrade2.java): Simulates the use of the classes just described according to the upgrade_1 version of the program.
#### server side:  
  * [server](./CarParks/src/server/Server.java): Same as upgrade 1
  * [Client handler](./CarParks/src/server/Server.java): Same as upgrade 1 but allows you to manage parking place reservations.
  * [Parking system](./CarParks/src/server/GestioneParcheggi.java): Same as upgrade 1 but allows you to manage parking place reservations.
