package com.javarush.task.task30.task3008;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port;

        System.out.println("Insert port, please.");
        port = ConsoleHelper.readInt();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is up!");
            while (true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sendBroadcastMessage(Message message) {
        for (Connection connection : connectionMap.values()) {
            try {
                connection.send(message);
            }
            catch (IOException e) {
                System.out.println("Message can`t be delivered:(");
            }
        }
    }
    private static class Handler extends Thread {
        private Socket socket;

        private Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
//            connection.send(new Message(MessageType.NAME_REQUEST, "Enter your name, please."));
//            Message nameReceived = connection.receive();
//            if (nameReceived.getType() == MessageType.USER_NAME) {
//                String name = nameReceived.getData();
//                if (name != null && !name.isEmpty()) {
//                    for (String exist : connectionMap.keySet()) {
//                        if (exist.equals(name)) {
//                            ConsoleHelper.writeMessage("User with this name already exists, please try again.");
//                            return serverHandshake(connection);
//                        }
//                    }
//                    connectionMap.put(name, connection);
//                    ConsoleHelper.writeMessage("Name accepted!");
//                    return name;
//                }
//            }
//            ConsoleHelper.writeMessage("Ooops! Wrong name. Please, try again.");
//            return serverHandshake(connection);
            //ЕБАНЫЙ ВАЛИДАТОР МОЙ РЕКУРСИВНЫЙ СПОСОБ ПИЗЖЕ!
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message userResponse = connection.receive();
                if (userResponse.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Incorrect message. From: " + socket.getRemoteSocketAddress());
                    continue;
                }
                String name = userResponse.getData();
                if (name == null || name.isEmpty()) {
                    ConsoleHelper.writeMessage("Invalid empty name. From: " + socket.getRemoteSocketAddress());
                    continue;
                }
                if (connectionMap.containsKey(name)) {
                    ConsoleHelper.writeMessage("Name already exists. From: " + socket.getRemoteSocketAddress());
                    continue;
                }
                connectionMap.put(name, connection);
                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return name;
            }
        }
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                String name = entry.getKey();
                if (!name.equals(userName)) {
                    Message message = new Message(MessageType.USER_ADDED, name);
                    connection.send(message);
                }
            }
        }
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message receivedMessage = connection.receive();
                if (receivedMessage.getType() == MessageType.TEXT) {
                    Message message = new Message(MessageType.TEXT, String.format("%s: %s", userName, receivedMessage.getData()));
                    sendBroadcastMessage(message);
                } else {
                    ConsoleHelper.writeMessage("Incorrect message type. From: " + userName);
                }
            }
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage(String.format("Connection with %s established", socket.getRemoteSocketAddress()));
            String name = "";
            try (Connection connection = new Connection(socket)) {
                name = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, name));
                notifyUsers(connection, name);
                serverMainLoop(connection, name);
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error occur ");
            }
            if (!name.isEmpty()) {
                connectionMap.remove(name);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, name));
            }
            ConsoleHelper.writeMessage("Connection closed.");
        }
    }
}
