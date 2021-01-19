import javafx.concurrent.Worker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        ServerSocket server = new ServerSocket(8989);
        Socket cliente;
        while (true) {
            cliente = server.accept();
            executorService.execute(new Work(cliente));

        }


    }

    private static class Work implements Runnable {
        private Socket client;

        public Work(Socket client) {
            this.client = client;
        }

        public void run() {
            System.out.println(" conectado cliente por puerto:" + client.getPort());
            OutputStream outputStream = null;
            try {
                outputStream = client.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream input = null;
            try {
                input = client.getInputStream();

            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream));
            String line;
            while (true) {
                try {
                    if (!((line = reader.readLine()) != null)) {
                        printer.println("Servidor: " + line);
                        System.out.println(line);
                        printer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
