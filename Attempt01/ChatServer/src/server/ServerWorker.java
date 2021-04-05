package server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;


public class ServerWorker extends Thread{

    private final Socket clientSocket;

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine()) != null){
            String[] tokens = StringUtils.split(line);
            if(tokens != null && tokens.length > 0){
                String cmd = tokens[0];
                if("quit".equalsIgnoreCase(cmd)){
                    break;
                }else{
                    String msg = "Unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }
}
