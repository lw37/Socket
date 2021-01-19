import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    private static final int PORT_HTTP =8989 ;

    public static void main(String[] args) throws IOException {

        Socket socket= new Socket();
        socket.connect(new InetSocketAddress("localhost",PORT_HTTP));
        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        PrintWriter printer =new PrintWriter(new OutputStreamWriter(outputStream));
        Scanner s = new Scanner(System.in);
        String message;
        while(true){
            message = s.nextLine();
            if(message.equals("exit")){
                break;
            }
            printer.println(message);
            printer.flush();
            System.out.println(reader.readLine());
        }


        String line;
        while ((line=reader.readLine())!=null){
            System.out.println((line));
        }
    }
}
