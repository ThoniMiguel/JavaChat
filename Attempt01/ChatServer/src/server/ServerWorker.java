package server;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class ServerWorker extends Thread{

    private final Socket clientSocket;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;

    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public String getLogin(){
        return login;
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
        this.outputStream = clientSocket.getOutputStream();

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
                String msg = "\nok login\n\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User '" + login +"' logged in ");

                List<ServerWorker> workerList = server.getWorkerList();
                for(ServerWorker worker: workerList){
                    if(!login.equals(worker.getLogin())) {
                        if(worker.getLogin() != null) {
                            String msg2 = "online " + worker.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                String onlineMsg = "Online " + login + "\n";
                for(ServerWorker worker : workerList){
                    if(!login.equals(worker.getLogin())) {
                        worker.send(onlineMsg);
                    }
                }
            }else{
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }else{
            String msg = "Missing arguments (user password)\n";
            outputStream.write(msg.getBytes());
        }
    }

    private void send(String onlineMsg) throws IOException {
        if(login != null) {
            outputStream.write(onlineMsg.getBytes());
        }
    }
}
