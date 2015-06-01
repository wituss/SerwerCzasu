package pl.edu.wat.wcy;

import javax.swing.*;
import java.awt.*;

/**
 * Created by wiciu on 31.05.15.
 */
public class ServerGUI extends JFrame{

    private JButton bUpdate;
    private JList clientList;
    private JTextArea LOG;

    public ServerGUI() throws HeadlessException {
        initUI();
    }

    private void initUI() {
        setTitle("Time Server");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JTextArea LOG = new JTextArea();
        scrollPane.setSize(150,160);
        scrollPane.add(LOG);
        JList<String> clientList = new JList<>();
        clientList.setSize(150,160);
        LOG.append("dsadsadasdasdasdasdasdasdasdasdasd");
        mainPanel.add(scrollPane,LEFT_ALIGNMENT);
        mainPanel.add(clientList,RIGHT_ALIGNMENT);
        mainPanel.add(new JButton(),BOTTOM_ALIGNMENT);
        add(mainPanel);

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ServerGUI serverGUI = new ServerGUI();
                serverGUI.setVisible(true);
            }
        });
    }
}
