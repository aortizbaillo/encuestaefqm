/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.bean.bbdd;

import com.teide.aortiz.encuestaefqm.util.DataExtraction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author antonio
 */
public class DataBaseUtil {
    
    public static final String URL = "jdbc:mysql://127.0.0.1:3306/efqm";
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final String USER = "root";
    public static final String PASSWORD = "chuki";
    
    //Queries
    public static final String INSERT_CICLO = "insert into ciclo values (?,?)";
    public static final String INSERT_RESPONSABLE = "insert into responsable values (?,?)";
    public static final String INSERT_ENCUESTADOS = "insert into encuestado values (?,?,?,?)";
    public static final String INSERT_PREGUNTA = "insert into pregunta (num,tipo,respuesta,ciclo,curso,nombreResponsable,tipoResponsable) "
            + "values (?,?,?,?,?,?,?)";
    
    private Connection conection;
    
    public DataBaseUtil() throws ClassNotFoundException, SQLException{
        startConnection();
    }
   
    private void startConnection () throws ClassNotFoundException, SQLException {
        Class.forName(DataBaseUtil.DRIVER);
        conection = DriverManager.getConnection(DataBaseUtil.URL, DataBaseUtil.USER, DataBaseUtil.PASSWORD);
    }
      
    /**
     * Este método permitirá insertar un ciclo en la BBDD
     * @param ciclo representa el ciclo formativo analizado
     * @param curso representa el curso analizado
     * @return el número de filas insertadas en BBDD
     * @throws SQLException si se produjera un error de insercción
     */
    public int insertarCiclo (String ciclo, String curso) throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_CICLO);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        return ps.executeUpdate();
    }
    
    /**
     * Este método permite insertar los responsables genéricos para los casos en los que las 
     * respuestas no sean de tipo Likert. Se asociarán sobre estos responsables genéricos
     * @throws SQLException si se produjera un error de insercción
     */
    public void insertaResponsablesGenericos () throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_RESPONSABLE);
        ps.setString(1, "P");
        ps.setString(2, "P");
        ps.executeUpdate();
        ps.setString(1, "D");
        ps.setString(2, "D");
        ps.executeUpdate();
        ps.setString(1, "S");
        ps.setString(2, "S");
        ps.executeUpdate();
        ps.setString(1, "O");
        ps.setString(2, "O");
        ps.executeUpdate();
    }
    
    /**
     * Este método permitirá encuestar a los responsables genéricos para los casos en los que 
     * las respuestas no sean de tipo Likert.
     * @param ciclo representa el ciclo
     * @param curso representa el curso
     * @throws SQLException si se produjera un error de insercción
     */
    public void encuestaResponsablesGenericos (String ciclo, String curso) throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_ENCUESTADOS);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, "P");
        ps.setString(4, "P");
        ps.executeUpdate();
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, "D");
        ps.setString(4, "D");
        ps.executeUpdate();
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, "S");
        ps.setString(4, "S");
        ps.executeUpdate();
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, "O");
        ps.setString(4, "O");
        ps.executeUpdate();
    }
    
    /**
     * Este método permitirá insertar todos los responsables que se analizarán para la encuesta
     * @param nombresAnalizados es un Array de ArrayList con todos los responsables organizados por tipo
     * @return el número de responsables insertados en BBDD
     * @throws SQLException si se produjera un error de insercción
     */
    public int insertarResponsables (ArrayList<String>[] nombresAnalizados) throws SQLException {
        int total = 0;
        for (int i = 0; i < nombresAnalizados.length; i++) {
            ArrayList<String> listado = nombresAnalizados[i];
            for (String responsable : listado) {
                PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_RESPONSABLE);
                ps.setString(1, responsable);
                ps.setString(2, DataExtraction.TIPOS_USUARIOS_ANALIZADOS[i]);
                total+=ps.executeUpdate();
            }
        }
        return total;
    }
    
    /**
     * Este método permitirá insertar todos los encuestados que se analizarán para la encuesta
     * @param nombresAnalizados es un Array de ArrayList con todos los responsables organizados por tipo
     * @param ciclo representa el ciclo formativo analizado
     * @param curso representa el curso analizado
     * @return el número de filas insertadas en BBDD
     * @throws SQLException si se produjera un error de insercción
     */
    public int insertarEncuestados (ArrayList<String>[] nombresAnalizados, String ciclo, String curso) throws SQLException {
        int total = 0;
        for (int i = 0; i < nombresAnalizados.length; i++) {
            ArrayList<String> listado = nombresAnalizados[i];
            for (String responsable : listado) {
                PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_ENCUESTADOS);
                ps.setString(1, ciclo);
                ps.setString(2, curso);
                ps.setString(3, responsable);
                ps.setString(4, DataExtraction.TIPOS_USUARIOS_ANALIZADOS[i]);
                total+=ps.executeUpdate();
            }
        }
        return total;
    }
    
    /**
     * Este método permite insertar una pregunta (una respuesta que da un usuario a una respuesta)
     * @param num representa el número de la pregunta
     * @param tipo representa el tipo de pregunta (Likert, SI o NO, abierta)
     * @param respuesta representa la respuesta del usuario
     * @param ciclo representa el ciclo que realiza la encuesta
     * @param curso representa el curso en el que se realiza la encuesta
     * @param nombreResponsable representa el nombre del responsable
     * @param tipoResponsable representa el tipo de responsable (P, O, S, D)
     * @return 1 si se inserta la fila en BBDD o 0 si no se hace
     * @throws SQLException si se produjera un error de insercción.
     */
    public int insertaPregunta (String num, String tipo, String respuesta, String ciclo, String curso, String nombreResponsable, String tipoResponsable) throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_PREGUNTA);
        ps.setString(1, num);
        ps.setString(2, tipo);
        ps.setString(3, respuesta);
        ps.setString(4, ciclo);
        ps.setString(5, curso);
        ps.setString(6, nombreResponsable);
        ps.setString(7, tipoResponsable);
        return ps.executeUpdate();
    }
    
    
}
