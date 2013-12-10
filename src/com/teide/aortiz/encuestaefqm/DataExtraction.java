package com.teide.aortiz.encuestaefqm;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 *
 * @author emilio
 */
public class DataExtraction {

    public static final String[] TIPOS_USUARIOS_ANALIZADOS = {"P","D","S","O"};
    public static final int NUM_INICIAL = 9; //Los anteriores valores de columnas no son útiles
    private ArrayList<String>[] nombresAnalizados;
    private ArrayList<String> preguntas;
    private File fichero;
    
    public DataExtraction(File fichero) {
        this.fichero = fichero;
       
        //Generamos nuestro array de ArrayList con todos los tipos de usuarios analizados
        this.nombresAnalizados = new ArrayList[TIPOS_USUARIOS_ANALIZADOS.length];
        for (int i = 0; i < nombresAnalizados.length; i++) {
            nombresAnalizados[i] = new ArrayList<>();
        }
    }
    
    /**
     * Este método permite obtener si el elemento analizado es un profesor, un elemento del equipo directivo,
     * secretaría u orientación
     * @param campo representa el campo a analizar
     * @return 0 si es un profesor, 1 si es equipo directivo, 2 si es secretaría, 3 si es orientación o -1 en caso contrario
     */
    private int obtenerPosicionTipoAnalizado (String campo) {
        for (int i = 0; i < TIPOS_USUARIOS_ANALIZADOS.length; i++) {
            int pos = campo.indexOf("_");
            if (campo.substring(pos+1, pos+2).contains(TIPOS_USUARIOS_ANALIZADOS[i])) return i;
        }
        return -1;
    }

    public void analizaResponsables() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fichero));
        String cadena = br.readLine();
        String[] campos = cadena.split("\t");
        int i = NUM_INICIAL;
        for (; i < campos.length; i++) {
            //Solo tenemos que recoger nombres si el elmento contiene un >
            if (campos[i].contains(">")) {
                int pos = obtenerPosicionTipoAnalizado(campos[i]);
                if (pos!=-1) {                   
                    if (!nombresAnalizados[pos].contains((campos[i].split(">"))[1]))  nombresAnalizados[pos].add((campos[i].split(">"))[1]);
                }
            }
            
        }
        br.close();
    }
    
    
    
    /*

    private void numberQuestionSearch(File f) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String cadena = br.readLine();
        String[] campos = cadena.split("\t");
        this.questionNumber = new ArrayList<>();
        int i = NUM_INICIAL; // Leemos a partir del 9 los profes
        while (i < campos.length) {
            this.questionNumber.add((campos[i].split("_"))[0]);
            i = i + profes.size();
        }
        br.close();
    }

    public String fileNameSearch(File f) {
        return (f.getName().split("\\."))[0];
    }
    
    /*

    public ArrayList<Respuesta> rowExtraction(File f, String year) throws Exception {
      
        numberQuestionSearch(f);

        BufferedReader br = new BufferedReader(new FileReader(f));
        String cadena = br.readLine();
        ArrayList<Respuesta> resArrayList = new ArrayList<>();

        // lectura de la primera fila

        while ((cadena = br.readLine()) != null)  {
            if (!cadena.isEmpty()) {
                String[] campos = cadena.split("\t");
                int j=0;
                for (int i = NUM_INICIAL; i < campos.length - 1; i++) {
                    // completamos el objetos
                    // ROOT    if (profes.get(j).startsWith("Luis")) val = "4";
                    Respuesta res = new Respuesta();
                    res.setNombreProfesor(profes.get(j++));
                    res.setNombreCurso(this.fileNameSearch(f));
                    res.setPregunta(questionNumber.get((i-9)/profes.size()));
                    res.setValoracion(campos[i]);
                    res.setYear(year);
                    //Meter la respuesta al ArrayList
                    resArrayList.add(res);
                    if (j==profes.size()) j=0;
                }
                //Para la última pregunta del cuestionario ... Respuesta del alumno AL CURSO, no por profesor
                Respuesta res = new Respuesta();
                res.setNombreProfesor(this.fileNameSearch(f));
                res.setNombreCurso(this.fileNameSearch(f));
                res.setPregunta(questionNumber.get(questionNumber.size()-1));
                res.setValoracion(campos[campos.length-1]);
                //Meter la respuesta al ArrayList
                resArrayList.add(res);
            }
        }

        return resArrayList;
    }
    */

    public ArrayList<String>[] getNombresAnalizados() {
        return nombresAnalizados;
    }
    
    public ArrayList<String> getNombreAnalizado (String campo) {
        int pos = obtenerPosicionTipoAnalizado(campo);
        if (pos!=-1) return nombresAnalizados[pos];
        return null;
    }
}
