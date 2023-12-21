event manager project : 

Common setup
Clone the repo and install the dependencies.

git clone https://github.com/itai64/EventManager.git

Steps for read-only access

After you will run the server ,he will open http://localhost:8081 and take a look around.

There is no Login process. 

Entities : 

User - represent the client. 
attributes :
Long id - key of the entity.
String name - full name of the user.
List<Long> events - ids of the own user event's.

Event - very client can have many event.
attributes :
Long id - key of the entity.
Integer popularity - amount of people attending to the event.
LocalDateTime eventDate - event accurate time.
LocalDateTime creationTime - creation accurate time.
String location - the location of the event.
