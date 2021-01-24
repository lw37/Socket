import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static final int PORT_HTTP =9876;

    public static void main(String[] args) throws IOException {
        Socket socket= new Socket();
        socket.connect(new InetSocketAddress("localhost",PORT_HTTP));
        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();

        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printer =new PrintWriter(new OutputStreamWriter(outputStream),true);

        Scanner s = new Scanner(System.in);
        String message;
        String line;

        while(true){
            while ((line=reader.readLine())!=null){
                System.out.println((line));
            }
            message = s.nextLine();
            printer.println(message);

        }
    }
}
