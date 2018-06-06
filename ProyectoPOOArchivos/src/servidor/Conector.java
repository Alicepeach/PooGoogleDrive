/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import cliente.Cliente;

public class Conector extends Thread{
    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    
    private ArrayList<Cliente> clientes;
    final int puerto = 9999;
    
    public Conector() {
        try {
            ss = new ServerSocket(puerto);
            s = ss.accept();
            
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());
            
        } catch (IOException e) {
            System.out.println("Ocurrio un error al crear el socket servidor "+ e.getMessage());
        }
    }
}
