package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


class Conversation extends Thread {
    private Socket socket;
    private List<Socket> socketList;
    private int count;

    public Conversation(int count, Socket socket, List<Socket> socketList) {
        this.count = count;
        this.socket = socket;
        this.socketList = socketList;
    }

    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String message = null;
            char[] chars;
            while (true) {
                //GPS发送的消息长度为52，故接收52长
                int length = 52;
                chars = new char[length];
                if (reader.read(chars) != -1) {
                    message = new String(chars);
                } else {
                    message = "bye";
                }

                // 接收到客户端的bye信息，客户端即将退出，并将bye写入到该客户端
                if (message.equals("bye")) {
                    writer = new PrintWriter(socket.getOutputStream());
                    writer.flush();
                    break;
                } else {
                    // 向所有的客户端发送接收到信息，除了自己，这里是为了解决GPS客户端不能接收数据的问题
                    for (int i = 0; i < socketList.size(); i++) {
                        if (i != count - 1) {
                            writer = new PrintWriter(socketList.get(i)
                                    .getOutputStream());
                            writer.println(message);
                            writer.flush();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class Server {


    public void startWork() throws IOException {
        ServerSocket serverSocket = new ServerSocket(2345);
        List<Socket> socketList = new ArrayList<Socket>();
        Socket socket = null;
        int count = 0;
        while (true) {
            socket = serverSocket.accept();
            count++;
            System.out.println(count + " clinet connected to the server!");
            // 将每一个连接到该服务器的客户端，加到List中
            socketList.add(socket);
            // 每一个连接到服务器的客户端，服务器开启一个新的线程来处理
            new Conversation(count, socket, socketList).start();
        }
    }


    public static void main(String[] args) throws IOException {
        Server Server = new Server();
        Server.startWork();
    }

}
