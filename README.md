# BookMyMovie
As the title of the project itself suggests, the inspiration to take up this PS came from the BookMyShow app, which effortlessly offers showtimes, movie tickets, reviews, trailers, concert tickets and other events near whatever city you live in.
Downscaling the scope a bit, I restricted myself to exploring the Movie booking part.

The project is divided into two basic modules: the Admin and the User.

Initially, I started with using Classes, Enums and Collections to store the various User, Movie and Theatre data but later realised that Collections would provide me with just a temporary storage solution, due to which I decided to back my system with a Database.

To incorporate a database I used the JDBC API to ensure Java Database Connectivity to the SQL Database. 
The next problem I faced was to optimize storing the N-N relationship between Movies and Theatres in my SQL database as well as keeping a track of various screens, show-timings and seats booked in them. After a lot of contemplation, I finally came up with an appropriate schema to make this system possible and optimally normalized the database.

To make it as close to real-life Movie Booking Systems, I modelled it into a Client-Server architecture and established communication between the Server and Client using Java Socket Programming. To authenticate it further, I incorporated Multithreading in the code to enable multiple clients to book tickets at the same time.
I am aware that the system is not as optimized as it should be, to be deployed in real life, but the entire process of building up this system from scratch has been a great learning arc for me as I faced problems at every stage and worked towards solving them as optimally as I could.


So presently, the system gives the admin functionalities to :
add new movies, new theatres in the database along with managing what movies are upcoming, now showing and not available. The admin also generates a Ticket with appropriate payment and seat details when the user books one. 

While the user can:
book tickets, chat with the admin to lodge any complaint or leave a feedback or rating. 

Along with these functionalities, there is a scope for future work as well where I plan to develop a GUI with other additional functionalities.   
