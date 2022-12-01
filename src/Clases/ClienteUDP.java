package Clases;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteUDP {
    private static final String ENCODING_TYPE = "UTF-8";

    public static void main(String[] args) {
        Pattern pat;
        Matcher mat;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int puerto = 3344;
        byte[] buffer;
        byte[] buffer2 = new byte[1024];
        int respuesta = 0;
        try {
            InetAddress direccionServidor = InetAddress.getByName("localhost");
            DatagramSocket cliente = new DatagramSocket();

            do {
                try {
                    System.out.println("""
                            Bienvenido a JamaicaBank que desea hacer?.
                            1. Darse de alta
                            2. Entrar a su portal
                            3. Salir""");
                    System.out.print("Res: ");
                    respuesta = Integer.parseInt(br.readLine());
                    switch (respuesta) {
                        case 1 -> {
                            int resIntroducirDatos;
                            do {
                                System.out.println("""
                                        Que desea hacer?
                                        1. Introducir datos
                                        2. Salir""");
                                System.out.print("Res: ");
                                resIntroducirDatos = Integer.parseInt(br.readLine());

                                if (resIntroducirDatos == 1) {
                                    System.out.println("Escriba su nombre: ");
                                    System.out.print("Nombre: ");
                                    String nombre = br.readLine();
                                    System.out.println("Escriba su apellido: ");
                                    System.out.print("Apellido: ");
                                    String apellido = br.readLine();
                                    System.out.println("Escriba su edad: ");
                                    System.out.print("Edad: ");
                                    int edad = Integer.parseInt(br.readLine());
                                    System.out.println("Escriba su email");
                                    System.out.print("Email: ");
                                    String email = br.readLine();
                                    pat = Pattern.compile("[a-zA-Z]@gmail.com$");
                                    mat = pat.matcher(email);
                                    if (mat.find()) {
                                        System.out.println("Escriba su usuario: ");
                                        System.out.print("Usuario: ");
                                        String usuario = br.readLine();
                                        System.out.println("Escriba su contraseña: ");
                                        System.out.println("Contraseña: ");
                                        String contra = br.readLine();
                                        byte[] contraHash = ServidorUdpHilo.getDigest(contra.getBytes(ENCODING_TYPE));
                                        ServidorUdpHilo.darAltaUsuario(nombre, apellido, edad, email, usuario, contraHash);
                                        System.out.println("Cliente dado de alta");
                                    } else {
                                        System.out.println("Tu email no es correcto 🥹");
                                    }


                                }
                            } while (resIntroducirDatos != 2);
                        }
                        case 2 -> {
                            int opAccesoPortal;
                            do {
                                System.out.println("1. Insertar credenciales\n" + "2. Atras");
                                System.out.print("Res: ");
                                opAccesoPortal = Integer.parseInt(br.readLine());
                                if (opAccesoPortal == 1) {
                                    System.out.println("Inserta tu usuario: ");
                                    System.out.println("Usuario: ");
                                    String usuario = br.readLine();
                                    if (comprobarUsuario(usuario)) {
                                        System.out.println("Escribe la contraseña: ");
                                        System.out.print("Contraseña: ");
                                        String contra = br.readLine();
                                        if (comprobarContra(contra)) {
                                            System.out.println("Bienvenido! " + usuario);
                                            
                                            System.out.println("""
                                                    Que deseas hacer?\s
                                                    1. Ver saldo.
                                                    2. Hacer una transferencia a otra cuenta.
                                                    3. Salir.
                                                    """);
                                        } else {
                                            System.out.println("La contraseña no se encuentra en el sistema de datos");
                                        }
                                    } else {
                                        System.out.println("El usuario no se encuentra en el sistema de datos!");
                                    }
                                }
                            } while (opAccesoPortal != 2);
                        }
                        case 3 -> System.out.println("Adios! 😁");
                        default -> System.out.println("Tiene que ser una opcion valida");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Tiene que ser un numero!");
                } catch (NoSuchAlgorithmException | ClassNotFoundException e) {
                    System.out.println("Error: " + e);
                }

            } while (respuesta != 3);

            System.out.println("Introduce mensaje: ");
            String mensaje = br.readLine();
            buffer = mensaje.getBytes();
            DatagramPacket paquete = new DatagramPacket(buffer, buffer.length, direccionServidor, puerto);
            System.out.println("Se envio el mensaje");
            cliente.send(paquete);


            DatagramPacket respuestaDa = new DatagramPacket(buffer2, buffer2.length);
            cliente.receive(respuestaDa);
            System.out.println("Hora recibida");
            mensaje = new String(respuestaDa.getData());
            System.out.println(mensaje.trim());

            cliente.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

    }

    public static boolean comprobarUsuario(String usuario) throws IOException, ClassNotFoundException {
        File fichero = new File(".//src/Ficheros/Clientes.dat");
        FileInputStream fileInputStream = new FileInputStream(fichero);
        ObjectInputStream objectInputStream = null;

        while (fileInputStream.available() != 0) {
            objectInputStream = new ObjectInputStream(fileInputStream);
            Cliente cLiente = (Cliente) objectInputStream.readObject();
            if (cLiente.getUsuario().equals(usuario)) {
                return true;
            }
        }
        assert objectInputStream != null;
        objectInputStream.close();
        fileInputStream.close();
        return false;
    }

    public static boolean comprobarContra(String contra) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        File fichero = new File(".//src/Ficheros/Clientes.dat");
        FileInputStream fileInputStream = new FileInputStream(fichero);
        ObjectInputStream objectInputStream = null;
        byte[] contraDeEntrada;
        byte[] contraDeOriginal;
        while (fileInputStream.available() != 0) {
            objectInputStream = new ObjectInputStream(fileInputStream);
            Cliente cLiente = (Cliente) objectInputStream.readObject();
            contraDeEntrada = ServidorUdpHilo.getDigest(contra.getBytes(ENCODING_TYPE));
            contraDeOriginal = cLiente.getContra();
            if (ServidorUdpHilo.compararResumenes(contraDeOriginal, contraDeEntrada)) {
                System.out.printf(cLiente.toString());
                return true;
            }
        }

        assert objectInputStream != null;
        objectInputStream.close();
        fileInputStream.close();

        return false;
    }
}
