package Clases;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteTCP {
    private static final String ENCODING_TYPE = "UTF-8";

    public static void main(String[] args) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Pattern pat;
        Matcher mat;


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int puerto = 3344;
        int respuesta = 0;
        try {

            Socket socket = new Socket("localhost", puerto);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("Leemos la clave");
            //obtenemos la clave publica
            PublicKey clave = (PublicKey) ois.readObject();
            System.out.println("La clave recibida es: " + clave);


            do {

                try {

                    System.out.println("""
                            Bienvenido a JamaicaBank que desea hacer?.
                            1. Darse de alta
                            2. Entrar a su portal
                            3. Salir""");
                    System.out.print("Res: ");
                    respuesta = Integer.parseInt(br.readLine());
                    byte[] opSer = cifrarDatosAlta(String.valueOf(respuesta), clave);
                    oos.writeObject(opSer);
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
                                    byte[] nombreSer = cifrarDatosAlta(nombre, clave);

                                    System.out.println("Escriba su apellido: ");
                                    System.out.print("Apellido: ");
                                    String apellido = br.readLine();
                                    byte[] apellidoSer = cifrarDatosAlta(apellido, clave);

                                    System.out.println("Escriba su edad: ");
                                    System.out.print("Edad: ");
                                    int edad = Integer.parseInt(br.readLine());
                                    byte[] edadSer = cifrarDatosAlta(String.valueOf(edad), clave);
                                    boolean emailComp = false;
                                    byte[] emailSer;
                                    do {
                                        System.out.println("Escriba su email");
                                        System.out.print("Email: ");
                                        String email = br.readLine();
                                        emailSer = cifrarDatosAlta(email, clave);

                                        pat = Pattern.compile("[a-zA-Z]@gmail.com$");
                                        mat = pat.matcher(email);
                                        if (mat.find()) {
                                            emailComp = true;
                                        } else {
                                            System.out.println("Tu email no es valido! 🥹");
                                        }
                                    } while (!emailComp);


                                    System.out.println("Escriba su usuario: ");
                                    System.out.print("Usuario: ");
                                    String usuario = br.readLine();
                                    byte[] usuarioSer = cifrarDatosAlta(usuario, clave);
                                    if (ServidorTcpHilo.comprobrarUsuario(usuario)) {
                                        System.out.println("Este nombre de usuario ya esta en uso!");
                                    } else {
                                        System.out.println("Escriba su contraseña: ");
                                        System.out.println("Contraseña: ");
                                        String contra = br.readLine();
                                        byte[] contraHash = ServidorTcpHilo.getDigest(contra.getBytes(ENCODING_TYPE));
                                        oos.writeObject(nombreSer);
                                        oos.writeObject(apellidoSer);
                                        oos.writeObject(edadSer);
                                        oos.writeObject(emailSer);
                                        oos.writeObject(usuarioSer);
                                        oos.writeObject(contraHash);
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
                                            int rCliente;
                                            System.out.println("Bienvenido! " + usuario);
                                            do {
                                                System.out.println("""
                                                        Que deseas hacer?\s
                                                        1. Ver saldo.
                                                        2. Hacer una transferencia a otra cuenta.
                                                        3. Ingresar/Retirar dinero
                                                        4. Salir.""");
                                                System.out.print("R: ");
                                                rCliente = Integer.parseInt(br.readLine());
                                                switch (rCliente) {
                                                    case 1:
                                                        ServidorTcpHilo.verSaldo(usuario);
                                                        break;
                                                    case 2:
                                                        break;
                                                    case 3:
                                                        int rIngresar;
                                                        do {
                                                            System.out.println("""
                                                                    Que deseas hacer?
                                                                    1. Ingresar dinero
                                                                    2. Retirar dinero
                                                                    3. Salir""");
                                                            rIngresar = Integer.parseInt(br.readLine());
                                                            switch (rIngresar) {
                                                                case 1:
                                                                    byte[] ingresarId = cifrarDatosIngresarRetirar("I", clave);
                                                                    oos.writeObject(ingresarId);
                                                                    byte[] usuSer = cifrarDatosIngresarRetirar(usuario, clave);
                                                                    oos.writeObject(usuSer);
                                                                    System.out.println("Escribe la cantidad a ingresar:");
                                                                    int dinero = Integer.parseInt(br.readLine());
                                                                    byte[] dineroSer = cifrarDatosIngresarRetirar(String.valueOf(dinero), clave);
                                                                    oos.writeObject(dineroSer);
                                                                case 2:
                                                                    byte[] retirarId = cifrarDatosIngresarRetirar("I", clave);
                                                                    oos.writeObject(retirarId);
                                                                    System.out.println("Escribe la cantidad a retirar:");
                                                                case 3:
                                                                default:
                                                                    System.out.println("Tiene que ser una opcion valida");
                                                            }
                                                        } while (rIngresar != 3);
                                                        break;
                                                    case 4:
                                                        break;
                                                    default:
                                                        System.out.println("Tiene que ser una opcion valida");
                                                }
                                            } while (rCliente != 4);

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

        } catch (IOException e) {
            System.out.println("Error: " + e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] cifrarDatosAlta(String dato, PublicKey clave) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            //directamente cifrarlo en un array de bytes, y no hacer conversiones a string
            byte[] mensaje = cipher.doFinal(dato.getBytes());
            return mensaje;

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }

    }

    public static byte[] cifrarDatosIngresarRetirar(String dato, PublicKey clave) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, clave);
            //directamente cifrarlo en un array de bytes, y no hacer conversiones a string
            byte[] mensaje = cipher.doFinal(dato.getBytes());
            return mensaje;

        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
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
            contraDeEntrada = ServidorTcpHilo.getDigest(contra.getBytes(ENCODING_TYPE));
            contraDeOriginal = cLiente.getContra();
            if (ServidorTcpHilo.compararResumenes(contraDeOriginal, contraDeEntrada)) {
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
