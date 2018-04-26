package com.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 接受服务器信息
 */
class ReadMes extends Thread {
    private Socket socket;

    public ReadMes(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String message = null;
            while (true) {
                message = reader.readLine();
                // 当读服务器信息线程接收到bye，该线程退出
                if (message.equals("bye")) {
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

/**
 *
 * 发送信息
 */
class SendMes extends Thread {
    private Socket socket;

    public SendMes(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader input = null;
        PrintWriter writer = null;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            writer = new PrintWriter(socket.getOutputStream());
            String message = null;
            while (true) {
                message = input.readLine();
                // 当输入bye客户端退出
                if (message.equals("bye")) {
                    break;
                }
                // 向服务器端发送信息
                writer.println(message);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}

public class ChatClient {
    private String ipAdress;

    public ChatClient(String ipAString) {
        this.ipAdress = ipAdress;
    }

    public void startWork() throws UnknownHostException, IOException {
        Socket socket = new Socket(ipAdress, 2345);
        new ReadMes(socket).start();
        new SendMes(socket).start();


    }

    /**
     * Description
     *
     * @param args
     * @throws UnknownHostException
     * @throws IOException
     */
    public static void main(String[] args) throws UnknownHostException,
            IOException {
        ChatClient chatClient = new ChatClient("127.0.0.1");
        chatClient.startWork();
    }

}
