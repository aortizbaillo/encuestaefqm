/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.bean.bbdd;

import com.teide.aortiz.encuestaefqm.bean.ComentariosBean;
import com.teide.aortiz.encuestaefqm.bean.ResponsableBean;
import com.teide.aortiz.encuestaefqm.bean.MediaBean;
import com.teide.aortiz.encuestaefqm.bean.MediaResponsableBean;
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
    public static final String PASSWORD = "patata";
    
    //Queries
    public static final String INSERT_CICLO = "insert into ciclo values (?,?)";
    public static final String INSERT_RESPONSABLE = "insert into responsable values (?,?)";
    public static final String INSERT_ENCUESTADOS = "insert into encuestado values (?,?,?,?)";
    public static final String INSERT_PREGUNTA = "insert into pregunta (num,tipo,respuesta,ciclo,curso,nombreResponsable,tipoResponsable) "
            + "values (?,?,?,?,?,?,?)";
    public static final String INSERT_MEDIA = "insert into media (num,tipo,media,ciclo,curso,nombreResponsable,tipoResponsable) values (?,?,?,?,?,?,?)";
    public static final String INSERT_PORCENTAJE = "insert into media (num,tipo,media,respuesta,ciclo,curso,nombreResponsable,tipoResponsable) values (?,?,?,?,?,?,?,?)";
   
    
    public static final String CALCULATE_MEDIA_BY_QUESTION = "select num,tipo,ciclo,curso,nombreResponsable,tipoResponsable from pregunta group by num, nombreResponsable,tipoResponsable,ciclo,curso \n" +
                                                           "having ciclo=? and curso=? and tipo='L'";
    public static final String CALCULATE_PERCENTAGE = "select num,tipo,count(respuesta),ciclo,curso,nombreResponsable,tipoResponsable from pregunta group by num,nombreResponsable,respuesta,ciclo,curso\n" +
                                                          "having ciclo=? and curso=? and tipo='S' and respuesta=?";
    public static final String CALCULATE_TOTAL_PUNTUACIONES = "select sum(respuesta) from pregunta where nombreResponsable=? and num=? and ciclo=?";
    public static final String CALCULATE_NUM_PUNTUACIONES_VALIDAS = "select count(respuesta) from pregunta where nombreResponsable= ? and num=? and "
                                                                    + "ciclo=? and respuesta != 0";
    public static final String COUNT_ANSWER = "select count(respuesta),ciclo,curso,tipo from pregunta group by num,nombreResponsable,ciclo,curso\n" +
                                              "having ciclo=? and curso=? and tipo='S'";
    public static final String OBTAIN_RESPONSABLE_CON_FILTRO = "select nombreResponsable,ciclo from encuestado where curso=? and tipoResponsable=? "
                                                + "and nombreResponsable <> ? order by nombreResponsable";
    public static final String OBTAIN_RESPONSABLE_SIN_FILTRO = "select nombreResponsable,ciclo from encuestado where curso=? and tipoResponsable=? "
                                                + "order by nombreResponsable";
    public static final String OBTAIN_MEDIA_RESPONSABLE = "select num,media,respuesta from media where nombreResponsable=? and ciclo=? "
            + "and curso=? and tipoResponsable=? and tipo=? order by num";
    public static final String OBTAIN_COMENTARIOS = "select respuesta,ciclo from pregunta where tipo='T' and nombreResponsable=? and curso=? order by ciclo";
    public static final String COUNT_NUMBER_QUESTION_BY_KIND = "select count(num) from pregunta where num=? and respuesta=? and tipoResponsable=? and ciclo=? and curso=?";
    
    public static final String[][] ORDER_PREGUNTAS_EQUIPO_DIRECTIVO = {{"1","1"},{"1","0"},{"2","1"},{"2","0"}};
    public static final String[][] ORDER_PREGUNTAS_ORIENTACION = {{"3","1"},{"3","0"}};
    
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
     */
    public int insertarResponsables (ArrayList<String>[] nombresAnalizados) {
        int total = 0;
        for (int i = 0; i < nombresAnalizados.length; i++) {
            ArrayList<String> listado = nombresAnalizados[i];
            for (String responsable : listado) {
                try {
                    PreparedStatement ps = conection.prepareStatement(DataBaseUtil.INSERT_RESPONSABLE);
                    ps.setString(1, responsable);
                    ps.setString(2, DataExtraction.TIPOS_USUARIOS_ANALIZADOS[i]);
                    total+=ps.executeUpdate();
                }
                catch (SQLException e) {}
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
        PreparedStatement psTotal = conection.prepareStatement(DataBaseUtil.CALCULATE_TOTAL_PUNTUACIONES);
        PreparedStatement psNum = conection.prepareStatement(DataBaseUtil.CALCULATE_NUM_PUNTUACIONES_VALIDAS);
        ps.setString(1, ciclo);
        ps.setString(2, curso);
        ResultSet rs = ps.executeQuery();
        ArrayList<MediaBean> listado = new ArrayList<>();
        while (rs.next()) {
            MediaBean mb = new MediaBean();
            //Obtenemos la suma de los puntos del profesor
            psTotal.setString(1, rs.getString("nombreResponsable"));
            psTotal.setString(2, rs.getString("num"));
            psTotal.setString(3, rs.getString("ciclo"));
            ResultSet rsTotal = psTotal.executeQuery();
            rsTotal.next();
            int total = rsTotal.getInt(1);
            
            //Obtenemos el total de alumnos que han contestado para eliminar a los NS/NC
            psNum.setString(1, rs.getString("nombreResponsable"));
            psNum.setString(2, rs.getString("num"));
            psNum.setString(3, rs.getString("ciclo"));
            ResultSet rsNum = psNum.executeQuery();
            rsNum.next();
            int num = rsNum.getInt(1);
            
            mb.setNum(rs.getString("num"));
            mb.setTipo(rs.getString("tipo"));
            if (num == 0) mb.setMedia(0);
            else mb.setMedia((double)total/num);
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
        if (rs.isLast()) System.out.println("Última fila");
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
    
    /**
     * Permite obtener el listado de responsables (Profesores, Directivos, Secretaría u Orientación).
     * @param curso representa el curso
     * @param tipo representa el tipo de responsable que se quiere obtener
     * @return el listado de responsables de ese tipo y para ese curso
     * @throws SQLException 
     */
    private ArrayList<ResponsableBean> obtenerResponsables (String curso, String tipo, boolean filtro) throws SQLException {
        ArrayList<ResponsableBean> listado = new ArrayList<>();
        PreparedStatement ps;
        if (filtro) ps = conection.prepareStatement(DataBaseUtil.OBTAIN_RESPONSABLE_CON_FILTRO);
        else ps = conection.prepareStatement(DataBaseUtil.OBTAIN_RESPONSABLE_SIN_FILTRO);
        ps.setString(1, curso);
        ps.setString(2, tipo);
        if (filtro) ps.setString(3, tipo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ResponsableBean eb = new ResponsableBean();
            eb.setNombreResponsable(rs.getString("nombreResponsable"));
            eb.setCiclo(rs.getString("ciclo"));
            listado.add(eb);
        }
        return listado;
    }
    
    public ArrayList<ResponsableBean> obtenerProfesores (String curso) throws SQLException {
        return obtenerResponsables(curso, "P", true);
    }
    
    public ArrayList<ResponsableBean> obtenerEquipoDirectivo (String curso) throws SQLException {
        return obtenerResponsables(curso, "D", false);
    }
    
    public ArrayList<ResponsableBean> obtenerSecretaria (String curso) throws SQLException {
        return obtenerResponsables(curso, "S", false);
    }
    
    public ArrayList<ResponsableBean> obtenerOrientacion (String curso) throws SQLException {
        return obtenerResponsables(curso, "O", false);
    }
    
    /**
     * Este método permite obtener la media de cualquier tipo de responsable (Profesor, E.Directivo, Secretaría u Orientación)
     * @param nombreResponsable representa el nombre del responsable
     * @param ciclo representa el ciclo
     * @param curso representa el curso
     * @param tipoResponsable representa el tipo de responsable
     * @return sus medias
     * @throws SQLException 
     */
    private ArrayList<MediaResponsableBean> obtenerMediasResponsables (String nombreResponsable, String ciclo, String curso, String tipoResponsable,
            String tipo) throws SQLException {
        ArrayList<MediaResponsableBean> listado = new ArrayList<>();
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.OBTAIN_MEDIA_RESPONSABLE);
        ps.setString(1, nombreResponsable);
        ps.setString(2, ciclo);
        ps.setString(3, curso);
        ps.setString(4, tipoResponsable);
        ps.setString(5, tipo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            MediaResponsableBean mrb = new MediaResponsableBean();
            mrb.setNum(rs.getInt("num"));
            mrb.setMedia(rs.getDouble("media"));
            mrb.setRespuesta(rs.getString("respuesta"));
            listado.add(mrb);
        }
        return listado;
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasProfesores (String nombreResponsable, String ciclo, String curso) throws SQLException {
        return obtenerMediasResponsables(nombreResponsable, ciclo, curso, "P","L");
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasEquipoDirectivo (String nombreResponsable, String ciclo, String curso) throws SQLException {
        return obtenerMediasResponsables(nombreResponsable, ciclo, curso, "D","L");
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasEquipoDirectivoGenerico (String nombreResponsable, String ciclo, String curso) throws SQLException {
        ArrayList<MediaResponsableBean> listado = obtenerMediasResponsables(nombreResponsable, ciclo, curso, "D","S");
        //Si están todos los valores los devolvemos
        if (listado.size() == 4) return listado;
        
        //En caso contrario
        for (int i = 0; i < 4; i++) {
            //Generamos el posible valor a añadir
            MediaResponsableBean mrb = new MediaResponsableBean(Integer.parseInt(ORDER_PREGUNTAS_EQUIPO_DIRECTIVO[i][0]), ORDER_PREGUNTAS_EQUIPO_DIRECTIVO[i][1], 0);
            //Si estamos al final la añadimos
            if (i==listado.size()) listado.add(mrb);
            else {
                if (!(listado.get(i).getNum() == Integer.parseInt(ORDER_PREGUNTAS_EQUIPO_DIRECTIVO[i][0]) &&
                        listado.get(i).getRespuesta().equals(ORDER_PREGUNTAS_EQUIPO_DIRECTIVO[i][1]))) {
                    listado.add(i, mrb);
                }
            }
        }
        return listado;
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasSecretaria (String nombreResponsable, String ciclo, String curso) throws SQLException {
        return obtenerMediasResponsables(nombreResponsable, ciclo, curso, "S","L");
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasSecretariaGenerico (String nombreResponsable, String ciclo, String curso) throws SQLException {
        return obtenerMediasResponsables(nombreResponsable, ciclo, curso, "S","S");
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasOrientacion (String nombreResponsable, String ciclo, String curso) throws SQLException {
        return obtenerMediasResponsables(nombreResponsable, ciclo, curso, "O","L");
    }
    
    public ArrayList<MediaResponsableBean> obtenerMediasOrientacionGenerico (String nombreResponsable, String ciclo, String curso) throws SQLException {
        ArrayList<MediaResponsableBean> listado = obtenerMediasResponsables(nombreResponsable, ciclo, curso, "O","S");
        //Si están todos los valores los devolvemos
        if (listado.size() == 2) return listado;
        
        //En caso contrario
        for (int i = 0; i < 2; i++) {
            //Generamos el posible valor a añadir
            MediaResponsableBean mrb = new MediaResponsableBean(Integer.parseInt(ORDER_PREGUNTAS_ORIENTACION[i][0]), ORDER_PREGUNTAS_ORIENTACION[i][1], 0);
            //Si estamos al final la añadimos
            if (i==listado.size()) listado.add(mrb);
            else {
                if (!(listado.get(i).getNum() == Integer.parseInt(ORDER_PREGUNTAS_ORIENTACION[i][0]) &&
                        listado.get(i).getRespuesta().equals(ORDER_PREGUNTAS_ORIENTACION[i][1]))) {
                    listado.add(i, mrb);
                }
            }
        }
        return listado;
    }
    
    /**
     * Permite obtener el listado de comentarios de texto libre de todos (Profesores, Directivos, Secretaría u Orientación).
     * @param nombreResponsable representa el nombre de responsable (tipo) de comentarios que se quiere obtener
     * @param curso representa el curso
     * @return el listado de comentarios de ese tipo y para ese curso
     * @throws SQLException 
     */
    private ArrayList<ComentariosBean> obtenerComentarios (String nombreResponsable, String curso) throws SQLException {
        ArrayList<ComentariosBean> listado = new ArrayList<>();
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.OBTAIN_COMENTARIOS);
        ps.setString(1, nombreResponsable);
        ps.setString(2, curso);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            ComentariosBean cb = new ComentariosBean();
            cb.setComentario(rs.getString("respuesta"));
            cb.setCiclo(rs.getString("ciclo"));
            listado.add(cb);
        }
        return listado;
    }
    
    public ArrayList<ComentariosBean> obtenerComentariosProfesores (String curso) throws SQLException {
        return obtenerComentarios("P", curso);
    }
    
    public ArrayList<ComentariosBean> obtenerComentariosEquipoDirectivo (String curso) throws SQLException {
        return obtenerComentarios("D", curso);
    }
    
    public ArrayList<ComentariosBean> obtenerComentariosSecretaria (String curso) throws SQLException {
        return obtenerComentarios("S", curso);
    }
    
    public ArrayList<ComentariosBean> obtenerComentariosOrientacion (String curso) throws SQLException {
        return obtenerComentarios("O", curso);
    }
    
    /**
     * Este método permite contar las respuestas que aportan los usuarios de un ciclo y curso para conocer los que han necesitado antención
     * especial ante equipoDirectivo, secretaría u orientación
     * @param num representa la pregunta llave que permitirá a los clientes indicar si han necesitado atención especial o no
     * @param respuesta representa la respuesta (SI o NO) que se buscará en cada momento
     * @param tipoResponsable representa el tipo de Responsable (D,S,O)
     * @param ciclo representa el ciclo
     * @param curso representa el curso
     * @return el número de respuesta de ese tipo
     * @throws SQLException 
     */
    private int obtenerTotalPreguntasConNecesidades (String num, String respuesta, String tipoResponsable, String ciclo, String curso) throws SQLException {
        PreparedStatement ps = conection.prepareStatement(DataBaseUtil.COUNT_NUMBER_QUESTION_BY_KIND);
        ps.setString(1, num);
        ps.setString(2, respuesta);
        ps.setString(3, tipoResponsable);
        ps.setString(4, ciclo);
        ps.setString(5, curso);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }
    
    /**
     * Este método nos devolverá el número de encuestas que han necesitado la atención especial del equipo directivo con respecto al total
     * @param ciclo represeta el ciclo
     * @param curso representa el curso
     * @return el número de respuestas con atención especial con respecto al total
     * @throws SQLException 
     */
    public String obtenerNecesidadesDirectivo (String ciclo, String curso) throws SQLException {
        int totalSi = obtenerTotalPreguntasConNecesidades("02", "1" , "D", ciclo, curso);
        int totalNo = obtenerTotalPreguntasConNecesidades("02", "0" , "D", ciclo, curso);
        return " ("+totalSi+"/"+(totalSi+totalNo)+")";
    }
    
     /**
     * Este método nos devolverá el número de encuestas que han necesitado la atención especial de secretaría con respecto al total
     * @param ciclo represeta el ciclo
     * @param curso representa el curso
     * @return el número de respuestas con atención especial con respecto al total
     * @throws SQLException 
     */
    public String obtenerNecesidadesSecretaria (String ciclo, String curso) throws SQLException {
        int totalSi = obtenerTotalPreguntasConNecesidades("01", "1" , "S", ciclo, curso);
        int totalNo = obtenerTotalPreguntasConNecesidades("01", "0" , "S", ciclo, curso);
        return " ("+totalSi+"/"+(totalSi+totalNo)+")";
    }
    
     /**
     * Este método nos devolverá el número de encuestas que han necesitado la atención especial del equipo de orientación con respecto al total
     * @param ciclo represeta el ciclo
     * @param curso representa el curso
     * @return el número de respuestas con atención especial con respecto al total
     * @throws SQLException 
     */
    public String obtenerNecesidadesOrientacion (String ciclo, String curso) throws SQLException {
        int totalSi = obtenerTotalPreguntasConNecesidades("03", "1" , "O", ciclo, curso);
        int totalNo = obtenerTotalPreguntasConNecesidades("03", "0" , "O", ciclo, curso);
        return " ("+totalSi+"/"+(totalSi+totalNo)+")";
    }
}
