package Clases;

import java.io.IOException;
import java.net.DatagramSocket;


public class ServidorUDP {
    public static void main(String[] args) throws IOException {

        int puerto = 4444;

        DatagramSocket servidor = new DatagramSocket(puerto);
        ServidorUdpHilo serverHilo = new ServidorUdpHilo(servidor);
        serverHilo.start();


    }
}
