
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PORT_HTTP = 8989;

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ServerSocket server = new ServerSocket(PORT_HTTP);
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
            OutputStream outputStream;
            InputStream input;
            try {
                outputStream = client.getOutputStream();
                input = client.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream), true);
                String line;
                while (true) {
                    line = reader.readLine();
                    printer.println("Servidor: " + line);
                    System.out.println(line);
                    if (line == "1") {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
