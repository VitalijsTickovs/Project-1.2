package GUI;//package crazyPutting;

import JMonkeyRender.Renderer;

public class MenuGUI extends javax.swing.JFrame {
    static boolean gameBoolean =true;//false =player game; true =bot game
    static int levelNum =0;//0 =Earth; 1 =Moon; 2 =Mars; 3 =Test
    
    public MenuGUI() {
        initComponents();
        botRB.setSelected(gameBoolean);//bot game is default
        playerRB.setSelected(!gameBoolean);
        
        levelCB.setSelectedIndex(levelNum);
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel3 = new javax.swing.JPanel();
        levelCB = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        singleB = new javax.swing.JButton();
        multiB = new javax.swing.JButton();
        exitB = new javax.swing.JButton();
        botRB = new javax.swing.JRadioButton();
        playerRB = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        highscoreB = new javax.swing.JButton();
        levelDetailsB = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu");
        setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));

        jPanel3.setBackground(new java.awt.Color(0, 102, 0));

        levelCB.setBackground(new java.awt.Color(0, 153, 0));
        levelCB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        levelCB.setForeground(new java.awt.Color(51, 51, 51));
        levelCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Earth", "Moon", "Mars", "Test" }));
        levelCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelCBActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("AGAINST");

        singleB.setBackground(new java.awt.Color(0, 204, 0));
        singleB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        singleB.setForeground(new java.awt.Color(51, 51, 51));
        singleB.setText("SINGLEPLAYER");
        singleB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        singleB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                singleBActionPerformed(evt);
            }
        });

        multiB.setBackground(new java.awt.Color(0, 204, 0));
        multiB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 24)); // NOI18N
        multiB.setForeground(new java.awt.Color(51, 51, 51));
        multiB.setText("MULTIPLAYER");
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

        buttonGroup1.add(botRB);
        botRB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 14)); // NOI18N
        botRB.setText("BOT");
        botRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botRBActionPerformed(evt);
            }
        });

        buttonGroup1.add(playerRB);
        playerRB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 14)); // NOI18N
        playerRB.setText("PLAYER");
        playerRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playerRBActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Rockwell Condensed", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("LEVEL:");

        highscoreB.setBackground(new java.awt.Color(0, 204, 0));
        highscoreB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        highscoreB.setForeground(new java.awt.Color(51, 51, 51));
        highscoreB.setText("HIGHSCORES");
        highscoreB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        highscoreB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highscoreBActionPerformed(evt);
            }
        });

        levelDetailsB.setBackground(new java.awt.Color(0, 204, 0));
        levelDetailsB.setFont(new java.awt.Font("Tw Cen MT Condensed Extra Bold", 0, 18)); // NOI18N
        levelDetailsB.setForeground(new java.awt.Color(51, 51, 51));
        levelDetailsB.setText("LEVEL DETAILS");
        levelDetailsB.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 255), 4));
        levelDetailsB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelDetailsBActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Rockwell Extra Bold", 1, 48)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 153));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CRAZY PUTTING");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(82, 82, 82))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(botRB)
                        .addGap(18, 18, 18)
                        .addComponent(playerRB)
                        .addGap(54, 54, 54)))
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(89, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(multiB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(singleB, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(142, 142, 142))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(exitB, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(highscoreB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(levelDetailsB, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel3)
                .addGap(69, 69, 69)
                .addComponent(singleB, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(multiB, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(levelCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(playerRB)
                            .addComponent(botRB))))
                .addGap(60, 60, 60)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(levelDetailsB, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highscoreB, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    Renderer render = new Renderer();
    private void singleBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_singleBActionPerformed
  
        /////FETCH LEVEL INDEX
        /////OPEN PUTTING RENDER
        render.start3d();
        
        this.setVisible(false);
    }//GEN-LAST:event_singleBActionPerformed

    private void multiBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multiBActionPerformed
        
        /////FETCH LEVEL INDEX
        ////FETCH PLAYER/BOT BOOLEAN
        /////OPEN PUTTING RENDER
        render.start3d();
        
        this.setVisible(false);
    }//GEN-LAST:event_multiBActionPerformed

    private void exitBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitBActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitBActionPerformed

    private void highscoreBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highscoreBActionPerformed
        new HighscoreGUI().setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_highscoreBActionPerformed

    private void levelDetailsBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelDetailsBActionPerformed
        
    }//GEN-LAST:event_levelDetailsBActionPerformed

    private void botRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botRBActionPerformed
        gameBoolean =true;
    }//GEN-LAST:event_botRBActionPerformed

    private void playerRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playerRBActionPerformed
        gameBoolean =false;
    }//GEN-LAST:event_playerRBActionPerformed

    private void levelCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelCBActionPerformed
        levelNum = levelCB.getSelectedIndex(); 
    }//GEN-LAST:event_levelCBActionPerformed
    
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
    private javax.swing.JRadioButton botRB;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton exitB;
    private javax.swing.JButton highscoreB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox<String> levelCB;
    private javax.swing.JButton levelDetailsB;
    private javax.swing.JButton multiB;
    private javax.swing.JRadioButton playerRB;
    private javax.swing.JButton singleB;
    // End of variables declaration//GEN-END:variables
}
