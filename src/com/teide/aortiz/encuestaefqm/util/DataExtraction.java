package com.teide.aortiz.encuestaefqm.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.teide.aortiz.encuestaefqm.bean.PreguntaBean;
import com.teide.aortiz.encuestaefqm.bean.bbdd.DataBaseUtil;
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
    public static final String SI = "1";
    public static final String NO = "0";
    
    private int[] respuestaPorCliente;
    private ArrayList<String>[] nombresAnalizados;
    private ArrayList<PreguntaBean>[] preguntas;
    private File fichero;
    private String ciclo, curso;
    
    
    public DataExtraction(File fichero, String curso) {
        this.fichero = fichero;
        this.curso = curso;
        this.ciclo = obtenerNombreFichero(fichero);
       
        //Generamos nuestro array de ArrayList con todos los tipos de usuarios analizados
        this.nombresAnalizados = new ArrayList[TIPOS_USUARIOS_ANALIZADOS.length];
        this.preguntas = new ArrayList[TIPOS_USUARIOS_ANALIZADOS.length];

        for (int i = 0; i < nombresAnalizados.length; i++) {
            nombresAnalizados[i] = new ArrayList<>();
            preguntas[i] = new ArrayList<>();
        }
    }
    
    /**
     * Este método permite obtener el nombre del fichero CSV proporcionado. 
     * @param f El fichero CSV
     * @return el nombre del fichero, que coincidirá con el nombre del ciclo a analizar
     */
    private String obtenerNombreFichero (File f) {
        return (f.getName().split("\\."))[0];
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

    /**
     * Este método permite obtener nuestro listado de participantes y de preguntas para todos ellos
     * @throws Exception 
     */
    public void analizaResponsables() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fichero));
        String cadena = br.readLine();
        String[] campos = cadena.split("\t");
        int i = NUM_INICIAL;
        for (; i < campos.length; i++) {
            int pos = obtenerPosicionTipoAnalizado(campos[i]);
            //Solo tenemos que recoger nombres si el elmento contiene un >
            if (campos[i].contains(">")) {
                if (pos!=-1) {                   
                    if (!nombresAnalizados[pos].contains((campos[i].split(">"))[1]))  nombresAnalizados[pos].add((campos[i].split(">"))[1]);
                }
            }
            //Recogemos las preguntas y el tipo que tiene cada apartado (profesores, eq. directivo, secretaría y orientación).
            int posPregunta = campos[i].indexOf("-");
            //Creamos el bean para la comparación
            PreguntaBean pb = new PreguntaBean(campos[i].substring(posPregunta+1, posPregunta+3), null);
            if (!preguntas[pos].contains(pb)) {
                int posTipo = campos[i].indexOf("_");
                int posDependencia = campos[i].indexOf("*");
                //Si no hay dependencia
                if (posDependencia == -1) pb = new PreguntaBean(campos[i].substring(posPregunta+1, posPregunta+3), campos[i].substring(posTipo+2,posTipo+3));
                else pb = new PreguntaBean(campos[i].substring(posPregunta+1, posPregunta+3), campos[i].substring(posTipo+2,posTipo+3), campos[i].substring(posDependencia+4,posDependencia+6));
                preguntas[pos].add(pb);
            }
        }
        br.close();
    }
    
    /**
     * Este método permite obtener cuántas respuestas va a contestar cada usuario.
     * Generará un array de enteros con las respuestas por cada tipo. En la posición 0 irán los profesores
     * en la 1 equipo docente, en la dos secretaría y en la tres orientación
     * @param respuesta representa una respuesta de cualquier usuarios

     */
    private void obtenerNumeroRespuestasPorUsuario (String respuesta) {
        respuestaPorCliente = new int[TIPOS_USUARIOS_ANALIZADOS.length];
        for (int i = 0; i < respuestaPorCliente.length; i++) {
            int total = 0;
            ArrayList<PreguntaBean> preguntasTipo = preguntas[i];
            for (PreguntaBean pb : preguntasTipo) {
                //Las preguntas tipo Likert se responden por cada participante, así que si es tipo likert
                //no solo recibiremos una respuesta
                if (pb.getTipo().equals("L")) total+=nombresAnalizados[i].size();
                else total++;
            }
            respuestaPorCliente[i]=total;
        }
    }
    
    /**
     * Este método permtite devolver en un array de arrayList todas las respuestas de un cliente organizadas por tipo
     * En cada celda del array se devolverán las respuestas de cada tipo (P,D,S,O)
     * @param respuesta representa una fila del CSV
     * @return todas las respuestas de un cliente
     */
    private ArrayList<String>[] analizarRespuesta (String respuesta) {
        //Definimos el array de arraylist con las respuestas del usuario
        ArrayList<String>[] respuestaUsuario = new ArrayList[respuestaPorCliente.length];
        for (int i = 0; i < respuestaUsuario.length; i++) {
            respuestaUsuario[i] = new ArrayList<>();
        }
        String[] campos = respuesta.split("\t");
        int pos = NUM_INICIAL;
                
        for (int i=0;i<respuestaPorCliente.length;i++) {
           for (int j=0;j<respuestaPorCliente[i];j++) {
               if (pos<campos.length) respuestaUsuario[i].add(campos[pos++]);
           }
       }
       return respuestaUsuario;
    }
    
    /**
     * Este método permitirá obtener todas las respuestas del CSV e insertarlas en BBDD
     * @param dbu Representa el objeto de tipo DataBaseUtil para realizar la insercción.
     * @throws Exception si se produjera un error de insercción
     */
    public void analizarRespuestas (DataBaseUtil dbu) throws Exception {
        //Comenzaremos en análisis del CSV encuestando a los responsables genéricos
        //Sobre estos responsables genéricos asociaremos todas aquellas respuestas que no sean de tipo Likert
        dbu.encuestaResponsablesGenericos(ciclo, curso);
                
        BufferedReader br = new BufferedReader(new FileReader(fichero));
        //Leemos la primera en vacío porque está la cabecera
        br.readLine();
        String cadena;
        boolean primeraRespuesta = true;
        while ((cadena=br.readLine())!= null) {
            if (primeraRespuesta) {
                obtenerNumeroRespuestasPorUsuario(cadena);
                primeraRespuesta = false;
            }
            //Una vez conocidas las respuestas que debería tener cada usuario
            //Insertaremos todas las respuestas organizadas por tipos
            ArrayList<String>[] respuestaAlumno = analizarRespuesta(cadena);
            for (int i = 0; i < respuestaAlumno.length; i++) {
                ArrayList<String> respuestasPorTipo = respuestaAlumno[i];
                int posPreguntaBean = 0, posLikert = 0;
                for (int j=0; j< respuestasPorTipo.size(); j++) {
                    PreguntaBean pb = preguntas[i].get(posPreguntaBean);
                    //Si la pregunta es de tipo Likert será la misma respuesta por cada responsable encuestado
                    if (pb.getTipo().equals("L")) {
                        posLikert++;
                        //Si hemos alcanzado el total número de encuestados cambiaremos de pregunta
                        if (posLikert == nombresAnalizados[i].size()) {
                            posLikert=0;
                            posPreguntaBean++;
                        }
                    }
                    else {
                        posLikert = 0;
                        posPreguntaBean++;
                    }
                    //Comprobaremos si la pregunta tiene dependencias con otras
                    if (pb.getDependenciaNum()!=null) {
                        int posDependencia = Integer.parseInt(pb.getDependenciaNum())-1;
                        if (respuestasPorTipo.get(posDependencia).equals(DataExtraction.SI)) {
                            System.out.println("Pregunta con Dependencia válida");
                            String nombreResponsable = obtenerNombreResponsableParaInsertar(i, j, pb);
                            //Insertaremos en BBDD siempre y cuando haya respuesta
                            //Así evitaremos insertar respuestas tipo texto sin información
                            if (!respuestasPorTipo.get(j).trim().isEmpty()) dbu.insertaPregunta(pb.getPregunta(), pb.getTipo(), respuestasPorTipo.get(j), ciclo, curso, 
                            nombreResponsable, TIPOS_USUARIOS_ANALIZADOS[i]);
                            
                            System.out.println("Pregunta: "+pb.getPregunta());
                            System.out.println("Tipo: "+pb.getTipo());
                            System.out.println("Respuesta: "+respuestasPorTipo.get(j));
                            System.out.println("Ciclo: "+ciclo);
                            System.out.println("Curso: "+curso);
                            System.out.println("Responsable: "+nombreResponsable);
                            System.out.println("Tipo Responsable: "+TIPOS_USUARIOS_ANALIZADOS[i]);
                            System.out.println("----------------------------------------------------");
                        }
                        else System.out.println("Pregunta con dependencia no válida");
                    }
                    else {
                        String nombreResponsable = obtenerNombreResponsableParaInsertar(i, j, pb);
                        //Insertaremos en BBDD siempre y cuando haya respuesta
                        //Así evitaremos insertar respuestas tipo texto sin información
                        if (!respuestasPorTipo.get(j).trim().isEmpty()) dbu.insertaPregunta(pb.getPregunta(), pb.getTipo(), respuestasPorTipo.get(j), ciclo, curso, 
                            nombreResponsable, TIPOS_USUARIOS_ANALIZADOS[i]);
                        
                        System.out.println("Pregunta: "+pb.getPregunta());
                        System.out.println("Tipo: "+pb.getTipo());
                        System.out.println("Respuesta: "+respuestasPorTipo.get(j));
                        System.out.println("Ciclo: "+ciclo);
                        System.out.println("Curso: "+curso);
                        System.out.println("Responsable: "+nombreResponsable);
                        System.out.println("Tipo Responsable: "+TIPOS_USUARIOS_ANALIZADOS[i]);
                        System.out.println("----------------------------------------------------");
                    }
                }
            }
        }
        br.close();
    }
    
    /**
     * Este método permite obtener el nombre de la persona responsable de esa pregunta en la encuesta
     * @param posEnTipo representa el tipo que analizamos en valor entero (P,D,S,O)
     * @param posEnPregunta representa la posición de la pregunta
     * @param pb representa la PreguntaBean con todos los datos
     * @return el nombre del responsable analizado si es que hay (solo para preguntas tipo Likert)
     */
    private String obtenerNombreResponsableParaInsertar (int posEnTipo, int posEnPregunta, PreguntaBean pb) {
        //Si la pregunta es de tipo Likert
        if (pb.getTipo().equals("L")) {
            return nombresAnalizados[posEnTipo].get(posEnPregunta%nombresAnalizados[posEnTipo].size());
        }
        //Si es de tipo SI/NO o Texto Libre
        else return TIPOS_USUARIOS_ANALIZADOS[posEnTipo];
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

    public ArrayList<PreguntaBean>[] getPreguntas() {
        return preguntas;
    }
    
    public ArrayList<PreguntaBean> getPregunta(String campo) {
        int pos = obtenerPosicionTipoAnalizado(campo);
        if (pos!=-1) return preguntas[pos];
        return null;
    }

    public String getCiclo() {
        return ciclo;
    }

    public String getCurso() {
        return curso;
    }
    
}
