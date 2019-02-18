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
public class ResponsableBean {
    
    private String nombreResponsable, ciclo;

    public ResponsableBean() {
    }

    public ResponsableBean(String nombreResponsable, String ciclo) {
        this.nombreResponsable = nombreResponsable;
        this.ciclo = ciclo;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }
    
    
    
}
