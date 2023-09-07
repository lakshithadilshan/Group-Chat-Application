import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferreader;
    private BufferedWriter bufferwriter;
    private String ClientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.ClientUsername = this.bufferreader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER:" + ClientUsername+ "has entered the chat!");
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try {
                messageFromClient = bufferreader.readLine();
                broadcastMessage(messageFromClient);

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try {
                if(!clientHandler.ClientUsername.equals(ClientUsername)){
                    clientHandler.bufferwriter.write(messageToSend);
                    clientHandler.bufferwriter.newLine();
                    clientHandler.bufferwriter.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
