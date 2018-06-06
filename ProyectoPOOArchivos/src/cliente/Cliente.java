/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.Socket;
import servidor.Vista;

/**
 *
 * @author alice
 */
public class Cliente{
    private String user;
    private String password;
    Vista vista = new Vista();
    //Constructor por defecto
    public Cliente(Socket s){
    }
    
    public Cliente(String user, String password){
        this.user = user;
        this.password = password;
    }
    
    void solicitaDatosLogin(){
        vista.txtUser.setText(user);
    }
    
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
