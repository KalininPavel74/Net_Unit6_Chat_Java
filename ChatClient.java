import java.io.*;
import java.net.*;

public class ChatClient {
    private static java.net.Socket client;
    // Connection Data
    private static final String host = "localhost";
    private static final int port = 55555;
    private static final String consoleEncoding = "Cp866";
    private String nickname = String.valueOf(ChatClient.class.hashCode());

    public static void main(String[] args) {
        new ChatClient();
    }

    public ChatClient() {

        // Connecting To Server
        try {
            client = new Socket(host, port);
            System.out.println("Connecting To Server " + host + ":" + port);
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            InputStreamReader inputStreamReader = new InputStreamReader(System.in, consoleEncoding);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String s = getNicknameFromUser(reader);
            if (s != null) nickname = s;

            Write write = new Write(nickname, client, out, reader);
            write.start();

            receive(in, out);

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public class Write extends Thread {
        private String nickname;
        private Socket client = null;
        private PrintWriter out = null;
        private BufferedReader reader = null;

        public Write(String nickname, Socket client, PrintWriter out, BufferedReader reader) {
            this.nickname = nickname;
            this.client = client;
            this.out = out;
            this.reader = reader;
        }

        public void run() {
            System.out.println("Wait for messages from " + nickname);
            String message = null;
            while (true) {
                try {
                    message = reader.readLine();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                if (message != null && !message.isBlank())
                    out.println(nickname + ": " + message);
            } // while
        } // run()
    } // Handle

    private String getNicknameFromUser(BufferedReader reader) throws IOException {
        System.out.println("Choose your nickname: ");
        String s = reader.readLine();
        if (s != null && !s.isBlank())
            return s;
        return null;
    }

    private void receive(BufferedReader in, PrintWriter out) {
        try {
            while (true) {
                String message = in.readLine();
                if (message != null && !message.isBlank())
                    if (message.equals("NICK"))
                        out.println(nickname);
                    else System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());

        }
    }

}