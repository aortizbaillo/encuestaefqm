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
public class MediaResponsableBean {
    
    private int num;
    private double media;
    private String respuesta;
    
    public MediaResponsableBean() {
    }

    public MediaResponsableBean(int num, String respuesta, double media) {
        this.num = num;
        this.respuesta = respuesta;
        this.media = media;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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
