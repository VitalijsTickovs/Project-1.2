package gui;//package crazyPutting;

import visualization.gameengine.Game;
import visualization.jmonkeyrender.Renderer;

public class MenuGUI extends javax.swing.JFrame {
    public static String texPath;
    
    public MenuGUI() {
        initComponents();
        
        levelCB.getSelectedIndex();
        getTexPath();
    }

    public static String getTexPath(){
        if (levelCB.getSelectedIndex() == 0) {
            // set path String to grass terrain
            texPath = "Terrain/grass2.jpeg";
        }
        if (levelCB.getSelectedIndex() == 1) {
            // set path String to moon terrain
            texPath = "Terrain/Moon_ground.png";
        }
        if (levelCB.getSelectedIndex() == 2) {
            // set path String to mars terrain
            texPath = "Terrain/Mars_ground.jpg";
        }
        return texPath;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        singleB = new javax.swing.JButton();
        multiB = new javax.swing.JButton();
        exitB = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        levelCB = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu");
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));

        jPanel3.setBackground(new java.awt.Color(0, 102, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 255), 3));

        singleB.setBackground(new java.awt.Color(0, 204, 0));
        singleB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        singleB.setForeground(new java.awt.Color(51, 51, 51));
        singleB.setText("3D");
        singleB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        singleB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleBActionPerformed(evt);
            }
        });

        multiB.setBackground(new java.awt.Color(0, 204, 0));
        multiB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        multiB.setForeground(new java.awt.Color(51, 51, 51));
        multiB.setText("2D");
        multiB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        multiB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multiBActionPerformed(evt);
            }
        });

        exitB.setBackground(new java.awt.Color(0, 0, 0));
        exitB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        exitB.setForeground(new java.awt.Color(204, 204, 204));
        exitB.setText("EXIT :(");
        exitB.setPreferredSize(new java.awt.Dimension(120, 40));
        exitB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitBActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(0, 102, 255));
        jLabel3.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 153));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CRAZY PUTTING");

        levelCB.setBackground(new java.awt.Color(0, 204, 0));
        levelCB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        levelCB.setForeground(new java.awt.Color(51, 51, 51));
        levelCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hill Climbing", "Gradient Descent", "Rule Based" }));
        levelCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelCBActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("LEVEL");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(exitB, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(multiB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(singleB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(120, 120, 120))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(125, 125, 125)
                .addComponent(levelCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(singleB, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multiB, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(exitB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void singleBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleBActionPerformed
        
        /////FETCH LEVEL INDEX
        ////FETCH PLAYER/BOT BOOLEAN
        /////OPEN PUTTING RENDER
        
        //open settings
        new SettingsGUI().setVisible(true);
        
        Renderer render = new Renderer();
        render.start3d();
        this.dispose();
        
    }//GEN-LAST:event_singleBActionPerformed

    private void multiBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiBActionPerformed
        
        /////FETCH LEVEL INDEX
        /////OPEN PUTTING RENDER
        //render.start3d();
        
        //open settings
        new SettingsGUI().setVisible(true);
        
        Game game = new Game(60);
        game.start();
        this.setVisible(false);
        
    }//GEN-LAST:event_multiBActionPerformed

    private void exitBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitBActionPerformed
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HighscoreGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HighscoreGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HighscoreGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HighscoreGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuGUI().setVisible(true);
            }
        });
    }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.ButtonGroup buttonGroup1;
   private javax.swing.JButton exitB;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JPanel jPanel3;
   private static javax.swing.JComboBox<String> levelCB;
   private javax.swing.JButton multiB;
   private javax.swing.JButton singleB;
   // End of variables declaration//GEN-END:variables
}
