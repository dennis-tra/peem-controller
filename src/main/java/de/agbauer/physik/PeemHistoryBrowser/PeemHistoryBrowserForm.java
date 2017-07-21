package de.agbauer.physik.PeemHistoryBrowser;

import de.agbauer.physik.Constants;
import de.agbauer.physik.PeemHistory.PEEMHistoryForm;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;

public class PeemHistoryBrowserForm extends JFrame {
    JTree directoryTree;
    PEEMHistoryForm peemHistoryForm;
    private JPanel rootPanel;
    private JScrollPane directoryTreeScrollPane;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode lastChild;

    PeemHistoryBrowserForm() {
        super();

        setContentPane(rootPanel);
        setLocationRelativeTo(null);
        pack();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.out.print("Couldn't load system look and feel for ");
        }

        setTitle("PEEM History Browser");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void createUIComponents() {
        this.root = new DefaultMutableTreeNode("root", true);
        getList(root, new File(Constants.defaultFileSaveFolder));
        this.directoryTree = new JTree(root);
        this.directoryTree.setRootVisible(false);

        this.directoryTree.setSelectionPath(new TreePath(this.lastChild.getPath()));
        this.directoryTree.expandPath(new TreePath(this.lastChild.getPath()));
    }

    private void getList(DefaultMutableTreeNode node, File f) {
        if(f.isDirectory()) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(f.getName());
            node.add(child);
            File fList[] = f.listFiles();
            for(int i = 0; i  < fList.length; i++) {
                getList(child, fList[i]);
                lastChild = child;
            }

        }
    }
}
