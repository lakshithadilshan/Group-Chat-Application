

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {


    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        ServerSocket ss = new ServerSocket(1234);

        try {
            while (!ss.isClosed()) {
                Socket s = ss.accept();
                System.out.println("Client is connected");
                ClientHandler clientHandler = new ClientHandler(s);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ss.close();
            } catch (Exception ex) {
            }
        }

    }

}
