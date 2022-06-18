package gui;

import bot.botimplementations.BotFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.math.Ray;
import gui.shotinput.MouseInputReader;
import gui.shotinput.ShotInputWindow;
import utility.math.Vector2;

import java.util.ArrayList;

public class SettingsGUI extends javax.swing.JFrame {
    /*
    private final Renderer renderer;
    private boolean isMouseInput= true;
    protected static boolean isTerrainEditor = false;
    private String obstacleType = "Box";
    private TerrainEditor terrainEditor;
    private ArrayList<CollisionResults> collisions = new ArrayList<>();
    */

    public Settings(Renderer renderer) {
        initComponents();

        this.renderer = renderer;
        
        ////default game state
        manualTB.setSelected(true);
    }

    /*
    public boolean isMouseInput() {
        return isMouseInput;
    }
    
    public boolean isTerrainEditor() {
        return isTerrainEditor;
    }
    */

    @SuppressWarnings("unchecked")

    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        updateB = new javax.swing.JButton();
        editTreesTB = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        editRocksTB = new javax.swing.JToggleButton();
        terrainTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        manualTB = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        botsDD = new javax.swing.JComboBox<>();
        runBotB = new javax.swing.JButton();
        backB = new javax.swing.JButton();
        resetB = new javax.swing.JButton();

        jLabel4.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 255));
        jLabel4.setText("MODES");
        jLabel4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));

        jPanel1.setBackground(new java.awt.Color(0, 153, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 51, 204), 3));

        updateB.setBackground(new java.awt.Color(0, 204, 0));
        updateB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        updateB.setForeground(new java.awt.Color(51, 51, 51));
        updateB.setText("UPDATE");
        updateB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        updateB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBActionPerformed(evt);
            }
        });

        editTreesTB.setBackground(new java.awt.Color(0, 204, 0));
        buttonGroup1.add(editTreesTB);
        editTreesTB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        editTreesTB.setForeground(new java.awt.Color(51, 51, 51));
        editTreesTB.setText("EDIT TREES");
        editTreesTB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 153), 4));
        editTreesTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editTreesTBActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("MODES");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        editRocksTB.setBackground(new java.awt.Color(0, 204, 0));
        buttonGroup1.add(editRocksTB);
        editRocksTB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        editRocksTB.setForeground(new java.awt.Color(51, 51, 51));
        editRocksTB.setText("EDIT ROCKS");
        editRocksTB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 153), 4));
        editRocksTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRocksTBActionPerformed(evt);
            }
        });

        terrainTF.setBackground(new java.awt.Color(0, 204, 204));
        terrainTF.setForeground(new java.awt.Color(0, 0, 0));

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("TERRAIN MODEL");

        manualTB.setBackground(new java.awt.Color(0, 204, 0));
        buttonGroup1.add(manualTB);
        manualTB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        manualTB.setForeground(new java.awt.Color(51, 51, 51));
        manualTB.setText("PLAYER");
        manualTB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 153), 4));
        manualTB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualTBActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("BOTS");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        botsDD.setBackground(new java.awt.Color(0, 204, 0));
        botsDD.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        botsDD.setForeground(new java.awt.Color(51, 51, 51));
        botsDD.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hill Climbing", "Gradient Descent", "Rule Based" }));
        botsDD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botsDDActionPerformed(evt);
            }
        });

        runBotB.setBackground(new java.awt.Color(0, 204, 0));
        runBotB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        runBotB.setForeground(new java.awt.Color(51, 51, 51));
        runBotB.setText("RUN BOT");
        runBotB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        runBotB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBotBActionPerformed(evt);
            }
        });

        backB.setBackground(new java.awt.Color(0, 0, 0));
        backB.setFont(new java.awt.Font("Gill Sans MT Ext Condensed Bold", 0, 36)); // NOI18N
        backB.setForeground(new java.awt.Color(51, 204, 0));
        backB.setText("<");
        backB.setPreferredSize(new java.awt.Dimension(120, 40));
        backB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backBActionPerformed(evt);
            }
        });

        resetB.setBackground(new java.awt.Color(0, 204, 255));
        resetB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        resetB.setForeground(new java.awt.Color(51, 51, 51));
        resetB.setText("RESET GAME");
        resetB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        resetB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(terrainTF, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(manualTB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editTreesTB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editRocksTB, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backB, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(resetB, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(botsDD, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(runBotB, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(updateB, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(terrainTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(updateB)
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(manualTB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editTreesTB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editRocksTB, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botsDD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(runBotB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(backB, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(resetB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void updateBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBActionPerformed
        String newTerrainFormula =terrainTF.getText();

        //set new terrain

        //update

    }//GEN-LAST:event_updateBActionPerformed

    private void editTreesTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editTreesTBActionPerformed
        //end/change edit mode 

        //set to edit mode

        //obstacleType = "Tree";
    }//GEN-LAST:event_editTreesTBActionPerformed

    private void editRocksTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRocksTBActionPerformed
        //end/change edit mode 

        //set to edit mode

        //obstacleType = "Box";
    }//GEN-LAST:event_editRocksTBActionPerformed

    private void manualTBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualTBActionPerformed
        //end edit mode

        //set manual mode

    }//GEN-LAST:event_manualTBActionPerformed

    private void runBotBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBotBActionPerformed
        switch (botsDD.getSelectedItem().toString()) {
            case "Hill Climbing":
                //set bot and run
                break;
            case "Gradient Descent":
                //set bot and run
                break;
            case "Rule Based":
                //set bot and run
                break;
        }
        
        //Hill Climbing, Gradient Descent, Rule Based
        //System.out.println(botsDD.getSelectedItem());

    }//GEN-LAST:event_runBotBActionPerformed

    private void backBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backBActionPerformed
        //end game

        new MenuGUI().setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_backBActionPerformed

    private void resetBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBActionPerformed
        /*
        renderer.getUpdateLoop().resetGame();
        renderer.moveBall(renderer.ball.state.position);
        */
        
    }//GEN-LAST:event_resetBActionPerformed

    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Settings().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backB;
    private javax.swing.JComboBox<String> botsDD;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton editRocksTB;
    private javax.swing.JToggleButton editTreesTB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton manualTB;
    private javax.swing.JButton resetB;
    private javax.swing.JButton runBotB;
    private javax.swing.JTextField terrainTF;
    private javax.swing.JButton updateB;
    // End of variables declaration//GEN-END:variables
}
