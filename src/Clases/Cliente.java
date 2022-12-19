package Clases;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Javier Jamaica
 * 29/11/2022 - 20:37
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = -5402905524421154662L;
    private String nombre;
    private String apellido;
    private int edad;
    private String email;
    private String usuario;
    private byte[] contra;

    /**
     * Constructor de la clase cliente que recibe
     *
     * @param nombre   nombre del cliente
     * @param apellido apellido del cliente
     * @param edad     edad del cliente
     * @param email    correo electronico del cliente
     * @param usuario  el usuario que es unico
     * @param contra   contraseña que es hasheada
     */

    public Cliente(String nombre, String apellido, int edad, String email, String usuario, byte[] contra) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.contra = contra;
    }

    /**
     * Getter del atributo nombre
     *
     * @return devuelve el nombre del cliente en cuestion
     */

    public String getNombre() {
        return nombre;
    }

    /**
     * Setter del atributo nombre
     *
     * @param nombre recibe el nombre a asginar para el cliente en cuestion
     */

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter del atributo apellido
     *
     * @return devuelve el apellido del cliente en cuestion
     */

    public String getApellido() {
        return apellido;
    }

    /**
     * Setter del atributo apellido
     *
     * @param apellido recibe el apellido a asignar para el cliente en cuestion
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Getter del atributo edad
     *
     * @return devuelve la edad del cliente en cuestion
     */
    public int getEdad() {
        return edad;
    }

    /**
     * Setter del atributo edad
     *
     * @param edad recibe la edad a asiganar para el cliente en cuestion
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Getter del atributo email
     *
     * @return devuelve el correo electronico del cliente en cuestion
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setter del atributo email
     *
     * @param email recibe el correco electronico a asignar para el cliente en cuestion
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getter del atributo usuario
     *
     * @return devuelve el nombre de usuario del cliente en cuestion
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Setter del atributo usuario
     *
     * @param usuario recibe el nombre de usuario a asignar para el cliente en cuestion
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Getter del atributo contra
     *
     * @return devuelve la contraseña del cliente en cuestion siendo esta hasheada
     */
    public byte[] getContra() {
        return contra;
    }

    /**
     * Setter del atributo contra
     *
     * @param contra recibe la contraseña del cliente ya hasheada
     */
    public void setContra(byte[] contra) {
        this.contra = contra;
    }

    /**
     * ToString() funcion para ver los atributos
     *
     * @return devuelve cada atributo que este escrita dentro de ella
     */
    @Override
    public String toString() {
        return "Nombre: " + nombre + "\n" + "Apellidos: " + apellido + "\n" + "Edad: " + edad + "\n" + "Email: " + email + "\n"
                + "Usuario: " + usuario + "\n" + "Contraseña: " + Arrays.toString(contra);

    }
}
