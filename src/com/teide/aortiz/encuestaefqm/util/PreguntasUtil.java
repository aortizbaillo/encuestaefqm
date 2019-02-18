/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.util;

/**
 *
 * @author antonio
 */
public class PreguntasUtil {
    
    private static final String[] PROFESORES = {TituloPreguntas.PROFESORES_01,TituloPreguntas.PROFESORES_02,TituloPreguntas.PROFESORES_03,
        TituloPreguntas.PROFESORES_04,TituloPreguntas.PROFESORES_05,TituloPreguntas.PROFESORES_06,TituloPreguntas.PROFESORES_07,
        TituloPreguntas.PROFESORES_08,TituloPreguntas.PROFESORES_09,TituloPreguntas.PROFESORES_10};
    public static final String[] DIRECTIVO = {TituloPreguntas.DIRECTIVO_01,TituloPreguntas.DIRECTIVO_02,TituloPreguntas.DIRECTIVO_03,
        TituloPreguntas.DIRECTIVO_04};
    public static final String[] SECRETARIA = {TituloPreguntas.SECRETARIA_01,TituloPreguntas.SECRETARIA_02,TituloPreguntas.SECRETARIA_03,
        TituloPreguntas.SECRETARIA_04};
    public static final String[] ORIENTACION = {TituloPreguntas.ORIENTACION_01,TituloPreguntas.ORIENTACION_02,TituloPreguntas.ORIENTACION_03,
        TituloPreguntas.ORIENTACION_04, TituloPreguntas.ORIENTACION_05};
    
    /**
     * Este método permite devolver una cadena de texto que representará el nombre de la pregunta. Se obtendrá de las constantes especificadas
     * en el fichero TipoPreguntas.java
     * @param tipo representa el tipo de responsable (P,D,S,O)
     * @param num representa el número de pregunta
     * @return el texto de la pregunta
     */
    public static String obtenerTextoPregunta (String tipo, int num) {
        switch (tipo) {
            case "P": return PROFESORES[num-1]; 
            case "D": return DIRECTIVO[num-1]; 
            case "S": return SECRETARIA[num-1]; 
            case "O": return ORIENTACION[num-1];
        }
        return " - ";
        
    }
    
    
}
