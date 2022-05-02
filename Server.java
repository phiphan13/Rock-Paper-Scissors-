/**
    @author: Phi Phan
    CSCI 4311
    Programming Assignment 2: Socket Programming 

    Goal of Assignment: build a Multiplayer game ( Rock Paper Scissors)

    Goals of Server:
        * Must start on a known port 
        * Distributes messagse to all other clients who have connection to a server 
        
        
 */


import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    
    private static ExecutorService pool = Executors.newCachedThreadPool();
    
    public Server (int port) {
        
        try {
            //Starts server on a given port

            //serverSocket
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server Online.");
                
            //server listens for clients to connect to port
            while (true) {
                System.out.println("Waiting for player 1.");
                Socket socket1 = serverSocket.accept();
                Client client1 = new Client(socket1);
                System.out.println("Player 1 is ready.");
                
                System.out.println("Waiting for player 2");
                Socket socket2 = serverSocket.accept();
                Client client2 = new Client(socket2);
                System.out.println("Player 2 is ready.");
                
                //create new client objects
                GameSession game = new GameSession(client1, client2);

                //thread begins to handle clients
                pool.execute(game);
                System.out.println("New game has started!");
            }
        } catch (IOException e) {
            System.out.println("Server start failed");
        }
    }
    
    //Start of client threads
    public class GameSession implements Runnable {
        
        Client client1;
        Client client2;
        
        public GameSession (Client client1, Client client2) throws IOException{
        this.client1 = client1;
        this.client2 = client2;
        }
        
        @Override
        public void run(){

        //command prompts when user wins
        String player1win = client1.username + " Wins!";
        String player2win = client2.username + " Wins!";
        String tie = "Tie!";
        String[][] result_table = {{tie, player2win, player1win},
                                   {player1win, tie, player2win},
                                   {player2win, player1win, tie}};
            try {

                //prompts user what they are playing and prompts user to choose rock, paper, or scissors
                msgAll("You are playing Rock, Paper, Scissors!\nChoose your rock, paper, or scissors!");

                //stores player1 option
                String p1option = "";    

                //stores player2 option                                                                          
                String p2option = "";
                while(!(p1option.equals("disconnect")^ p2option.equals("disconnect"))){
                    p1option = client1.in.readLine();
                    p2option = client2.in.readLine();
                   
                   /* Whatever choice the player chooses, that player's index will change to that index that is storing the choice

                        i.e if player 1 chooses 'rock', p1option will change the indexp1 to 0 to store rock, vice versa for papers and scissors

                   */

                    /*
                        Index 0 is Rock 
                        Index 1 is Paper
                        Index 2 is Scissors
                    */

                    int indexp1 = 10;
                    int indexp2 = 10;
                    if (p1option.equals("rock")){
                        indexp1 = 0;
                    }
                    if (p1option.equals("paper")){
                        indexp1 = 1;
                    }
                    if (p1option.equals("scissor")){
                        indexp1 = 2;
                    }
                    if (p2option.equals("rock")){
                        indexp2 = 0;
                    }
                    if (p2option.equals("paper")){
                        indexp2 = 1;
                    }
                    if (p2option.equals("scissor")){
                        indexp2 = 2;
                    }

                    // Checks if user is choosing either of the three options, if not, print an error message
                    try {
                        msgAll(result_table[indexp1][indexp2]);
                    } catch (Exception e){msgAll("Error: rock, paper, or scissor not entered.");}
                    
                }

                //prompts player has disconncted 
                msgAll("Player has disconnected");
            } catch(IOException e){System.out.println(e);}
            
        }

        //prompts messages
        private void msgAll(String msg) {
            client1.out.println(msg);
            client2.out.println(msg);
        }
    }
    
   
    
    public class Client {
        
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;

        public Client(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("Enter a username.");
            username = in.readLine();
        }
        
    }
    
    public static void main(String[] args) {
        Server server = new Server(5000);
    }
}
