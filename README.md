# Java parking system

>Project's specifications are taken from the 2019 exam of the course "Sistemi Distribuiti" and can be found in the file assignment.pdf in italian languages.

This project implements a simple parking system that allows motorists to park and retrieve the car automatically by issuing a unique ticket. The project was carried out in 3 different versions 

## Basic version 

It is the basic version of the program. It allows customers to park their car in one of the car parks managed by the platform. In this case customers does not know all the possible parking spaces but only the one in which he intends to park. The class [Automobilista](./CarParks/src/carParks/Automobilista.java) is made as a Thread and the concurrent with other object of the same class is managed.

In the event of a parking request, if the parking is full, the driver will remain waiting for a space to become available 
  * [Car](./CarParks/src/carParks/Automobile.java): Contains the basic properties of a car. Each driver as a car!
  * [Driver](./CarParks/src/carParks/Automobilista.java): it acts as a thread and allows you to park and cyclically pick up a car in a state of concurrency with other threads of the same type. In this version the driver has his own specific parking instance that acts as a destination and with which he interacts directly.
  * [Parking place](./CarParks/src/carParks/Posto.java): Contains the basic properties of each single place of a parking site. Each place in this version could be taken or not.
  * [Parking system](./CarParks/src/carParks/Parcheggio.java): Contains a list of parking place, allows to manage the parking and pick up of cars based on available seats.
  * [Valet](./CarParks/src/carParks/Parcheggiatore.java): Manages the actual delivery and pick up of the car. A car park can have one or more valets.
  * [main](./CarParks/src/carParks/MainVersioneBase.java): Simulates the use of the classes just described according to the basic version of the program 
