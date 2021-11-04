package common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

/**
 * The view class of the Model-View-Controller paradigm for the productivity
 * manager.
 *
 * @author Steve Riley
 */
public class MainView
{
	private TaskManagementPanel m_taskManagementPanel;

	private final JFrame		m_frame;
	private final JTabbedPane	m_tabbedPane;

	/**
	 * Creates the MainView object, which displays the UI for the the
	 * productivity manager
	 */
	public MainView(final MainModel p_mainModel)
	{
		m_frame = new JFrame(Messages.getString("MainView.0")); //$NON-NLS-1$
		m_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		final JPanel tabbedPanePanel = new JPanel(new GridLayout(1, 1));
		m_tabbedPane = getTabbedPane(p_mainModel);
		tabbedPanePanel.add(m_tabbedPane);

		// Add content to the window.
		m_frame.add(m_tabbedPane, BorderLayout.CENTER);
		m_frame.setLocation(324, 200);
		m_frame.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));
	}

	public JFrame getFrame()
	{
		return m_frame;
	}

	public TaskManagementPanel getTaskManagementPanel()
	{
		return m_taskManagementPanel;
	}

	/**
	 * Makes the frame visible
	 */
	public void makeItSo()
	{
		m_frame.pack();
		m_frame.setVisible(true);
	}

	public void showTaskManagementPanel()
	{
		m_tabbedPane.setSelectedIndex(1);
	}

	private JTabbedPane getTabbedPane(final MainModel p_mainModel)
	{
		final JTabbedPane tabbedPane = new JTabbedPane();
		m_taskManagementPanel = new TaskManagementPanel(p_mainModel);

		tabbedPane.addTab(Messages.getString("MainView.1"), null, new TaskPullPanel(p_mainModel, this), null); //$NON-NLS-1$
		tabbedPane.addTab(Messages.getString("MainView.2"), null, m_taskManagementPanel, null); //$NON-NLS-1$

		// The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		return tabbedPane;
	}
}
