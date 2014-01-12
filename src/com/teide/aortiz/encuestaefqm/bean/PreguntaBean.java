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
public class PreguntaBean {
    
    private String pregunta, tipo, dependenciaNum;

    public PreguntaBean(String pregunta, String tipo) {
        this.pregunta = pregunta;
        this.tipo = tipo;
    }

    public PreguntaBean(String pregunta, String tipo, String dependenciaNum) {
        this.pregunta = pregunta;
        this.tipo = tipo;
        this.dependenciaNum = dependenciaNum;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDependenciaNum() {
        return dependenciaNum;
    }

    public void setDependenciaNum(String dependenciaNum) {
        this.dependenciaNum = dependenciaNum;
    }

    @Override
    public boolean equals(Object obj) {
        PreguntaBean otra = (PreguntaBean) obj;
        return pregunta.equals(otra.getPregunta());
    }

    @Override
    public String toString() {
        if (dependenciaNum == null) return pregunta+" "+tipo;
        else return pregunta+" "+tipo+" - Dependencia con "+dependenciaNum;
    }
    
    
}
