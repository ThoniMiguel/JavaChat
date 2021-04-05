package server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;


public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private String login = null;

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
                }else if("login".equalsIgnoreCase(cmd)){
                    handleLogin(outputStream, tokens);

                }

                else{
                    String msg = "Unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }
        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];

            if((login.equals("guest") && password.equals("guest")) || (login.equals("thoni") && password.equals("thoni"))  ){
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User '" + login +"' logged in ");
            }else{
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }else{
            String msg = "Missing arguments (user password)\n";
            outputStream.write(msg.getBytes());
        }
    }
}
