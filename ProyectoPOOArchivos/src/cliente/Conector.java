/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Conector extends Thread{
    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    
    final int puerto = 10001;
    
    public Conector(String ip){
        try {
            s = new Socket (ip,this.puerto);
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeUTF("Conectado \n");
            
        } catch (Exception e) {
            System.out.println("Ha habido un fallo en el cliente "+e.getMessage());
        }
    }
    
    public void run(){
        
    }
    
    public void enviarMSG(String msg){
        
    }
    
    public void desconectar(){
        try {
            s.close();
            ss.close();
        } catch (Exception e) {
        }
    }
}
