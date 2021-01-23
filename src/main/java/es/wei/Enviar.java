package es.wei;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static es.wei.Main.FILE_NAME;

public class Enviar {
    private static ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static void main(String[] args) throws  InterruptedException {
        System.out.println("Proceso ejecutado");
        String infoBolsa = args[0];
        executorService.execute(new Envio(infoBolsa, FILE_NAME));
        executorService.shutdown();
        if(!executorService.awaitTermination(2, TimeUnit.SECONDS)){
            executorService.shutdownNow();
        }
    }
}
