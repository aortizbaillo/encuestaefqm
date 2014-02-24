/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.util;

import com.teide.aortiz.encuestaefqm.bean.ComentariosBean;
import com.teide.aortiz.encuestaefqm.bean.MediaResponsableBean;
import com.teide.aortiz.encuestaefqm.bean.ResponsableBean;
import com.teide.aortiz.encuestaefqm.bean.bbdd.DataBaseUtil;
import java.io.File;
import java.util.ArrayList;
import jxl.Cell;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;
/**
 *
 * @author antonio
 */
public class ExcelUtil {
   
    public static final String HOJA_PROFESORES = "profesores.xls";
    public static final String HOJA_COMENTARIOS_PROFESORES = "comentariosProfesores.xls";
    public static final String HOJA_EQUIPO_DIRECTIVO = "equipoDirectivo.xls";
    public static final String HOJA_COMENTARIOS_DIRECTIVO = "comentariosEquipoDirectivo.xls";
    public static final String HOJA_SECRETARIA = "secretaria.xls";
    public static final String HOJA_COMENTARIOS_SECRETARIA = "comentariosSecretaria.xls";
    public static final String HOJA_ORIENTACION = "orientacion.xls";
    public static final String HOJA_COMENTARIOS_ORIENTACION = "comentariosOrientacion.xls";
    
    public static final int FILA_DATOS = 3;
    public static final double CALIFICACION_ROJA = 2.5;
    
    private DataBaseUtil dbu;
    private String directorio;
    
    public ExcelUtil (String directorio) throws Exception {
        dbu = new DataBaseUtil();
        this.directorio = directorio;
    }
    
    /**
     * Este método permite crear el Excel de Profesores de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaProfesores (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_PROFESORES), opciones);
        WritableCellFormat formatNormal = new WritableCellFormat (NumberFormats.FLOAT); 
        WritableCellFormat formatRojo = new WritableCellFormat (NumberFormats.FLOAT); 
        formatRojo.setBackground(Colour.RED);
        
        WritableSheet sheet = null;
        //Obtendremos los profesores de un curso dado
        ArrayList<ResponsableBean> listado = dbu.obtenerProfesores(curso);
        String nombreAnterior = "";
        int columna = 1;

        for (int i=0;i<listado.size();i++) {
            ResponsableBean rb = listado.get(i);
            int fila = FILA_DATOS;

            String nombre = rb.getNombreResponsable();
            //Si cambiamos de nombre de responsable crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Escribimos el nombre del profesor en la fila 0,0
                Label label = new Label(0, 0, nombre);
                sheet.addCell(label);
                Label label2 = new Label(1, 0, curso);
                sheet.addCell(label2);

                //Como cambiamos de profesor la columna vuelve a valer 1 y la fila la primera
                columna = 1;
                fila = FILA_DATOS;
            }
            //Añadimos el curso que vamos a analizar
            Label label = new Label(columna, fila, rb.getCiclo());
            sheet.addCell(label);
            
            //Obtenemos las medias de ese profesor y las escribimos
            ArrayList<MediaResponsableBean> mediasProfesor = dbu.obtenerMediasProfesores(nombre, rb.getCiclo(), curso);
            double mediaPorCiclo = 0;
            for (MediaResponsableBean mrb : mediasProfesor) {
                fila++;
                Label num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("P", mrb.getNum()));
                sheet.addCell(num);
                Number media;
                if (mrb.getMedia()<=CALIFICACION_ROJA)  media = new Number(columna, fila, mrb.getMedia(), formatRojo);
                else media = new Number(columna, fila, mrb.getMedia(), formatNormal);
                sheet.addCell(media);
                mediaPorCiclo+=mrb.getMedia();
            }
            
            //Añadimos la media por ciclo
            fila++;
            mediaPorCiclo/=mediasProfesor.size();
            Number media;
            if (mediaPorCiclo<=CALIFICACION_ROJA) media = new Number(columna, fila, mediaPorCiclo, formatRojo);
            else media = new Number(columna, fila, mediaPorCiclo, formatNormal);
            sheet.addCell(media);
            columna++;
            
            //Antes de guardar el Excel dejamos hueco para los comentarios
            Label comentarios = new Label(0, fila+3, "Comentarios");
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        workbook.write();
        workbook.close();
        
        //Por último escribimos las medias por pregunta
        escribirMediasPorPregunta(HOJA_PROFESORES);
    }
    
    /**
     * Permite escribir las medias por pregunta sobre cualquier fichero Excel
     * @param fichero representa el fichero que se analizará
     * @throws Exception 
     */
    private void escribirMediasPorPregunta (String fichero) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        
        Workbook workbook = Workbook.getWorkbook(new File(directorio, fichero), opciones);
        WritableWorkbook workbookCopy = Workbook.createWorkbook(new File(directorio, fichero), workbook);
        WritableCellFormat formatNormal = new WritableCellFormat (NumberFormats.FLOAT); 
        WritableCellFormat formatRojo = new WritableCellFormat (NumberFormats.FLOAT); 
        formatRojo.setBackground(Colour.RED);
        
        //Recorremos todas las hojas para añadir las medias por pregunta
        for (int i=0; i< workbook.getNumberOfSheets(); i++) {
            WritableSheet sheet = workbookCopy.getSheet(i);
            int fila = FILA_DATOS+1;
            int columna = 1;
            Cell celda;

            //Mientras que haya filas
            while (!(celda = sheet.getCell(columna, fila)).getContents().isEmpty()) {
                //Recorremos mientras que haya columnas
                double valor = 0;
                int total = 0;
                while ( !(celda = sheet.getCell(columna, fila)).getContents().isEmpty()) {
                        valor+= Double.parseDouble((celda.getContents().split(" ")[0]));
                        total++;
                        columna++;
                }
                double mediaPregunta = valor/total;
                Number media;
                if (mediaPregunta<=CALIFICACION_ROJA)  media = new Number(columna, fila, mediaPregunta, formatRojo);
                else media = new Number(columna, fila, mediaPregunta, formatNormal);
                sheet.addCell(media);
                fila++;
                columna=1;
            }
        }
        //Guardamos el Excel
        workbookCopy.write();
        workbookCopy.close();
        workbook.close();
    }
    
    
     /**
     * Este método permite crear el Excel de Comentarios de Profesores de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaComentariosProfesores (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_COMENTARIOS_PROFESORES), opciones);
        
        WritableSheet sheet = null;
        //Obtendremos los comentarios de profesores de todos los cursos 
        ArrayList<ComentariosBean> listado = dbu.obtenerComentariosProfesores(curso);
        String nombreAnterior = "";
        int fila = FILA_DATOS;
        for (int i=0;i<listado.size();i++) {
            ComentariosBean cb = listado.get(i);

            String nombre = cb.getCiclo();
            //Si cambiamos de nombre de ciclo crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Como cambiamos de ciclo la fila vuelve a ser la primera
                fila = FILA_DATOS;
            }
            
            //Añadimos el comentario
            Label comentarios = new Label(0, fila++, cb.getComentario());
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        if (!listado.isEmpty()) workbook.write();
        workbook.close();
    }
    
    /**
     * Este método permite crear el Excel de Equipo Directivo de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaEquipoDirectivo (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_EQUIPO_DIRECTIVO), opciones);
        WritableCellFormat formatNormal = new WritableCellFormat (NumberFormats.FLOAT); 
        WritableCellFormat formatRojo = new WritableCellFormat (NumberFormats.FLOAT); 
        formatRojo.setBackground(Colour.RED);
        
        WritableSheet sheet = null;
        //Obtendremos los responsables del Equipo Directivo de un curso dado
        ArrayList<ResponsableBean> listado = dbu.obtenerEquipoDirectivo(curso);
        String nombreAnterior = "";
        int columna = 1;

        for (int i=0;i<listado.size();i++) {
            ResponsableBean rb = listado.get(i);
            int fila = FILA_DATOS;

            String nombre = rb.getNombreResponsable();
            //Si cambiamos de nombre de responsable crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Escribimos el nombre del responsable del equipo directivo en la fila 0,0
                Label label = new Label(0, 0, nombre);
                sheet.addCell(label);
                Label label2 = new Label(1, 0, curso);
                sheet.addCell(label2);

                //Como cambiamos de profesor la columna vuelve a valer 1 y la fila la primera
                columna = 1;
                fila = FILA_DATOS;
            }
            //Añadimos el curso que vamos a analizar
            Label label = new Label(columna, fila, rb.getCiclo());
            sheet.addCell(label);
            
            //Obtenemos las medias de ese responsable del equipo directivo y las escribimos
            ArrayList<MediaResponsableBean> mediasDirectivo;
            //Si el nombre tiene longitud mayor a uno es que se trata de una persona
            //de lo contrario será el Equipo Directivo Genérico
            if (nombre.length() > 1 ) mediasDirectivo = dbu.obtenerMediasEquipoDirectivo(nombre, rb.getCiclo(), curso);
            else mediasDirectivo = dbu.obtenerMediasEquipoDirectivoGenerico(nombre, rb.getCiclo(), curso);
                        
            double mediaPorCiclo = 0;
            for (MediaResponsableBean mrb : mediasDirectivo) {
                fila++;
                Label num;
                
                //Añadimos si la respuesta es SI o NO
                if (mrb.getRespuesta()== null) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("D", mrb.getNum()));
                else {
                    if (mrb.getRespuesta().equals("1")) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("D", mrb.getNum())+" - SI");
                    else num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("D", mrb.getNum())+" - NO");
                }
                sheet.addCell(num);
                Label media;
                if (mrb.getMedia()<=CALIFICACION_ROJA)  {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesDirectivo(rb.getCiclo(), curso), formatRojo);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatRojo);
                }
                else {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesDirectivo(rb.getCiclo(), curso), formatNormal);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatNormal);
                }
                sheet.addCell(media);
                mediaPorCiclo+=mrb.getMedia();
            }
            
            //Añadimos la media por ciclo
            fila++;
            if (mediaPorCiclo!=0) mediaPorCiclo/=mediasDirectivo.size();
            Number media;
            if (mediaPorCiclo<=CALIFICACION_ROJA) media = new Number(columna, fila, mediaPorCiclo, formatRojo);
            else media = new Number(columna, fila, mediaPorCiclo, formatNormal);
            if (nombre.length()>1) sheet.addCell(media);
            columna++;
            
            //Antes de guardar el Excel dejamos hueco para los comentarios
            Label comentarios = new Label(0, fila+3, "Comentarios");
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        workbook.write();
        workbook.close();
        
        //Por último escribimos las medias por pregunta
        //escribirMediasPorPregunta(HOJA_EQUIPO_DIRECTIVO);
    }
    
    /**
     * Este método permite crear el Excel de Comentarios de Equipo Directivo de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaComentariosEquipoDirectivo (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_COMENTARIOS_DIRECTIVO), opciones);
        
        WritableSheet sheet = null;
        //Obtendremos los comentarios de equipo directivo de todos los cursos 
        ArrayList<ComentariosBean> listado = dbu.obtenerComentariosEquipoDirectivo(curso);
        String nombreAnterior = "";
        int fila = FILA_DATOS;
        for (int i=0;i<listado.size();i++) {
            ComentariosBean cb = listado.get(i);

            String nombre = cb.getCiclo();
            //Si cambiamos de nombre de ciclo crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Como cambiamos de ciclo la fila vuelve a ser la primera
                fila = FILA_DATOS;
            }
            
            //Añadimos el comentario
            Label comentarios = new Label(0, fila++, cb.getComentario());
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        if (!listado.isEmpty()) workbook.write();
        workbook.close();
    }
    
    /**
     * Este método permite crear el Excel de Secretaria de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaSecretaria (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_SECRETARIA), opciones);
        WritableCellFormat formatNormal = new WritableCellFormat (NumberFormats.FLOAT); 
        WritableCellFormat formatRojo = new WritableCellFormat (NumberFormats.FLOAT); 
        formatRojo.setBackground(Colour.RED);
        
        WritableSheet sheet = null;
        //Obtendremos los responsables de Secretaria de un curso dado
        ArrayList<ResponsableBean> listado = dbu.obtenerSecretaria(curso);
        String nombreAnterior = "";
        int columna = 1;

        for (int i=0;i<listado.size();i++) {
            ResponsableBean rb = listado.get(i);
            int fila = FILA_DATOS;

            String nombre = rb.getNombreResponsable();
            //Si cambiamos de nombre de responsable crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Escribimos el nombre de secretaria en la fila 0,0
                Label label = new Label(0, 0, nombre);
                sheet.addCell(label);
                Label label2 = new Label(1, 0, curso);
                sheet.addCell(label2);

                //Como cambiamos de secretaria la columna vuelve a valer 1 y la fila la primera
                columna = 1;
                fila = FILA_DATOS;
            }
            //Añadimos el curso que vamos a analizar
            Label label = new Label(columna, fila, rb.getCiclo());
            sheet.addCell(label);
            
            //Obtenemos las medias de ese responsable de secretaria y las escribimos
            ArrayList<MediaResponsableBean> mediasSecretaria;
            //Si el nombre tiene longitud mayor a uno es que se trata de una persona
            //de lo contrario será Secretaria Genérico
            if (nombre.length() > 1 ) mediasSecretaria = dbu.obtenerMediasSecretaria(nombre, rb.getCiclo(), curso);
            else mediasSecretaria = dbu.obtenerMediasSecretariaGenerico(nombre, rb.getCiclo(), curso);
            
            double mediaPorCiclo = 0;
            for (MediaResponsableBean mrb : mediasSecretaria) {
                fila++;
                Label num;
                
                //Añadimos si la respuesta es SI o NO
                if (mrb.getRespuesta()== null) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("S", mrb.getNum()));
                else {
                    if (mrb.getRespuesta().equals("1")) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("S", mrb.getNum())+" - SI");
                    else num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("S", mrb.getNum())+" - NO");
                }
                sheet.addCell(num);
                Label media;
                if (mrb.getMedia()<=CALIFICACION_ROJA) {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesSecretaria(rb.getCiclo(), curso), formatRojo);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatRojo);
                }
                else {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesSecretaria(rb.getCiclo(), curso), formatNormal);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatNormal);
                }
                sheet.addCell(media);
                mediaPorCiclo+=mrb.getMedia();
            }
            
            //Añadimos la media por ciclo
            fila++;
            mediaPorCiclo/=mediasSecretaria.size();
            Number media;
            if (mediaPorCiclo<=CALIFICACION_ROJA) media = new Number(columna, fila, mediaPorCiclo, formatRojo);
            else media = new Number(columna, fila, mediaPorCiclo, formatNormal);
            if (nombre.length()>1) sheet.addCell(media);
            columna++;
            
            //Antes de guardar el Excel dejamos hueco para los comentarios
            Label comentarios = new Label(0, fila+3, "Comentarios");
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        workbook.write();
        workbook.close();
        
        //Por último escribimos las medias por pregunta
        escribirMediasPorPregunta(HOJA_SECRETARIA);
    }
    
    /**
     * Este método permite crear el Excel de Comentarios de Secretaria de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaComentariosSecretaria (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_COMENTARIOS_SECRETARIA), opciones);
        
        WritableSheet sheet = null;
        //Obtendremos los comentarios de equipo directivo de todos los cursos 
        ArrayList<ComentariosBean> listado = dbu.obtenerComentariosSecretaria(curso);
        String nombreAnterior = "";
        int fila = FILA_DATOS;
        for (int i=0;i<listado.size();i++) {
            ComentariosBean cb = listado.get(i);

            String nombre = cb.getCiclo();
            //Si cambiamos de nombre de ciclo crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Como cambiamos de ciclo la fila vuelve a ser la primera
                fila = FILA_DATOS;
            }
            
            //Añadimos el comentario
            Label comentarios = new Label(0, fila++, cb.getComentario());
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        if (!listado.isEmpty()) workbook.write();
        workbook.close();
    }
    
    /**
     * Este método permite crear el Excel de Orientacion de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaOrientacion (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_ORIENTACION), opciones);
        WritableCellFormat formatNormal = new WritableCellFormat (NumberFormats.FLOAT); 
        WritableCellFormat formatRojo = new WritableCellFormat (NumberFormats.FLOAT); 
        formatRojo.setBackground(Colour.RED);
        
        WritableSheet sheet = null;
        //Obtendremos los responsables de Orientacion de un curso dado
        ArrayList<ResponsableBean> listado = dbu.obtenerOrientacion(curso);
        String nombreAnterior = "";
        int columna = 1;

        for (int i=0;i<listado.size();i++) {
            ResponsableBean rb = listado.get(i);
            int fila = FILA_DATOS;

            String nombre = rb.getNombreResponsable();
            //Si cambiamos de nombre de responsable crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Escribimos el nombre de secretaria en la fila 0,0
                Label label = new Label(0, 0, nombre);
                sheet.addCell(label);
                Label label2 = new Label(1, 0, curso);
                sheet.addCell(label2);

                //Como cambiamos de orientacion la columna vuelve a valer 1 y la fila la primera
                columna = 1;
                fila = FILA_DATOS;
            }
            //Añadimos el curso que vamos a analizar
            Label label = new Label(columna, fila, rb.getCiclo());
            sheet.addCell(label);
            
            //Obtenemos las medias de ese responsable de orientacion y las escribimos
            ArrayList<MediaResponsableBean> mediasOrientacion;
            //Si el nombre tiene longitud mayor a uno es que se trata de una persona
            //de lo contrario será Orientacion Genérico
            if (nombre.length() > 1 ) mediasOrientacion = dbu.obtenerMediasOrientacion(nombre, rb.getCiclo(), curso);
            else mediasOrientacion = dbu.obtenerMediasOrientacionGenerico(nombre, rb.getCiclo(), curso);
            
            double mediaPorCiclo = 0;
            for (MediaResponsableBean mrb : mediasOrientacion) {
                fila++;
                Label num;
                
                //Añadimos si la respuesta es SI o NO
                if (mrb.getRespuesta()== null) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("O", mrb.getNum()));
                else {
                    if (mrb.getRespuesta().equals("1")) num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("O", mrb.getNum())+" - SI");
                    else num = new Label(0, fila, PreguntasUtil.obtenerTextoPregunta("O", mrb.getNum())+" - NO");
                }
                sheet.addCell(num);
                Label media;
                if (mrb.getMedia()<=CALIFICACION_ROJA) {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null && mrb.getNum()>3) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesOrientacion(rb.getCiclo(), curso), formatRojo);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatRojo);
                }
                else {
                    //Solamente mostraremos el número de respuestas con necesidades en caso de un responsable no genérico
                    if (mrb.getRespuesta()==null && mrb.getNum()>3) media = new Label(columna, fila, mrb.getMedia()+dbu.obtenerNecesidadesOrientacion(rb.getCiclo(), curso), formatNormal);
                    else media = new Label(columna, fila, String.valueOf(mrb.getMedia()), formatNormal);
                }
                sheet.addCell(media);
                mediaPorCiclo+=mrb.getMedia();
            }
            
            //Añadimos la media por ciclo
            fila++;
            mediaPorCiclo/=mediasOrientacion.size();
            Number media;
            if (mediaPorCiclo<=CALIFICACION_ROJA) media = new Number(columna, fila, mediaPorCiclo, formatRojo);
            else media = new Number(columna, fila, mediaPorCiclo, formatNormal);
            if (nombre.length()>1) sheet.addCell(media);
            columna++;
            
            //Antes de guardar el Excel dejamos hueco para los comentarios
            Label comentarios = new Label(0, fila+3, "Comentarios");
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        workbook.write();
        workbook.close();
        
        //Por último escribimos las medias por pregunta
        escribirMediasPorPregunta(HOJA_ORIENTACION);
    }
    
    /**
     * Este método permite crear el Excel de Comentarios de Orientacion de un curso dado
     * @param curso representa el curso que se analizará
     * @throws Exception 
     */
    public void hojaComentariosOrientacion (String curso) throws Exception {
        //Creamos el fichero de profesores
        WorkbookSettings opciones= new WorkbookSettings();
        opciones.setEncoding("iso-8859-1");
        WritableWorkbook workbook = Workbook.createWorkbook(new File(directorio, HOJA_COMENTARIOS_ORIENTACION), opciones);
        
        WritableSheet sheet = null;
        //Obtendremos los comentarios de orientacion de todos los cursos 
        ArrayList<ComentariosBean> listado = dbu.obtenerComentariosOrientacion(curso);
        String nombreAnterior = "";
        int fila = FILA_DATOS;
        for (int i=0;i<listado.size();i++) {
            ComentariosBean cb = listado.get(i);

            String nombre = cb.getCiclo();
            //Si cambiamos de nombre de ciclo crearemos una nueva hoja de Excel
            if (!nombre.equalsIgnoreCase(nombreAnterior)) {
                //Cambiaremos de hoja una vez añadida las medias por pregunta
                sheet = workbook.createSheet(nombre, i);
                 
                //Asignaremos el nuevo nombre para saber si tenemos que cambiar de hoja o no la próxima vez
                nombreAnterior = nombre;

                //Como cambiamos de ciclo la fila vuelve a ser la primera
                fila = FILA_DATOS;
            }
            
            //Añadimos el comentario
            Label comentarios = new Label(0, fila++, cb.getComentario());
            sheet.addCell(comentarios);
        }
             
        //Guardamos el Excel
        if (!listado.isEmpty()) workbook.write();
        workbook.close();
    }
}
