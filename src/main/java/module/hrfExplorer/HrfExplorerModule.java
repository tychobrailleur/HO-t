package module.hrfExplorer;

import core.module.DefaultModule;
import core.module.IModule;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class HrfExplorerModule extends DefaultModule {
    @Override
    public int getModuleId() {
        return IModule.NEW_HRF_EXPLORER;
    }

    @Override
    public String getDescription() {
        return "New HRF Explorer";
    }

    @Override
    public JPanel createTabPanel() {
        return new HrfExplorerPanel();
    }

    @Override
    public KeyStroke getKeyStroke() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK);
    }
}
