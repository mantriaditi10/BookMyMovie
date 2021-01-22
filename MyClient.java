import java.io.*;
import java.net.*;
import java.util.*;

class ChatClient
{
    public static void main(String argv[]) throws Exception
    {
        Scanner sc = new Scanner(System.in);
        String sentence;
        String sentenceFromServer;

        Socket clientSocket = new Socket("localhost", 6000);
        System.out.println("\nConnected to Server.\n");

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream outToServer =new DataOutputStream(clientSocket.getOutputStream());
        DataInputStream din = new DataInputStream(clientSocket.getInputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    
        for(int i=0;i<5;i++) {
            sentenceFromServer = inFromServer.readLine();
            System.out.println("[SERVER]: " + sentenceFromServer);
        }

        sentence = inFromUser.readLine();
        String s = (String)sentence;
        outToServer.writeBytes(sentence + '\n');
        String str="",str2="";
        if(s.equals("3")) {
            str2 = din.readUTF();
            System.out.println("[SERVER]: " + str2);
            
            str = inFromUser.readLine();
            outToServer.writeUTF(str);
            outToServer.flush();

            str2 = din.readUTF();
            System.out.println("[SERVER]: " + str2);
            
            str2 = din.readUTF();
            System.out.println("[SERVER]: " + str2);
        }
        else if(s.equals("2")){
            System.out.println("\nAdmin online.Enter complain: ");
            while(!str.equals("stop")) {
                str = inFromUser.readLine();
                outToServer.writeUTF(str);
                outToServer.flush();
                str2 = din.readUTF();
                System.out.println("[SERVER]: " + str2);
            }
        }
        else if(s.equals("1")) {
            System.out.println("\nSelected Option: Login as User and Book a Ticket!\n");
            String eid;
            System.out.print("\nEnter email: ");
            eid = sc.nextLine();
            outToServer.writeUTF(eid);

            str2 = din.readUTF();
            if(str2.equals("Invalid User! Try Again!")){
                System.out.println("[SERVER]: " + str2);
            }
            else {
                System.out.println("\n[SERVER]: " + str2);
                System.out.println("\n[SERVER]:");
                for(int i=0;i<3;i++) {
                    str2 = din.readUTF();
                    System.out.println(str2);
                }

                int n = Integer.parseInt(din.readUTF());
                System.out.println("\n============================================================");
                for(int i=0;i<n;i++) {
                    
                    for(int j=0;j<4;j++) {
                        str2 = din.readUTF();
                        System.out.println(str2);
                    }
                    //Check if particular movie has now showing section
                    str2 = din.readUTF();
                    if(str2.equals("NS")) {
                        System.out.println("\n----------------------");
                        str2 = din.readUTF();
                        System.out.println(str2);
                        System.out.println("----------------------");
                        
                        int m = Integer.parseInt(din.readUTF());
                        
                        for(int j=0;j<m;j++) {
                            for(int k=0;k<4;k++) {
                                str2 = din.readUTF();
                                System.out.println(str2);
                            }
                        } 
                    }
                    for(int j=0;j<2;j++) {
                        str2 = din.readUTF();
                        System.out.println("\n" + str2);
                    }
                }

                str2 = din.readUTF();
                System.out.println(str2);

                str2 = din.readUTF();
                System.out.println("\n[SERVER]: " + str2);

                //Movieid
                str = inFromUser.readLine();
                outToServer.writeUTF(str);
                outToServer.flush();

                str2 = din.readUTF();
                System.out.println("\n[SERVER]: " + str2);

                //Theatreid
                str = inFromUser.readLine();
                outToServer.writeUTF(str);
                outToServer.flush();


                for(int i=0;i<2;i++) {
                    str2 = din.readUTF();
                    System.out.println(str2);
                }

                //Gold Silver Platinum Choice
                str = inFromUser.readLine();
                outToServer.writeUTF(str);
                outToServer.flush();

                str2 = din.readUTF();
                System.out.println(str2);

                //No of Seats
                String noOfSeats =  inFromUser.readLine();
                outToServer.writeUTF(noOfSeats);
                outToServer.flush();
                int nos = Integer.parseInt(noOfSeats);

                
                str2 = din.readUTF();
                System.out.println("\n" + str2);

                int ss = Integer.parseInt(din.readUTF());

                for(int i=0;i<ss;i++) {
                    str2 = din.readUTF();

                    if(str2.equals("YES")) {
                        str2 = din.readUTF();
                        System.out.print(str2);
                    }
                }

                System.out.println("\nSelect " + nos + " seat nos which you wish to book:");
                for(int i=0;i<nos;i++) {
                    str = inFromUser.readLine();
                    outToServer.writeUTF(str);
                    outToServer.flush();
                }

                System.out.println("\nBOOKING SUCCESSFUL\n");

                for(int i=0;i<6;i++) {
                    str2 = din.readUTF();
                    System.out.println(str2);
                }
                str2 = din.readUTF();
                System.out.print(str2);
                
                int x = Integer.parseInt(din.readUTF());
                
                for(int i=0;i<x;i++) {
                    str2 = din.readUTF();
                    System.out.print(str2);
                }

                System.out.println();

                for(int i=0;i<2;i++) {
                    str2 = din.readUTF();
                    System.out.print(str2);
                }
            }
        }

        outToServer.close();
        clientSocket.close();
    }
}