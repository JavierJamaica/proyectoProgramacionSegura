package Clases;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
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
                    System.out.println("Enviamos opcion");
                    oos.writeObject(opSer);
                    switch (respuesta) {

                        case 1 -> {

                            boolean check;
                            System.out.println("Leemos mensaje");
                            String mensaje = ois.readObject().toString();
                            System.out.println(mensaje);
                            System.out.println(terminosBanco());

                            System.out.println("Si/No");
                            System.out.print("R: ");
                            String r = br.readLine();
                            byte[] resNormas = cifrarDatosAlta(r, clave);
                            if (r.equalsIgnoreCase("si")) {
                                System.out.println("Enviamos respuesta normas");
                                oos.writeObject(resNormas);

                                Signature verificadsa = Signature.getInstance("SHA1WITHRSA");
                                verificadsa.initVerify(clave);

                                verificadsa.update(mensaje.getBytes());
                                byte[] firma = (byte[]) ois.readObject();
                                check = verificadsa.verify(firma);
                                System.out.println("Normas firmadas");
                            } else {
                                check = false;
                            }
                            if (check) {
                                int resIntroducirDatos = 0;

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
                                    boolean edadComp;
                                    byte[] edadSer;
                                    do {
                                        System.out.println("Escriba su edad: ");
                                        System.out.print("Edad: ");
                                        int edad = 0;
                                        try {
                                            edad = Integer.parseInt(br.readLine());
                                            edadComp = true;
                                        } catch (NumberFormatException e) {
                                            System.out.println("La edad tiene que ser un numero!");
                                            edadComp = false;
                                        }


                                        edadSer = cifrarDatosAlta(String.valueOf(edad), clave);
                                    } while (!edadComp);

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
                            } else {
                                oos.writeObject(resNormas);
                                System.out.println("No se aceptaron las normas");
                            }


                        }
                        case 2 -> {
                            int opAccesoPortal;
                            do {
                                System.out.println("1. Insertar credenciales\n" + "2. Atras");
                                System.out.print("Res: ");
                                opAccesoPortal = Integer.parseInt(br.readLine());
                                byte[] accCif = cifrarDatosAlta(String.valueOf(opAccesoPortal), clave);
                                oos.writeObject(accCif);
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
                                                byte[] opPor = cifrarDatosAlta(String.valueOf(rCliente), clave);
                                                oos.writeObject(opPor);
                                                switch (rCliente) {
                                                    case 1:
                                                        ServidorTcpHilo.verSaldo(usuario);
                                                        break;
                                                    case 2:
                                                        int rTransfer;
                                                        do {
                                                            System.out.println("1. Hacer transferencia\n" + "2. Ver cuentas\n" + "3. Salir");
                                                            System.out.print("R: ");
                                                            rTransfer = Integer.parseInt(br.readLine());
                                                            byte[] transferCif = cifrarDatosIngresarRetirar(String.valueOf(rTransfer), clave);
                                                            oos.writeObject(transferCif);
                                                            switch (rTransfer) {
                                                                case 1:

                                                                    System.out.println("Escribe el numero de cuenta a donde deseas hacer la transferencia: ");
                                                                    System.out.print("Cuenta: ");
                                                                    String nCuentaTrans = br.readLine();
                                                                    byte[] nCuentaCif = cifrarDatosAlta(nCuentaTrans, clave);
                                                                    System.out.println("Enviamos Cuenta");
                                                                    oos.writeObject(nCuentaCif);
                                                                    if (ois.readObject().toString().equals("false")) {
                                                                        System.out.println("La cuenta no existe");
                                                                    } else {
                                                                        System.out.println("Escribe la cantidad que vas a enviar: ");
                                                                        System.out.print("Cantidad: ");
                                                                        int dineroTran = Integer.parseInt(br.readLine());
                                                                        byte[] dineroTranCif = cifrarDatosAlta(String.valueOf(dineroTran), clave);

                                                                        byte[] usuCifTran = cifrarDatosAlta(usuario, clave);

                                                                        System.out.println("Enviamos Cantidad");
                                                                        oos.writeObject(dineroTranCif);
                                                                        oos.writeObject(usuCifTran);
                                                                    }


                                                                    break;
                                                                case 2:
                                                                    ServidorTcpHilo.leerCuenta();
                                                                    break;
                                                                case 3:
                                                                    break;
                                                                default:
                                                            }
                                                        } while (rTransfer != 3);
                                                        break;
                                                    case 3:
                                                        int rIngresar;
                                                        do {
                                                            System.out.println("""
                                                                    Que deseas hacer?
                                                                    1. Ingresar dinero
                                                                    2. Retirar dinero
                                                                    3. Salir""");
                                                            System.out.print("R: ");
                                                            rIngresar = Integer.parseInt(br.readLine());
                                                            switch (rIngresar) {
                                                                case 1:
                                                                    byte[] ingresarId = cifrarDatosIngresarRetirar(String.valueOf(rIngresar), clave);
                                                                    oos.writeObject(ingresarId);
                                                                    byte[] usuSer = cifrarDatosIngresarRetirar(usuario, clave);
                                                                    oos.writeObject(usuSer);
                                                                    System.out.println("Escribe la cantidad a ingresar:");
                                                                    int dinero = Integer.parseInt(br.readLine());
                                                                    byte[] dineroSer = cifrarDatosIngresarRetirar(String.valueOf(dinero), clave);
                                                                    oos.writeObject(dineroSer);
                                                                    break;
                                                                case 2:
                                                                    byte[] retirarId = cifrarDatosIngresarRetirar(String.valueOf(rIngresar), clave);
                                                                    oos.writeObject(retirarId);
                                                                    byte[] usuSer2 = cifrarDatosIngresarRetirar(usuario, clave);
                                                                    oos.writeObject(usuSer2);
                                                                    System.out.println("Escribe la cantidad a retirar:");
                                                                    int dinero2 = Integer.parseInt(br.readLine());
                                                                    byte[] dineroSer2 = cifrarDatosIngresarRetirar(String.valueOf(dinero2), clave);
                                                                    oos.writeObject(dineroSer2);
                                                                    break;
                                                                case 3:
                                                                    byte[] salirId = cifrarDatosIngresarRetirar(String.valueOf(rIngresar), clave);
                                                                    oos.writeObject(salirId);
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
                } catch (SignatureException e) {
                    throw new RuntimeException(e);
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

    public static String terminosBanco() {
        return "Para ello, cuenta con diversas políticas, códigos y normativa interna, que se inspiran en las mejores prácticas y protocolos internacionales, códigos de conducta y guías internacionales aplicables en cada materia.\n" +
                "El contenido de estas políticas constituye un proceso de mejora continua. Anualmente Santander lleva a cabo una revisión de sus políticas corporativas de sostenibilidad, de aplicación a todo el Grupo. Estas políticas son aprobadas por el consejo de administración del Grupo, indicando en la política la fecha de la última actualización.";
    }
}
