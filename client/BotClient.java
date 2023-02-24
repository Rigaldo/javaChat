package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class BotClient extends Client {
    @Override
    protected String getUserName() {
        return "date_bot_" + (int)(Math.random() * 100);
    }
    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    public static void main(String[] args) {
        new BotClient().run();
    }
    public class BotSocketThread extends SocketThread {
        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] array = message.split(": ");
            if (array.length == 2) {
                SimpleDateFormat format;
                String pattern = "";
                switch (array[1]) {
                    case ("дата"):
                        pattern = "d.MM.YYYY";
                        break;
                    case ("день"):
                        pattern = "d";
                        break;
                    case ("месяц"):
                        pattern = "MMMM";
                        break;
                    case ("год"):
                        pattern = "YYYY";
                        break;
                    case ("время"):
                        pattern = "H:mm:ss";
                        break;
                    case ("час"):
                        pattern = "H";
                        break;
                    case ("минуты"):
                        pattern = "m";
                        break;
                    case ("секунды"):
                        pattern = "s";
                }
                if (!pattern.isEmpty()) {
                    format = new SimpleDateFormat(pattern);
                    String date = format.format(Calendar.getInstance().getTime());
                    String answer = "Информация для " + array[0] + ": " + date;
                    BotClient.this.sendTextMessage(answer);
                }
            }
        }

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }
    }

}
