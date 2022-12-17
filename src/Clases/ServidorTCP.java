package Clases;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServidorTCP {
    public static void main(String[] args) throws IOException {

        int puerto = 3344;

        ServerSocket s;
        Socket c;
        s = new ServerSocket(puerto);
        System.out.println("Servidor iniciado");
        while (true) {
            c = s.accept(); //esperando cliente
            ServidorTcpHilo hilo = new ServidorTcpHilo(c);
            hilo.start();
        }

    }


}
