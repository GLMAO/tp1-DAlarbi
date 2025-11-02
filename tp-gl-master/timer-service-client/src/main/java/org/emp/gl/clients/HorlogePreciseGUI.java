package org.emp.gl.clients;

import org.emp.gl.timer.service.TimerService;
import org.emp.gl.timer.service.TimerChangeListener;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Horloge avec dixièmes de seconde
 */
public class HorlogePreciseGUI extends JFrame implements TimerChangeListener {
    
    private final TimerService timerService;
    private JLabel labelHeure;
    private JLabel labelDixieme;
    
    public HorlogePreciseGUI(TimerService timerService) {
        this.timerService = timerService;
        this.timerService.addTimeChangeListener(this);
        
        initComponents();
        updateDisplay();
    }
    
    private void initComponents() {
        setTitle("Horloge Précise");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 200);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(44, 62, 80));
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 50));
        
        // Label pour HH:MM:SS
        labelHeure = new JLabel("00:00:00");
        labelHeure.setFont(new Font("Courier New", Font.BOLD, 70));
        labelHeure.setForeground(new Color(46, 204, 113));
        
        // Label pour les dixièmes
        labelDixieme = new JLabel(".0");
        labelDixieme.setFont(new Font("Courier New", Font.BOLD, 50));
        labelDixieme.setForeground(new Color(52, 152, 219));
        
        mainPanel.add(labelHeure);
        mainPanel.add(labelDixieme);
        
        add(mainPanel);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(this::updateDisplay);
    }
    
    private void updateDisplay() {
        String heureText = String.format("%02d:%02d:%02d",
                timerService.getHeures(),
                timerService.getMinutes(),
                timerService.getSecondes());
        
        labelHeure.setText(heureText);
        labelDixieme.setText("." + timerService.getDixiemeDeSeconde());
    }
    
    public static void main(String[] args) {
        TimerService service = new org.emp.gl.timer.service.impl.DummyTimeServiceImpl();
        
        SwingUtilities.invokeLater(() -> {
            HorlogePreciseGUI horloge = new HorlogePreciseGUI(service);
            horloge.setVisible(true);
        });
    }
}