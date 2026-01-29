package tool.hrfExplorer;

import core.model.TranslationFacility;

import javax.swing.*;
import java.awt.*;

public class HrfExplorerDialog extends JDialog {

	private static final long serialVersionUID = -6591856825578209977L;

	public HrfExplorerDialog(JFrame owner, core.context.ApplicationContext context) {
		super(owner, true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize(context);
	}

	private void initialize(core.context.ApplicationContext context) {
		setSize(1024, 668);
		setLayout(new BorderLayout());
		setTitle(TranslationFacility.tr("Tab_HRF-Explorer"));

		add(new HrfExplorer(context), BorderLayout.CENTER);

	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);

		Dimension screenSize = getParent().getSize();
		int x = (screenSize.width - getWidth()) / 2;
		int y = (screenSize.height - getHeight()) / 2;

		setLocation(getParent().getX() + x, getParent().getY() + y);
	}
}
