package de.agbauer.physik.PeemHistoryBrowser;

import de.agbauer.physik.Constants;
import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.PeemHistory.PEEMHistoryController;

import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class PeemHistoryBrowserController {

    private final PeemHistoryBrowserForm peemHistoryBrowserForm;
    private final PEEMHistoryController peemHistoryController;

    public PeemHistoryBrowserController(DataFiler dataFiler) {
        this.peemHistoryBrowserForm = new PeemHistoryBrowserForm();
        this.peemHistoryController = new PEEMHistoryController(this.peemHistoryBrowserForm.peemHistoryForm, dataFiler);

        this.peemHistoryBrowserForm.directoryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                TreePath treePath = peemHistoryBrowserForm.directoryTree.getSelectionPath();
                String selectedPath = Constants.defaultFileSaveFolder;
                for (int i = 2; i < treePath.getPath().length; i++) {
                    String pathComponent = treePath.getPath()[i].toString();
                    selectedPath += pathComponent + Constants.pathSeparator;
                }

                peemHistoryController.loadDirectory(new File(selectedPath));
            }
        });

    }
}
