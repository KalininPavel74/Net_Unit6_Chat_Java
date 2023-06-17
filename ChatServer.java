import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ChatServer {
    private static java.net.ServerSocket server;
    //  Lists For Clients and Their Nicknames
// Connection Data
    //private static final String host = "localhost";
    private static final int port = 55555;
    private static final ArrayList<Socket> clients = new ArrayList<>();
    private static final ArrayList<BufferedReader> clientsIn = new ArrayList<>();
    private static final ArrayList<PrintWriter> clientsOut = new ArrayList<>();

    private static final ArrayList<String> nicknames = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer();
    }

    public ChatServer() {

        // Starting Server
        try {
            server = new ServerSocket(port);
            System.out.println("Start server on " + server.getInetAddress().getHostAddress() + ":" + port);
        } catch (IOException e) {
            System.out.println("Program already running port=" + port);
        }
        System.out.println("Server if listening...");
        accept();
    }

    private void accept() {
        while (true) {
            try {
                System.out.println("Waiting for a client...");
                Socket client = server.accept();
                System.out.println("Client connected: " + client);
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println("NICK");
                String nickname = in.readLine();
                if (nickname != null && !nickname.isBlank()) {
                    nicknames.add(nickname);
                    clients.add(client);
                    clientsIn.add(in);
                    clientsOut.add(out);
                    broadcast(nickname + " joined!");
                    out.println("Connected to server!");
                    // Start Handling Thread For Client
                    Handle handle = new Handle(nickname, client, in);
                    handle.start();
                }
            } catch (IOException e) {
                System.out.println("Can't accept");
            }
        }

    }

    // Sending Messages To All Connected Clients
    private void broadcast(String message) {
        for (PrintWriter clientOut : clientsOut)
            clientOut.println(message);
    }

    public class Handle extends Thread {
        private String nickname;
        private Socket client = null;
        private BufferedReader in = null;

        public Handle(String nickname, Socket client, BufferedReader in) {
            this.nickname = nickname;
            this.client = client;
            this.in = in;
        }

        public void run() {
            System.out.println("Wait for messages from " + nickname);
            String message = null;
            while (true) {
                try {
                    message = in.readLine();
                } catch (IOException e1) {
                    // Removing And Closing Clients
                    System.out.println(nickname + " left!");
                    broadcast(nickname + " left!");
                    try {
                        in.close();
                        client.close();
                    } catch (IOException e2) {
                        System.out.println(e2.getMessage());
                    }
                    break;
                }
                if (message != null && !message.isBlank())
                    broadcast(message);
            } // while
        } // run()
    } // Handle
}