/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm;

import com.teide.aortiz.encuestaefqm.bean.PreguntaBean;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author antonio
 */
public class Main {
    
    public static final String RUTA = "/Users/antonio/Dropbox/2DAM.csv";
    
    public static void main(String[] args) throws Exception {
        DataExtraction de = new DataExtraction(new File (RUTA));
        de.analizaResponsables();
        System.out.println("Profesores");
        for (String profe : de.getNombreAnalizado("P")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Equipo Directivo");
        for (String profe : de.getNombreAnalizado("D")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Secretaría");
        for (String profe : de.getNombreAnalizado("S")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Orientación");
        for (String profe : de.getNombreAnalizado("O")) {
            System.out.println(profe);
        }
        System.out.println("****************************************");
        
        System.out.println("Profesores");
        for (PreguntaBean profe : de.getPregunta("P")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Equipo Directivo");
        for (PreguntaBean profe : de.getPregunta("D")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Secretaría");
        for (PreguntaBean profe : de.getPregunta("S")) {
            System.out.println(profe);
        }
        System.out.println("-----------------------------");
        
        System.out.println("Orientación");
        for (PreguntaBean profe : de.getPregunta("O")) {
            System.out.println(profe);
        }
        System.out.println("*****************************************");
        
        de.analizarRespuestas();
    }
    
}
