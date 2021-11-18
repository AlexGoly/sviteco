package com.alex;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class TCPConection {
   private final Socket socket;
    private final Thread thread;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final ArrayList<TCPConection> conections= new ArrayList<>();

    public TCPConection(Socket socket) throws IOException {
        this.socket = socket;
        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread= new Thread((new Runnable() {
            @Override
            public void run() {
                try{
                newClientConection(TCPConection.this);
                while (!thread.isInterrupted()){
                    receiveString(reader.readLine());
                }
            }catch(IOException e){
                System.out.println("TCP conection Exeption");
            }finally {
                    clientDisconect(TCPConection.this);
                }
                }
        }));
                thread.start();
    }
    public synchronized void sendString(String str){
        try {
            writer.write(str+"\r\n");
            writer.flush();
        }catch (IOException e){
            System.out.println("TCP conection Exeption");
            disconect();
        }
    }
    public  synchronized void newClientConection(TCPConection tcpConection){
        conections.add(tcpConection);
        sendToAllConnections("Client conected "+tcpConection);

    }
    public synchronized void receiveString(String str){
        System.out.println("Client message: "+str);
        sendString("Server answer: "+str);
    }
    public synchronized void clientDisconect(TCPConection tcpConection){
        conections.remove(tcpConection);
        System.out.println("Client "+tcpConection+ " - disconected");
    }

    private void sendToAllConnections(String str) {
        System.out.println("Client: "+conections+" \n"+"Message: "+str);
        for (TCPConection conection : conections) {
            conection.sendString(str);

        }
    }
    private void disconect(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("TCP conection Exeption");
        }
    }
    @Override
    public String toString() {
        return "TCPConection: " + socket.getInetAddress() + " port:" + socket.getPort();
    }
}




