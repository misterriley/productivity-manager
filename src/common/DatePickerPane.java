package common;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import datepicker.DateModel;
import datepicker.JDatePicker;

public class DatePickerPane
{
	private JDatePicker m_picker;
	private boolean     m_isWindowClosed;

	private synchronized boolean isWindowClosed()
	{
		// TODO Auto-generated method stub
		return m_isWindowClosed;
	}

	public DateModel<?> response()
	{
		while (true)
		{
			if (isWindowClosed())
			{
				break;
			}

			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return m_picker.getModel();
	}

	synchronized void setIsWindowClosed(boolean p_isWindowClosed)
	{
		m_isWindowClosed = p_isWindowClosed;
	}

	public void showPanel()
	{
		try
		{
			UIManager.setLookAndFeel(Messages.getString("DatePickerPane.0")); //$NON-NLS-1$
		}
		catch (final Exception e)
		{
			// uhh, what do I do here?
		}
		final JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		testFrame.setSize(500, 500);
		final JPanel jPanel = new JPanel();
		m_picker = new JDatePicker();
		m_picker.setTextEditable(true);
		m_picker.setShowYearButtons(true);
		jPanel.add(m_picker);
		final JPanel DatePanel = new JPanel();
		DatePanel.setLayout(new BorderLayout());
		DatePanel.add(jPanel, BorderLayout.WEST);
		final BorderLayout fb = new BorderLayout();
		testFrame.setLayout(fb);
		testFrame.getContentPane().add(DatePanel, BorderLayout.WEST);
		testFrame.setVisible(true);

		testFrame.addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent p_arg0)
			{
				// nothing
			}

			@Override
			public void windowClosed(WindowEvent p_arg0)
			{
				setIsWindowClosed(true);
			}

			@Override
			public void windowClosing(WindowEvent p_arg0)
			{
				// nothing

			}

			@Override
			public void windowDeactivated(WindowEvent p_arg0)
			{
				// nothing

			}

			@Override
			public void windowDeiconified(WindowEvent p_arg0)
			{
				// nothing

			}

			@Override
			public void windowIconified(WindowEvent p_arg0)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void windowOpened(WindowEvent p_arg0)
			{
				// TODO Auto-generated method stub

			}
		});
	}
}
