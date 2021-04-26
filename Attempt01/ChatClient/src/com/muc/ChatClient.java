package com.muc;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private final int serverPort    ;
    private final String serverName;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private BufferedReader bufferedIn;

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);
        if(!client.connect()){
            System.err.println("Connect failed!");
        }else{
            System.out.println("Connect successful");
            if(client.login("guest", "guest")){
                System.out.println("Login successful");
            }else{
                System.err.println("Login failed.");
            }
        }
    }

    private boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password  +"\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();

        if("ok login".equalsIgnoreCase(response)){
            return true;
        }else{
            return false;
        }
    }

    private boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is: " + this.socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
}
