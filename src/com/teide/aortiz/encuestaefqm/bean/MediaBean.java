/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.teide.aortiz.encuestaefqm.bean;

/**
 *
 * @author antonio
 */
public class MediaBean {
    
    private String num, tipo, respuesta, ciclo,curso,nombreResponsable,tipoResponsable;
    private double media;

    public MediaBean() {
    }

    public MediaBean(String num, String tipo, String ciclo, String curso, String nombreResponsable, String tipoResponsable, double media) {
        this.num = num;
        this.tipo = tipo;
        this.ciclo = ciclo;
        this.curso = curso;
        this.nombreResponsable = nombreResponsable;
        this.tipoResponsable = tipoResponsable;
        this.media = media;
    }

    public MediaBean(String num, String tipo, String respuesta, String ciclo, String curso, String nombreResponsable, String tipoResponsable, double media) {
        this.num = num;
        this.tipo = tipo;
        this.respuesta = respuesta;
        this.ciclo = ciclo;
        this.curso = curso;
        this.nombreResponsable = nombreResponsable;
        this.tipoResponsable = tipoResponsable;
        this.media = media;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getTipoResponsable() {
        return tipoResponsable;
    }

    public void setTipoResponsable(String tipoResponsable) {
        this.tipoResponsable = tipoResponsable;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
