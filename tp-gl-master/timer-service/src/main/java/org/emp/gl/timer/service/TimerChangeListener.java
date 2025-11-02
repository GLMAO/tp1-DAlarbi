/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.emp.gl.timer.service;

import java.beans.PropertyChangeListener;

/**
 *
 * @author tina
 */
public interface TimerChangeListener extends PropertyChangeListener {
    
    final static String DIXEME_DE_SECONDE_PROP = "dixième";
    final static String SECONDE_PROP = "seconde";
    final static String MINUTE_PROP = "minute";
    final static String HEURE_PROP = "heure";
    
    // On hérite automatiquement de PropertyChangeListener :
    // void propertyChange(PropertyChangeEvent evt);
    
    // SUPPRIMÉ : void propertyChange(String prop, Object oldValue, Object newValue);
    // Car PropertyChangeSupport utilise PropertyChangeEvent
}