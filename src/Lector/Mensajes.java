/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Lector;

/**
 *
 * @author vaio
 */
import javax.swing.JOptionPane;

public final class Mensajes {
 
 /**
  * Lanza un dialogo de mensaje de una línea:
  */
 String mensaje;   
 public void Mensajes(String msj){
     this.mensaje=msj;
 }
 public void setMsj(String msj){
     this.mensaje=msj;
     
 }
    
 public void lanzarMensaje(){
  //En la siguiente línea está la magia (es lo que muestra el mensaje).
  
  
  JOptionPane.showMessageDialog(null, mensaje);
 }
 


    
 
}