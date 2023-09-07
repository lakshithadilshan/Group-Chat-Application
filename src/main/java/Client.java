

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (Exception ex) {
            }
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println("Enter Msg");
            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();

                bufferedWriter.write(username + ":" + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                socket.close();
            } catch (Exception ex) {
            }

        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {

            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                           //check ISO msg or normal msg
                        if (msgFromGroupChat.contains("ISO|")) {
                            //print username
                            String username = msgFromGroupChat.substring(0, msgFromGroupChat.indexOf(":"));
                            System.out.println(username + ":");

                            Integer msgindex = msgFromGroupChat.indexOf("|");
                            msgindex += 1;
                            System.out.println("ISO8583:" + msgFromGroupChat.substring(msgindex));

                            try {
                                // Setting packager
                                GenericPackager packager = new GenericPackager("C:\\Users\\lakshitha_i\\IdeaProjects\\untitled\\visapack.xml");
                                ISOMsg isoMsg = new ISOMsg();
                                isoMsg.setPackager(packager);


                                String isoMessage = msgFromGroupChat.substring(msgindex);

                                //  convert the ISO8583 Message String to byte[]
                                byte[] bIsoMessage = new byte[isoMessage.length() / 2];
                                for (int i = 0; i < isoMessage.length(); i += 2) {
                                    String byteString = isoMessage.substring(i, i + 2);
                                    bIsoMessage[i / 2] = (byte) Integer.parseInt(byteString, 16);
                                }

                                // Unpack the message
                                isoMsg.unpack(bIsoMessage);

                                // Separate and print the ISO8583 fields
                                for (int i = 1; i <= isoMsg.getMaxField(); i++) {
                                    if (isoMsg.hasField(i)) {
                                        System.out.println("Field " + i + ": " + isoMsg.getString(i));
                                    }
                                }
                            } catch (ISOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println(msgFromGroupChat);
                        }


                    } catch (Exception e) {
                        try {
                            e.printStackTrace();
                            socket.close();
                        } catch (Exception ex) {
                        }

                    }
                }
            }
        }).start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Your username for grp chat");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Server Connected");
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }


}
