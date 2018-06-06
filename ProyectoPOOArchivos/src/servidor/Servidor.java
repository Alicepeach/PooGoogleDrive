/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import cliente.Cliente;
import cliente.vistaCliente;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author alice
 */
public class Servidor extends Thread{
    public static Conector servidor;
    private static ArrayList<vistaCliente> clientes = new ArrayList<vistaCliente>();
    //Método para leer archivo con los usuarios
    private final String nombreCliente;
    private BufferedReader entrada;
    private PrintStream escribir;
    private final vistaCliente emisor;
    
    public Servidor(vistaCliente cliente) throws IOException{
        
        this.emisor = cliente;
        Socket s = cliente.getSocket();
        entrada = new BufferedReader(new InputStreamReader(s.getInputStream()));
        nombreCliente = s.getInetAddress().getCanonicalHostName();
        System.out.println("Conexión aceptada desde " +s.getRemoteSocketAddress());
        escribir = new PrintStream(s.getOutputStream());
    
        
    }
    
    public static void main(String[] args) throws IOException {
      
    ServerSocket ss = null;
    Socket cliente = null;
    String nombre = null;
    try{
        ss = new ServerSocket(0);
        System.out.println("Servidor aceptando conexiones en puerto" + ss.getLocalPort());
    }catch(IOException e){
        //Logger.getLogger(Servidor.class.getName()).log(Level.SERVERE, null, ex);
    }
    while(true){
        try{
            cliente = ss.accept();
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            nombre = entrada.readLine();
            boolean temp = seEncuentraElCliente(nombre);
            PrintStream escribir = new PrintStream(cliente.getOutputStream());
            escribir.println(String.valueOf(temp).toLowerCase());
            escribir.flush();
            if(!temp){
            vistaCliente c = new vistaCliente(cliente, nombre);
            clientes.add(c);
            Servidor hilo = new Servidor(c);
            hilo.start();
            }
            }catch(IOException ex){
                
            }
        }
    }
    
    public static boolean seEncuentraElCliente(String nombre){
        for(vistaCliente vc : clientes){
            if(vc.getNombre().equals(nombre)){
                return true;
            }
        }
        return false;
    }
}
    
    
