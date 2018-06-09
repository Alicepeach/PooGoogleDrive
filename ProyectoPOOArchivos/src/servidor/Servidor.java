/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;
import cliente.Cliente;
import cliente.vistaCliente;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
        System.out.println("Conexion aceptada desde" + nombreCliente);
        escribir = new PrintStream(s.getOutputStream());    
    }
    
    
    public static void main(String[] args) throws Exception {
        ServerSocket ss = null;
        Socket cliente = null;
        String nombre = null;

        //Primero realizamos la conexión
        try{
            ss = new ServerSocket(0);
            System.out.println("Servidor aceptando conexiones en puerto" + ss.getLocalPort());
        }catch(IOException e){
            //Logger.getLogger(Servidor.class.getName()).log(Level.SERVERE, null, ex);
        }
        File archivo;
        FileWriter fw;
        BufferedWriter bw;
        BufferedReader entrada;
        while(true){
            try{
                cliente = ss.accept();
                entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                nombre = entrada.readLine();
                System.out.println(nombre.substring(0,nombre.indexOf("@")).equals("registro"));
                if(nombre.substring(0,nombre.indexOf("@")).equals("registro")){
                    
                    try {
                        //Mandamos la información al servidor a través de un PrintStream
                        
                        String user = nombre.substring(nombre.indexOf("@")+1, nombre.indexOf(":"));
                        String password = nombre.substring(nombre.indexOf(":")+1);
                        System.out.println(user + " \n "+ password);
                        String ruta = "/home/emanuel/Documentos/UsuariosExistentes.txt";
                        try{
                            if(!getUsuario(user)){
                                archivo = new File(ruta);
                                //True verifica si el archivo existe o no, si no, lo crea.
                                fw = new FileWriter(archivo, true);
                                bw = new BufferedWriter(fw);

                                bw.write(user + ":" + password);
                                bw.newLine();
                                bw.flush();
                                bw.close();
                                fw.close();
                                System.out.println("Se registro un nuevo usuario: " + user);
                                entrada.close();
                            }
                            else{
                            }
                        }catch(IOException ioe){}
                    }catch(Exception e){}
                }else{
                    boolean temp = seEncuentraElCliente(nombre);
                    PrintStream escribir = new PrintStream(cliente.getOutputStream());
                    escribir.println(String.valueOf(temp).toLowerCase());
                    escribir.flush();
                    //Si se encuentra el cliente, entonces se agrega al arreglo de clientes
                    if(!temp){
                        vistaCliente c = new vistaCliente(cliente, nombre);
                        clientes.add(c);
                        Servidor hilo = new Servidor(c);
                        hilo.start();
                    }
                }
            }catch(Exception e){}
        }
    }
    
    public static vistaCliente getCliente(String nom){
        for(vistaCliente temp:clientes){
            if(temp.getNombre().equals(nom)){
                System.out.println("Cliente: " +temp+ " Nombre : " +temp.getNombre());
                return temp;
            }
        }
        return null;
    }
    
    public static boolean seEncuentraElCliente(String nombre){    
       return clientes.contains(getCliente(nombre));
    }
     
   /* public void recibirArchivo() throws Exception{
        DataOutputStream output;
        BufferedInputStream bis;
        BufferedOutputStream bos;

        byte[] receivedData;
        int in;
        String file;
        //Buffer de 1024 bytes
            receivedData = new byte[1024];
            bis = new BufferedInputStream(connection.getInputStream());
            DataInputStream dis=new DataInputStream(connection.getInputStream());

            //Recibimos el nombre del fichero
            file = dis.readUTF();
            file = file.substring(file.indexOf('\\')+1,file.length());

            //Para guardar fichero recibido
            bos = new BufferedOutputStream(new FileOutputStream(file));
            while ((in = bis.read(receivedData)) != -1){
            bos.write(receivedData,0,in);
            }
    }*/
    
    @Override
    public void run(){
        try{
            String cadena;
            do{
                cadena = recibirDelCliente();
                ejecutarAccion(cadena);
            }while(!cadena.equals("Salir"));
        }catch(IOException ioe){
            eliminar();
        }finally{
            try{
                cerrarFlujo();
            }catch(IOException e) {
                System.out.println("Error al cerrar");
            }
        }
    }
    
    public String recibirDelCliente() throws IOException{
        String str = entrada.readLine();
        return str;
    }
    
    public void ejecutarAccion(String cadena){
           System.out.println("Soy la clase fregona :v");
    }
    
    
    public void cerrarFlujo() throws IOException{
        entrada.close();
    }
    
    
    public void eliminar(){
        clientes.remove(emisor);
       // enviarMensaje(emisor.getNombre()+"Ha salido");
        try{
            cerrarFlujo();
        }catch(IOException e){
            System.out.println("Error al cerrar");
        }
        this.destroy();
    }
    
    private static boolean getUsuario(String user){
	try{
            String ruta = "usuariosExistentes.txt";
            FileReader fr=new FileReader(ruta);
            BufferedReader br=new BufferedReader(fr);
            String datos=br.readLine();
            while(datos!=null){
                    if(datos.substring(0,datos.indexOf(":")).equals(user)){
                            return true;
                    }else{
                            datos=br.readLine();
                    }
            }
        }catch(Exception e){}
        return false;
    }
    
    
}
    
    
