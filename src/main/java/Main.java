import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static final int PORT_HTTP = 9876;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", PORT_HTTP));
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printer = new PrintWriter(new OutputStreamWriter(outputStream), true);

        Scanner s = new Scanner(System.in);
        int i;
        String message;
        String line;

        while (true) {
            line = reader.readLine();
            System.out.println((line));
            if(line.equals("0:Salir")) {
                i = s.nextInt();
                printer.println(i);
                line = reader.readLine();
                System.out.println((line));
                message = s.nextLine();
                printer.println(message);
                line = reader.readLine();
                System.out.println((line));
                message = s.nextLine();
                printer.println(message);
                line = reader.readLine();
                System.out.println((line));
                message = s.nextLine();
                printer.println(message);
            }
        }

    }
}
