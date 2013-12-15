/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.bean.bbdd;

import com.teide.aortiz.encuestaefqm.bean.MediaBean;
import com.teide.aortiz.encuestaefqm.util.DataExtraction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public static final String INSERT_MEDIA = "insert into media (num,tipo,media,ciclo,curso,nombreResponsable,tipoResponsable) values (?,?,?,?,?,?,?)";
    public static final String INSERT_PORCENTAJE = "insert into media (num,tipo,media,respuesta,ciclo,curso,nombreResponsable,tipoResponsable) values (?,?,?,?,?,?,?,?)";
   
    
    public static final String CALCULATE_MEDIA_BY_QUESTION = "select num,tipo,avg(respuesta),ciclo,curso,nombreResponsable,tipoResponsable from pregunta group by num, nombreResponsable,tipoResponsable \n" +
                                                             "having ciclo=? and curso=? and tipo='L'";
    public static final String CALCULATE_PERCENTAGE = "select num,tipo,count(respuesta),ciclo,curso,nombreResponsable,tipoResponsable from pregunta group by num,nombreResponsable,respuesta\n" +
                                                          "having ciclo=? and curso=? and tipo='S' and respuesta=?";
    public static final String COUNT_ANSWER = "select count(respuesta),ciclo,curso,tipo from pregunta group by num,nombreResponsable\n" +
                                              "having ciclo=? and curso=? and tipo='S'";
    
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
    
    /**
     * Este método permite obtener y generar las medias de todas las preguntas tipo Likert por pregunta, profesor y tipo
     * @param ciclo representa el ciclo del que se generarán las medias
     * @param curso representa el curso del que se generarán las medias
     * @return un ArrayList de medias
     * @throws SQLException si se produjera una excepción durante la búsqueda
     */
    private ArrayList<MediaBean> obtenerMedias (String ciclo, String curso) throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.CALCULATE_MEDIA_BY_QUESTION);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ResultSet rs = ps.executeQuery();
        ArrayList<MediaBean> listado = new ArrayList<>();
        while (rs.next()) {
            MediaBean mb = new MediaBean();
            mb.setNum(rs.getString("num"));
            mb.setTipo(rs.getString("tipo"));
            mb.setMedia(rs.getDouble("avg(respuesta)"));
            mb.setCiclo(rs.getString("ciclo"));
            mb.setCurso(rs.getString("curso"));
            mb.setNombreResponsable(rs.getString("nombreResponsable"));
            mb.setTipoResponsable(rs.getString("tipoResponsable"));
            listado.add(mb);
        }
        return listado;
    }
    
     /**
     * Este método permite obtener y generar los porcentajes de todas las preguntas tipo SI/NO por pregunta, profesor y tipo
     * @param ciclo representa el ciclo del que se generarán las medias
     * @param curso representa el curso del que se generarán las medias
     * @return un ArrayList de medias
     * @throws SQLException si se produjera una excepción durante la búsqueda
     */
    private ArrayList<MediaBean> obtenerPorcentajes (String ciclo, String curso) throws SQLException {
        //Primero obtendremos el número de respuestas de la encuesta
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.COUNT_ANSWER);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int totalRespuestas = rs.getInt("count(respuesta)");
               
        //Ahora obtendremos el número de respuestas afirmativas
        ps = conection.prepareStatement(DataBaseUtil.CALCULATE_PERCENTAGE);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, DataExtraction.SI);
        rs = ps.executeQuery();
        ArrayList<MediaBean> listado = new ArrayList<>();
        while (rs.next()) {
            //Añadimos al listado el porcentaje de las respuestas afirmativas
            MediaBean mbSi = new MediaBean();
            mbSi.setNum(rs.getString("num"));
            mbSi.setTipo(rs.getString("tipo"));
            mbSi.setRespuesta(DataExtraction.SI);
            int respuestasSI = rs.getInt("count(respuesta)");
            mbSi.setMedia((respuestasSI*100.0)/totalRespuestas);
            mbSi.setCiclo(rs.getString("ciclo"));
            mbSi.setCurso(rs.getString("curso"));
            mbSi.setNombreResponsable(rs.getString("nombreResponsable"));
            mbSi.setTipoResponsable(rs.getString("tipoResponsable"));
            listado.add(mbSi);
        }
            
        //Ahora obtendremos el número de respuestas negativas
        ps = conection.prepareStatement(DataBaseUtil.CALCULATE_PERCENTAGE);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ps.setString(3, DataExtraction.NO);
        rs = ps.executeQuery();
        while (rs.next()) {
            MediaBean mbNo = new MediaBean();
            mbNo.setNum(rs.getString("num"));
            mbNo.setTipo(rs.getString("tipo"));
            mbNo.setRespuesta(DataExtraction.NO);
            int respuestasNo = rs.getInt("count(respuesta)");
            mbNo.setMedia((respuestasNo*100.0)/totalRespuestas);
            mbNo.setCiclo(rs.getString("ciclo"));
            mbNo.setCurso(rs.getString("curso"));
            mbNo.setNombreResponsable(rs.getString("nombreResponsable"));
            mbNo.setTipoResponsable(rs.getString("tipoResponsable"));
            listado.add(mbNo);
        }
        return listado;
    }
    
    /**
     * Este método permite insertar todas las medias de las preguntas Likert en la BBDD
     * @param ciclo representa el ciclo sobre el que se insertarán las medias
     * @param curso representa el curso sobre el que se insertarán las medias
     * @return el total de medias insertadas
     * @throws SQLException si se produjera un error de BBDD
     */
    public int insertarMedias (String ciclo, String curso) throws SQLException {
        ArrayList<MediaBean> listado = obtenerMedias(ciclo, curso);
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_MEDIA);
        int total = 0;
        for (MediaBean mb : listado) {
            ps.setString(1, mb.getNum());
            ps.setString(2, mb.getTipo());
            ps.setDouble(3, mb.getMedia());
            ps.setString(4, mb.getCiclo());
            ps.setString(5, mb.getCurso());
            ps.setString(6, mb.getNombreResponsable());
            ps.setString(7, mb.getTipoResponsable());
            total+=ps.executeUpdate();
        }
        return total;
    }
    
    /**
     * Este método insertará en BBDD todos los porcentajes de las respuestas afirmativas y negativas
     * @param ciclo representa el ciclo que se insertará
     * @param curso representa el curso que se insertará
     * @return el número de porcentajes insertados
     * @throws SQLException si se produjera un error de BBDD
     */
    public int insertaPorcentajes (String ciclo, String curso) throws SQLException {
        ArrayList<MediaBean> listado = obtenerPorcentajes(ciclo, curso);
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_PORCENTAJE);
        int total = 0;
        for (MediaBean mb : listado) {
            ps.setString(1, mb.getNum());
            ps.setString(2, mb.getTipo());
            ps.setDouble(3, mb.getMedia());
            ps.setString(4, mb.getRespuesta());
            ps.setString(5, mb.getCiclo());
            ps.setString(6, mb.getCurso());
            ps.setString(7, mb.getNombreResponsable());
            ps.setString(8, mb.getTipoResponsable());
            total+=ps.executeUpdate();
        }
        return total;
    }
    
    
}
