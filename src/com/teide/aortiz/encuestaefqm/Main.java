/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm;

import com.teide.aortiz.encuestaefqm.util.DataExtraction;
import com.teide.aortiz.encuestaefqm.bean.PreguntaBean;
import com.teide.aortiz.encuestaefqm.bean.bbdd.DataBaseUtil;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author antonio
 */
public class Main {
    
    public static final String RUTA = "/Users/antonio/Dropbox/2DAM.csv";
    public static final String CURSO = "13/14";
    
    public static void main(String[] args) {
        
        try {
            //Este objeto nos permitirá realizar todas las acciones sobre BBDD
            DataBaseUtil dbu = new DataBaseUtil();
            
            DataExtraction de = new DataExtraction(new File (RUTA), CURSO);
            
            //En primer lugar insertaremos los responsables genéricos
            dbu.insertaResponsablesGenericos();            
            
            //Analizamos el CSV en busca de los responsables que se han encuestado en ese ciclo y curso
            de.analizaResponsables();

            //Insertamos el ciclo y curso que vamos a analizar
            dbu.insertarCiclo(de.getCiclo(), de.getCurso());
            
            //Insertamos los responsables
            dbu.insertarResponsables(de.getNombresAnalizados());
            
            //Insertamos los encuestados
            dbu.insertarEncuestados(de.getNombresAnalizados(), de.getCiclo(), de.getCurso());

            //Insertamos y analizamos todas las respuestas
            de.analizarRespuestas(dbu);
            
            //Insertamos todas las medias de las preguntas tipo Likert
            dbu.insertarMedias(de.getCiclo(), de.getCurso());
            
            //Insertamos los porcentajes de las respuestas tipo SI/NO
            dbu.insertaPorcentajes(de.getCiclo(), de.getCurso());
        }
        catch (ClassNotFoundException e) {
            System.out.println("Error de driver");
        }
        catch (SQLException e) {
            System.out.println("Error de BBDD: "+e.getMessage());
        }
        catch (Exception e) {
            System.out.println("Error al leer el CSV: "+e.getMessage());
        }
        
        
    }
    
}
