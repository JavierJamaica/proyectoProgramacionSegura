package Clases;

import java.io.Serializable;

/**
 * @author Javier Jamaica
 * 01/12/2022 - 1:10
 */
public class Cuenta implements Serializable {
    private int saldo;
    private String numeroCuenta;
    private Cliente cliente;

    /**
     * Constructor de la clase Cuenta
     *
     * @param saldo        recibe como parametro una cantidad de dinero que es el saldo
     * @param numeroCuenta el numero de cuenta que es unico
     * @param cliente      un cliente al que asociar con esta cuenta
     */
    public Cuenta(int saldo, String numeroCuenta, Cliente cliente) {
        this.saldo = saldo;
        this.numeroCuenta = numeroCuenta;
        this.cliente = cliente;
    }

    /**
     * Getter del atributo saldo de la cuenta en cuestion
     *
     * @return devuelve el saldo
     */
    public int getSaldo() {
        return saldo;
    }

    /**
     * Setter del atributo saldo
     *
     * @param saldo recibe un saldo que sera asignado a la cuenta en cuestion
     */
    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    /**
     * Getter del atributo numero cuenta
     *
     * @return devuelve el numero de cuenta de la cuenta en cuestion
     */
    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    /**
     * Setter del numero de cuenta
     *
     * @param numeroCuenta recibe un numero de cuenta para asignar en la cuenta en cuestion
     */
    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    /**
     * Getter del cliente asociado a la cuenta
     *
     * @return devuelce un objeto de tipo cliente de la cuenta en cuestion
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Setter del cliente de la cuenta en cuestion
     *
     * @param cliente recibe un objeto cliente para asignar a la cuenta
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    /**
     * Visualiza los atributos que esten en ella
     *
     * @return devuelve los atributos que esten escritos en ella para visualizarlos
     */
    @Override
    public String toString() {
        return "Cuenta: " + numeroCuenta + ", Saldo: " + saldo + "\n"
                + "Cliente asociado: " + cliente;
    }
}
