package org.eu.hanana.reimu.mc.chatimage;


import org.eu.hanana.reimu.mc.chatimage.layout.TableLayout;
import org.eu.hanana.reimu.mc.chatimage.layout.TableLayoutConstraints;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WinRun extends JFrame{
    static WinRun jf;
    public static void main(String[] args) {
        jf = new WinRun();
        jf.initComponents();
        jf.setVisible(true);
    }
    private void okButtonMouseClicked(MouseEvent e) {
        Dimension ld;
        while (jf.getSize().width>2||jf.getSize().width>2){
            ld=jf.getSize();
            jf.setSize((int) (jf.getSize().width/1.1), (int) (jf.getSize().height/1.1));
            if (ld.equals(jf.getSize())) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        jf.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new TableLayout(new double[][] {
                    {TableLayout.FILL},
                    {TableLayout.PREFERRED, TableLayout.PREFERRED}}));

                //---- label1 ----
                label1.setText("\u8fd9\u662f\u4e00\u4e2amc\u6a21\u7ec4\uff0c\u4e0d\u8981\u53cc\u51fb\u8fd0\u884c");
                label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 8f));
                contentPanel.add(label1, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.LEFT, TableLayoutConstraints.FULL));

                //---- label2 ----
                label2.setText("\u5c06\u8fd9\u4e2a\u6587\u4ef6\u653e\u5230Minecraft\u7684mod\u6587\u4ef6\u5939\u4e2d\u5373\u53ef\u751f\u6548");
                label2.setFont(label2.getFont().deriveFont(label2.getFont().getSize() + 7f));
                contentPanel.add(label2, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.LEFT, TableLayoutConstraints.FULL));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        okButtonMouseClicked(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JLabel label2;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
