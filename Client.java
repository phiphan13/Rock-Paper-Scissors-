/**
    @author: Phi Phan
    CSCI 4311
    Programming Assignment 2: Socket Programming 

    Goal of Assignment: build a Multiplayer game ( Rock Paper Scissors)

    Goals of Client:
        able to have multiple clients
        client may choose option rock paper or scissors
        if user type disconnect, it disconenct user
        
        
        
 */

import java.io.*;
import java.net.*;

public class Client {
    
    //start of client socket
    public Client(String address, int port){
        try (Socket socket = new Socket(address, port)) {
            
            //create outputs for user to see
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            
            //begins thread to read messagse from servers
            readServer s1 = new readServer(socket);
            new Thread(s1).start();
            
            //writes message to server
            String line = "";
            // if user presses 'disconnects', disconnects user from gamee
            System.out.println("Type \"disconnect\" to stop playing.");
            while (!line.equals("disconnect")){
                try {
                    line = keyboard.readLine();
                    out.println(line);
                } catch (IOException e){
                }
            }
            socket.close();
            keyboard.close();
            out.close();
        } catch (IOException e) {

            //if there is no server port display error message 
            System.out.println("Error: No server connection.");
        }
    }
    
    public class readServer implements Runnable{
        private Socket serverSocket;
        private BufferedReader in;
        
        public readServer(Socket socket) throws IOException {
            serverSocket = socket;
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        }
        
        @Override
        public void run(){
            // display messags from the server
            try {
                while (true) {
                String message = in.readLine();
                
                if (message == null) {
                    break;
                }
                
                System.out.println(message + "\n");
                }
            } catch (IOException e) {
            } finally {
                try {
                    serverSocket.close();
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public static void main(String[] args) {
        Client client = new Client("localhost", 5000);
    }
}