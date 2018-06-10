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
                //Si la conexión se realiza correctamente...
                cliente = ss.accept();
                entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                nombre = entrada.readLine();
                System.out.println(nombre.substring(0,nombre.indexOf("@")).equals("registro"));
                //Si la cadena que recibe tiene el siguiente formato, vamos a realizar las siguientes acciones
                if(nombre.substring(0,nombre.indexOf("@")).equals("registro")){ 
                    try {
                        //Recibimos la información al servidor a través de un PrintStream  
                        String user = nombre.substring(nombre.indexOf("@")+1, nombre.indexOf(":"));
                        String password = nombre.substring(nombre.indexOf(":")+1);
                        //System.out.println(user + " \n "+ password);
                        String ruta = "C:\\Users\\alice\\Desktop\\Servidor\\UsuariosExistentes.txt";
                        try{
                            //Si no encuentra el usuario en el archivo, significa que no se repite.
                            if(!getUsuario(user)){
                                //True verifica si el archivo existe o no, si no, lo crea.
                                archivo = new File(ruta);
                                fw = new FileWriter(archivo, true);
                                bw = new BufferedWriter(fw);
                                //Escribimos el usuario seguido de dos puntos la contraseña en una sola linea
                                bw.write(user + ":" + password);
                                bw.newLine();
                                //Limpiamos el flujo
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
                    //Buscamos en el arreglo el usuario. Si se encuentra, el valor de temp es true.
                    boolean temp = seEncuentraElCliente(nombre);
                    PrintStream escribir = new PrintStream(cliente.getOutputStream());
                    escribir.println(String.valueOf(temp).toLowerCase());
                    escribir.flush();
                    //Si no está en el archivo, significa que es nuevo, así que lo agrega
                    if(!temp){
                        vistaCliente c = new vistaCliente(cliente, nombre);
                        clientes.add(c);
                        Servidor hilo = new Servidor(c);
                        hilo.start();
                    }
                }
              
                if(nombre.substring(0,nombre.indexOf("@")).equals("logueo")){
                    String user = nombre.substring(nombre.indexOf("@")+1, nombre.indexOf(":"));
                    String password = nombre.substring(nombre.indexOf(":")+1);
                    System.out.println("Buscando" + user + " \n "+ password);
                    String ruta = "C:\\Users\\alice\\Desktop\\Servidor\\UsuariosExistentes.txt";
                    try{
                        if(obtenDatos(user, password)){
                            
                            //InputStreamReader  entradaSocket = new InputStreamReader(cliente.getInputStream());
                            //entrada = new BufferedReader(entradaSocket);
                            //DataOutputStream salida = new DataOutputStream(cliente.getOutputStream());
                            
                            PrintStream escribir = new PrintStream(cliente.getOutputStream());
                            
                            escribir.println(user);
                            escribir.flush();
                            vistaCliente vc = new vistaCliente(cliente,user);
                            vc.setVisible(true);
                            Thread a = new Thread(vc);
                            a.start();
                        }else{
                            System.out.println("El usuario no existe");
                        }
                    }catch(Exception e) {
                        System.out.println("Ha habido un fallo en el cliente "+e.getMessage());
                    }
                    System.out.println("Bienvenido : " + user);
                       // break;
                }/*else{
                    entrada=br.readLine();
                }*/
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
       //Si existe el cliente en el arreglo, retorna true.
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
    
    //Esta función verifica si el usuario existe. Si no, retorna un false
    private static boolean getUsuario(String user){
	try{
            String ruta = "usuariosExistentes.txt";
            FileReader fr=new FileReader(ruta);
            BufferedReader br=new BufferedReader(fr);
            String datos=br.readLine();
            while(datos!=null){
                /*De la cadena de datos, obtenemos sólo el usuario. No queremos la contraseña
                porque ésta puede repetirse*/
                    if(datos.substring(0,datos.indexOf(":")).equals(user)){
                            return true;
                    }else{
                            datos=br.readLine();
                    }
            }
        }catch(Exception e){}
        return false;
    }
    

    private static boolean obtenDatos(String user,String pass){
		try{
                    String ruta = "usuariosExistentes.txt";
                    FileReader fr=new FileReader(ruta);
                    BufferedReader br=new BufferedReader(fr);
                    String datos=br.readLine();
		while(datos!=null){
			if(datos.equals(user+":"+pass)){
				return true;
			}else{
				datos=br.readLine();
			}
		}
		}catch(Exception e){}
		return false;
	}
    
    
}
    
    
