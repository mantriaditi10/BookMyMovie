import java.util.*;
import java.io.*;
import java.net.*;

class MyClass{

    public static void main(String args[]) throws Exception {

        Scanner sc = new Scanner(System.in);
        int choice;
        int uid = 1;
        UserList ul = new UserList();
        MovieList obj = new MovieList();
        TheatreList tl = new TheatreList();
        MyServer ms = new MyServer();
        Booking b = new Booking();
        do{
            System.out.println("\nMenu:\n1. Register as User.\n2. Login as Admin.\n3. Establish Connection with Booking Portal.");
            System.out.println("-1 to exit");
            System.out.print("Enter Choice:");
            choice = Integer.parseInt(sc.nextLine());

            switch(choice) {
                case 1:
                    ul.createUser();
                    break;

                case 2: //Admin Login and Its functionalities.
                    Admin ad = new Admin();
                    boolean flag;
                    flag = ad.loginAsAdmin();
                    if(flag) {
                        System.out.println("\nSuccessfully Logged in as Admin!!\n");
                        int c;
                        do {
                            System.out.println("\nAdmin Menu:\n1. Movie Management.\n2. Theatres Management.\n3. Display registered Users.\n-1. Logout.\n");
                            System.out.print("Enter choice: ");
                            c = Integer.parseInt(sc.nextLine());

                            switch(c) {
                                case 1:
                                    int cho;
                                    do{
                                        System.out.println("\n\nMovie Management:\n1. Add Upcoming Movie.\n2. Display Movie database.\n-1. Return to Admin Menu.\n");
                                        System.out.print("Enter choice: ");
                                        cho = Integer.parseInt(sc.nextLine());

                                        switch(cho) {
                                            case 1:
                                                obj.addMovie();
                                                break;

                                            case 2:
                                                obj.displayMovieList(tl);
                                                break;
                                        }
                                    }while(cho!=-1);
                                    break;

                                case 2:
                                    int choi;
                                    do{
                                        System.out.println("\n\nTheatre Management:\n1. Add new Theatre.\n2. Remove existing Theatre.\n3. Display Theatre database.\n4. Add movies in existing theatre.\n-1. Return to Admin Menu.\n");
                                        System.out.print("Enter choice: ");
                                        choi = Integer.parseInt(sc.nextLine());

                                        switch(choi) {
                                            case 1:
                                                tl.addTheatre();
                                                break;
                                            case 2:
                                                tl.removeTheatre();
                                                break;
                                            case 3:
                                                tl.displayTheatreList();
                                                break;
                                            case 4:
                                                tl.addMovieInTheatre(obj);
                                                break;
                                            case 5:
                                                //tl.removeMovieFromTheatre(obj);
                                                break;
                                        }
                                    }while(choi!=-1);
                                    break;

                                case 3:
                                    ul.displayUsers();
                                    break;
                            }
                        }while(c!=-1);
                    }
                    break;
                case 3:
                    ms.chatWithClient(ul,obj,tl);
                    break;
            }
        }
        while(choice!=-1);
    }
}

class MyServer {

    public void chatWithClient(UserList ul,MovieList ml, TheatreList tl) throws Exception {
        String fromClient;
        String s;
        System.out.println("\nHello!\nServer Side Booking Portal:\n");
        ServerSocket ss = new ServerSocket(6000);
        System.out.println("Server Socket awaits connections..");

        Socket cs = ss.accept();

        System.out.println("\nConnection Established with Client!\n");

        BufferedReader inFromAdmin = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cs.getInputStream()));

        DataOutputStream outToClient = new DataOutputStream(cs.getOutputStream());
        DataInputStream din = new DataInputStream(cs.getInputStream());

        outToClient.writeBytes("WELCOME TO BookMyMovie!!\n");
        outToClient.writeBytes("Select Action: \n");
        outToClient.writeBytes("1. Login as User and Book a Ticket.\n");
        outToClient.writeBytes("2. Chat with Admin to lodge a complaint.\n");
        outToClient.writeBytes("3. Leave a Feedback.\n");

        fromClient = inFromClient.readLine();
        String str="",str2="";
        switch(fromClient) {
            case "1"://Login as User and book a ticket
                boolean flag = false;
                System.out.println("\nSelected Option: Login as User and Book a Ticket!\n");
                System.out.println("Waiting for email from client for verification.");
                String eid = din.readUTF();
                System.out.println("FROM CLIENT: Email id: " + eid);

                User u = new User();
                for(Integer i : ul.Ul.keySet()) {
                    u = ul.Ul.get(i);
                    if(u.emailId.equals(eid)) flag = true;
                }
                if(!flag) {
                    outToClient.writeUTF("Invalid User! Try Again!");
                }
                else {
                    outToClient.writeUTF("Login Successful!");
                    Booking b = new Booking();
                    b.handleBooking(ml,tl,outToClient,din);
                }

                break;

            case "2":
                while(!str.equals("stop")){
                    str = din.readUTF();
                    System.out.println("FROM CLIENT: " + str);

                    str2 = inFromAdmin.readLine();
                    outToClient.writeUTF(str2);
                    outToClient.flush();
                }
                break;
            case "3":
                outToClient.writeUTF("Enter your feedback: ");
                str = din.readUTF();

                System.out.println("FEEDBACK FROM CLIENT: " + str);

                outToClient.writeUTF("Thanking you for taking time to leave a feedback!");
                outToClient.writeUTF("Your feedback is valuable to us. Visit Again!");

                outToClient.flush();
                break;
        }
        din.close();
        cs.close();
        ss.close();
        //System.out.println("Choice From Client: " + fromClient);
    }
}

enum MovieStatus {
    NOW_SHOWING,
    MOVIE_NOT_AVAILABLE,
    UPCOMING;
}

enum MovieType {
    ENGLISH,
    HINDI;
}

enum SeatStatus {
    SEAT_BOOKED,
    SEAT_NOT_BOOKED;
}

enum SeatType {
    SILVER,
    GOLD,
    PLATINUM;
}
//========================================= SEAT =========================================================

class Seat {
    int seatNo;
    SeatType seatType;
    SeatStatus seatStatus;
    float seatCost;

    Seat() {}

    Seat(int sno,SeatType st,SeatStatus ss,float c) {
        seatNo = sno;
        seatStatus = ss;
        seatType = st;
        seatCost = c;
    }
}

//==========================================================================================================



//========================================= BOOKING =======================================================

class Booking{

    void handleBooking(MovieList ml, TheatreList tl,DataOutputStream out,DataInputStream in) throws Exception {
        int mc;
        Scanner sc = new Scanner(System.in);
        out.writeUTF("From below given Movie List, Enter Movie id for which you wish to book tickets: ");
        Movie m = new Movie();

        // ml.displayMovieList(tl);

        out.writeUTF("\n\n============================================================");
        out.writeUTF("\nMOVIE DATABASE:");
        out.writeUTF(""+ml.Ml.size());

        for(int i=0; i<ml.Ml.size();i++) {
            m = ml.Ml.get(i);
            out.writeUTF("\nMovieId: "+ m.movieId);
            out.writeUTF("Movie Name: " + m.movieName);
            out.writeUTF("Movie Type: "+ m.movieType);
            out.writeUTF("Movie Status: " + m.movieStatus);
            if(m.movieStatus == MovieStatus.NOW_SHOWING) out.writeUTF("NS");
            else out.writeUTF("ABS");
            if(m.movieStatus == MovieStatus.NOW_SHOWING) {
                out.writeUTF("NOW SHOWING IN THEATRES: ");
                out.writeUTF(""+m.theaterId.size());
                for(int j=0;j<m.theaterId.size();j++) {
                    //ob.displayTheatre(m.theaterId.get(j));
                    Theatre th = new Theatre();

                    for(int k=0;k<tl.Tl.size();k++) {
                        th = tl.Tl.get(k);
                        if(th.theatreId == m.theaterId.get(j)) break;
                    }
                    out.writeUTF(th.theatreId + ": " + th.theatreName);
                    Address a = new Address();
                    a = th.address;
                    out.writeUTF("Address: " + a.streetNo + " , Near " + a.landmark + " , " + a.city);
                    out.writeUTF("Pincode: " + a.pinCode);
                    out.writeUTF("State: " + a.state);
                }
            }
            out.writeUTF("\nRating: " + m.rating);
            out.writeUTF("-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.");
        }
        out.writeUTF("============================================================\n");


        out.writeUTF("Enter MovieId for booking: ");
        mc = Integer.parseInt(in.readUTF());
        //Movie m = new Movie();

        for(int i=0;i<ml.Ml.size();i++) {
            if(ml.Ml.get(i).movieId == mc) {
                m = ml.Ml.get(i);
                break;
            }
        }

        System.out.println("\nMovie id selected by client: " + mc);
        int theatreChoice,theatreIndex = 0;

        out.writeUTF("\nFrom Now Showing section of selected movie, Enter Theatre ID where you wish to watch movie: ");
        theatreChoice = Integer.parseInt(in.readUTF());
        System.out.println("\nTheatre id selected by client: " + theatreChoice);

        Theatre t = new Theatre();
        for(int i=0;i<tl.Tl.size();i++) {
            if(tl.Tl.get(i).theatreId == theatreChoice) {
                t = tl.Tl.get(i);
                theatreIndex = i;
            }
        }
        String theatreName = t.theatreName;

        int movieIndex = 0;
        for(int i=0;i<t.movies.size();i++) {
            if(t.movies.get(i).movieId == mc) {
                m = t.movies.get(i);
                movieIndex = i;
            }
        }
        String movieName = m.movieName;

        int st,nos,temp;

        out.writeUTF("Show Timing Available: 16:00\n");
        out.writeUTF("Select seat type: 1- Silver(Rs.210) 2- Gold(Rs.270) 3- Platinum(Rs.320)\n\n");
        st = Integer.parseInt(in.readUTF());

        out.writeUTF("Enter no. of seats you wish to book: ");
        nos = Integer.parseInt(in.readUTF());

        List<Integer> sb = new ArrayList<Integer>(nos);
        Seat s = new Seat();
        int costOfTickets = 0;
        int j=0;

        switch(st) {
            case 1:
                out.writeUTF("Available seats in Silver: ");
                out.writeUTF("25");
                for(int i=0;i<25;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED) out.writeUTF("YES");
                    else out.writeUTF("NO");
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED)
                        out.writeUTF(s.seatNo + " ");
                }
                //out.writeUTF("\nSelect " + nos + " seat nos which you wish to book: ");

                for(int i=0;i<nos;i++) {
                    temp = Integer.parseInt(in.readUTF());
                    sb.add(temp);
                }

                Collections.sort(sb);
                j = 0;
                for(int i=0;i<25;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatNo == sb.get(j)) {
                        s.seatStatus = SeatStatus.SEAT_BOOKED;
                        j++;
                        m.showAtFour.set(i,s);
                        if(j == sb.size()) break;
                    }
                }
                costOfTickets = nos * 210;
                break;
            case 2:
                out.writeUTF("Available seats in Gold: ");
                out.writeUTF("15");
                for(int i=25;i<40;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED) out.writeUTF("YES");
                    else out.writeUTF("NO");
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED)
                        out.writeUTF(s.seatNo + " ");
                }
                //out.writeUTF("\nSelect " + nos + " seat nos which you wish to book: ");
                for(int i=0;i<nos;i++) {
                    temp = Integer.parseInt(in.readUTF());
                    sb.add(temp);
                }
                Collections.sort(sb);
                j = 0;
                for(int i=25;i<40;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatNo == sb.get(j)) {
                        s.seatStatus = SeatStatus.SEAT_BOOKED;
                        j++;
                        m.showAtFour.set(i,s);
                        if(j == sb.size()) break;
                    }
                }
                costOfTickets = nos * 270;
                break;
            case 3:
                out.writeUTF("Available seats in Platinum: ");
                out.writeUTF("10");
                for(int i=40;i<50;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED) out.writeUTF("YES");
                    else out.writeUTF("NO");
                    if(s.seatStatus == SeatStatus.SEAT_NOT_BOOKED)
                        out.writeUTF(s.seatNo + " ");
                }
                //out.writeUTF("\nSelect " + nos + " seat nos which you wish to book: ");
                for(int i=0;i<nos;i++) {
                    temp = Integer.parseInt(in.readUTF());
                    sb.add(temp);
                }
                Collections.sort(sb);
                j = 0;
                for(int i=40;i<50;i++) {
                    s = m.showAtFour.get(i);
                    if(s.seatNo == sb.get(j)) {
                        s.seatStatus = SeatStatus.SEAT_BOOKED;
                        j++;
                        m.showAtFour.set(i,s);
                        if(j == sb.size()) break;
                    }
                }
                costOfTickets = nos * 320;
                break;
        }

        t.movies.set(movieIndex,m);
        tl.Tl.set(theatreIndex,t);
        out.writeUTF("\n=======================================================");
        out.writeUTF("MOVIE TICKET:");
        out.writeUTF("=======================================================\n");

        out.writeUTF("Movie Name: " + movieName);
        out.writeUTF("Theatre Name: " + theatreName);
        out.writeUTF("Seat No: ");
        if(st == 1) out.writeUTF("SILVER - ");
        else if(st == 2) out.writeUTF("GOLD - ");
        else if(st == 3) out.writeUTF("PLATINUM - ");
        out.writeUTF("" + sb.size());
        for(int i=0;i<sb.size();i++) out.writeUTF(sb.get(i) + " ");
        //out.writeUTF("\n ");
        out.writeUTF("Total cost of tickets: Rs." + costOfTickets);

        out.writeUTF("\n=======================================================\n");

    }
}

//==========================================================================================================


//================================== MOVIE & MOVIE_LIST =====================================================
class Movie{
    int movieId;
    List<Integer> theaterId = new ArrayList<Integer>(); //Make this a vector or ArrayList
    String movieName;
    MovieType movieType;
    MovieStatus movieStatus;
    float rating;
    List<Seat>showAtFour = new ArrayList<Seat>(50);

    Movie() {}

    Movie(int mid,String n,MovieType mt,MovieStatus ms,float r) {
        movieId = mid;
        //theaterId.add(tid);
        movieName = n;
        movieType = mt;
        movieStatus = ms;
        rating = r;

        for(int i=1;i<=25;i++) {
            Seat s = new Seat(i,SeatType.SILVER,SeatStatus.SEAT_NOT_BOOKED,210);
            showAtFour.add(s);
        }
        for(int i=26;i<=40;i++) {
            Seat s = new Seat(i,SeatType.GOLD,SeatStatus.SEAT_NOT_BOOKED,270);
            showAtFour.add(s);
        }
        for(int i=40;i<=50;i++) {
            Seat s = new Seat(i,SeatType.PLATINUM,SeatStatus.SEAT_NOT_BOOKED,320);
            showAtFour.add(s);
        }
    }

    void displayMovie() {
        System.out.println("\nMovieId: "+ movieId);
        System.out.println("Movie Name: " + movieName);
        System.out.println();
    }
}

class MovieList implements Serializable{
    List<Movie> Ml = new ArrayList<Movie>();
    static int id;

    static {
        id = 1;
    }

    void addMovie() {
        Scanner sc = new Scanner(System.in);
        String name;MovieType mt;MovieStatus ms;float r;int movt;
        System.out.print("\nEnter name of movie: ");
        name = sc.nextLine();
        System.out.println("Select movie type: ");
        System.out.println("1.Hindi\n2.English");
        movt = Integer.parseInt(sc.nextLine());
        switch(movt) {
            case 1:
                mt = MovieType.HINDI;
                break;
            case 2:
                mt = MovieType.ENGLISH;
                break;
            default:
                mt = MovieType.HINDI;
        }

        ms = MovieStatus.UPCOMING;

        System.out.print("Enter rating(1-5 stars): ");
        r = Float.parseFloat(sc.nextLine());
        System.out.println("Adding movie!");
        Movie m = new Movie(id,name,mt,ms,r);

        Ml.add(m);

        id++;
    }

    void displayMovieList(TheatreList ob) {
        Movie m = new Movie();
        System.out.println("\n\n-------------------------------------------------");
        System.out.println("\nMOVIE DATABASE:");
        for(int i=0; i<Ml.size();i++) {
            m = Ml.get(i);
            System.out.println("\nMovieId: "+ m.movieId);
            System.out.println("Movie Name: " + m.movieName);
            System.out.println("Movie Type: "+ m.movieType);
            System.out.println("Movie Status: " + m.movieStatus);
            if(m.movieStatus == MovieStatus.NOW_SHOWING) {
                System.out.println("\nNOW SHOWING IN THEATRES: ");
                for(int j=0;j<m.theaterId.size();j++) {
                    ob.displayTheatre(m.theaterId.get(j));
                }
            }
            System.out.println("\nRating: " + m.rating);
            System.out.println("----------------------------");
        }
        System.out.println("-------------------------------------------------\n");
    }
}
//==============================================================================================================



//================================== THEATRE & THEATRE_LIST =====================================================
class Theatre{

    int theatreId;
    String theatreName;
    Address address;
    List<Movie> movies = new ArrayList<Movie>();

    Theatre() {}

    Theatre(int tid,String n,Address add) {
        theatreId = tid;
        theatreName = n;
        address = add;
    }
}

class TheatreList {
    List<Theatre> Tl = new ArrayList<Theatre>();
    static int id;

    static{
        id = 1;
    }

    void addTheatre() {
        String name;
        Scanner sc = new Scanner(System.in);
        Address a = new Address();
        System.out.println("\n\nEnter Theatre Details: ");
        System.out.print("Name of Theatre: ");
        name = sc.nextLine();
        System.out.println("Address Details: ");
        a = a.addAddress();

        Theatre t = new Theatre(id,name,a);
        id++;

        Tl.add(t);
    }

    void removeTheatre() {
        Scanner sc = new Scanner(System.in);
        int tid;

        System.out.print("Enter TheatreId of Theatre to be removed: ");
        tid = Integer.parseInt(sc.nextLine());
        Theatre t =new Theatre();
        int index = 0;
        for(int i=0;i<Tl.size();i++) {
            t = Tl.get(i);
            if(t.theatreId == tid) {
                index = i;
                break;
            }
        }
        Tl.remove(index);
    }

    void displayTheatreList() {
        Theatre t = new Theatre();
        Address a = new Address();
        //System.out.println(Ml);
        System.out.println("\n\n-------------------------------------------------");
        System.out.println("\nTHEATRE DATABASE:");
        for(int i=0; i<Tl.size();i++) {
            t = Tl.get(i);
            System.out.println("\nTheatreId: "+ t.theatreId);
            System.out.println("Theatre Name: " + t.theatreName);
            a = t.address;
            a.displayAddress(a);
            System.out.println();
        }
        System.out.println("-------------------------------------------------\n");
    }

    void addMovieInTheatre(MovieList obj) {
        int tid;
        Scanner sc = new Scanner(System.in);

        displayTheatreList();

        System.out.print("From above Theatre Database, Enter TheatreId of Theatre you want to insert movies in: ");
        tid = Integer.parseInt(sc.nextLine());

        if(checkTheatreIdPresent(tid)) {
            Theatre t = new Theatre();
            int theatreIndex = 0;
            for(int i=0;i<Tl.size();i++) {
                t = Tl.get(i);
                if(t.theatreId == tid){
                    theatreIndex = i;
                    break;
                }
            }
            Movie m = new Movie();
            //t.movies = new ArrayList<Movie>();
            //System.out.println(obj.Ml);
            System.out.println("\nFor given movies, Enter: \n1.To add as Upcoming Movie.\n2.To add as Now Showing.\n3. Not Applicable.");
            int mc;

            for(int i=0;i< obj.Ml.size();i++) {
                m = obj.Ml.get(i);

                m.displayMovie();

                System.out.print("\nEnter choice(1-Uc , 2-Ns , 3-N/A): ");
                mc = Integer.parseInt(sc.nextLine());

                if(mc == 1) {
                    t.movies.add(m);
                    Tl.set(theatreIndex,t);
                }
                else if(mc==2) {
                    m.movieStatus = MovieStatus.NOW_SHOWING;
                    m.theaterId.add(tid);
                    //System.out.println("theaterId arraylist: " + m.theaterId);
                    t.movies.add(m);
                    obj.Ml.set(i,m);
                    Tl.set(theatreIndex,t);
                    //System.out.println(obj.Ml.get(i).theaterId);
                }
            }

            displayMoviesInTheatre(t);
        }
        else {
            System.out.println("Theatre does not exist in database!");
        }
    }

    //Add method to remove movie from database

    void displayMoviesInTheatre(Theatre t) {
        Movie m = new Movie();
        System.out.println("\n=======================================================");
        System.out.println("Now Showing Movies in " + t.theatreName + " are: ");
        System.out.println("=======================================================");

        for(int i=0;i<t.movies.size();i++) {
            m = t.movies.get(i);
            if(m.movieStatus == MovieStatus.NOW_SHOWING) {
                m.displayMovie();
            }
        }
        System.out.println("\n=======================================================");
        System.out.println("Upcoming Movies in " + t.theatreName + " are: ");
        System.out.println("=======================================================");

        for(int i=0;i<t.movies.size();i++) {
            m = t.movies.get(i);
            if(m.movieStatus == MovieStatus.UPCOMING) {
                m.displayMovie();
            }
        }
    }

    boolean checkTheatreIdPresent(int t) {
        Theatre th = new Theatre();

        for(int i=0;i<Tl.size();i++) {
            th = Tl.get(i);
            if(th.theatreId == t) return true;
        }
        return false;
    }


    void displayTheatre(int t) {
        Theatre th = new Theatre();

        for(int i=0;i<Tl.size();i++) {
            th = Tl.get(i);
            if(th.theatreId == t) break;
        }

        System.out.println(th.theatreId + ": " + th.theatreName);
        Address a = new Address();
        a = th.address;
        a.displayAddress(a);
        System.out.println();
    }
}
//==========================================================================================================




//============================================= ADDRESS =====================================================
class Address{
    String city;
    String pinCode;
    String state;
    String streetNo;
    String landmark;

    Address() {}
    Address(String c,String p,String st,String sn,String lm) {
        city = c;
        pinCode = p;
        state = st;
        streetNo = sn;
        landmark = lm;
    }

    Address addAddress() {
        String c;String p;String st;String sn;String lm;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter city: ");
        c = sc.nextLine();
        System.out.print("Enter Pincode: ");
        p = sc.nextLine();
        System.out.print("Enter State: ");
        st = sc.nextLine();
        System.out.print("Enter Street Name: ");
        sn = sc.nextLine();
        System.out.print("Enter Landmark: ");
        lm = sc.nextLine();

        Address a = new Address(c,p,st,sn,lm);
        return a;
    }
    void displayAddress(Address a) {
        System.out.println("Address: " + a.streetNo + " , Near " + a.landmark + " , " + a.city);
        System.out.println("Pincode: " + a.pinCode);
        System.out.println("State: " + a.state);
    }
}

//===================================================================================================




//======================================= ADMIN =====================================================
//Admin details and logging in as Admin
class Admin {
    final String adminEmailId="aditimantri04@gmail.com";
    final String password="Aditi123@";

    boolean loginAsAdmin() {
        Scanner sc = new Scanner(System.in);
        try{
            String eid,pw;
            System.out.print("\nEnter Admin emailid: ");
            eid = sc.nextLine();
            if(eid.equals(adminEmailId)) {
                try {
                    System.out.print("Enter Password: ");
                    pw = sc.nextLine();
                    if(pw.equals(password)) return true;
                    else {
                        throw new InvalidAdminDetails("Invalid Password!");
                    }
                }
                catch(InvalidAdminDetails exc) {
                    System.out.println(exc);
                    return false;
                }
            }
            else {
                throw new InvalidAdminDetails("Invalid Admin emailid!");
            }
        }
        catch(InvalidAdminDetails exc) {
            System.out.println(exc);
            return false;
        }
    }
}

//Exception class for invalid admin entries
class InvalidAdminDetails extends Exception {
    String str1;

    InvalidAdminDetails(String str2) {
        str1 = str2;
    }

    public String toString() {
        return ("Error: " + str1);
    }
}

//==========================================================================================================



//================================================ USER =====================================================
//One User blueprint
class User {
    int userId;
    String name;
    String mobNo;
    String emailId;
    String sex;

    User() {}

    User(int uid, String n, String mno, String eid, String gen) {
        userId = uid;
        name = n;
        mobNo = mno;
        emailId = eid;
        sex = gen;
    }
}

//Creation and storage of all users registered
class UserList {
    HashMap<Integer,User> Ul = new HashMap<Integer,User>();
    static int id;

    static {
        id = 1;
    }

    void createUser() {
        Scanner sc = new Scanner(System.in);
        String name; String d; String mno; String eid; String gen;
        System.out.println("\nEnter your details:");

        System.out.print("Name: ");
        name = sc.nextLine();

        System.out.print("Mobile No: ");
        mno = sc.nextLine();

        System.out.print("Email id: ");
        eid = sc.nextLine();

        System.out.print("Gender: ");
        gen = sc.nextLine();

        User usr = new User(id,name,mno,eid,gen);

        Ul.put(id,usr);
        id++;
    }

    boolean loginAsUser() {
        Scanner sc = new Scanner(System.in);
        String eid;
        System.out.print("\nEnter email: ");
        eid = sc.nextLine();
        User u = new User();
        for(Integer i : Ul.keySet()) {
            u = Ul.get(i);
            if(u.emailId.equals(eid)) return true;
        }
        return false;
    }

    void displayUsers() {
        User u = new User();
        System.out.println("\n\n-------------------------------------------------");
        System.out.println("\nUSER DATABASE:");
        for(Integer i : Ul.keySet()) {
            u = Ul.get(i);
            System.out.println("\nUserId: "+ u.userId);
            System.out.println("Name: " + u.name);
            System.out.println("Mobile No.: "+ u.mobNo);
            System.out.println("Email: " + u.emailId);
            System.out.println("Gender: " + u.sex);
            System.out.println();
        }
        System.out.println("-------------------------------------------------\n");
    }
}
//==========================================================================================================

