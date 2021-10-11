package common;

import java.awt.BorderLayout;
import java.time.LocalDate;

import javax.swing.*;

import datepicker.JDatePicker;

public class DatePickerDialog extends JOptionPane
{
	/**
	 *
	 */
	private static final long serialVersionUID = -3511960440606047358L;

	public static LocalDate getDate(JFrame p_frame)
	{
		final JOptionPane optionPane = new DatePickerDialog();

		final JDialog     dialog     = new JDialog(p_frame, Messages.getString("DatePickerDialog.0"), true); //$NON-NLS-1$
		dialog.setContentPane(optionPane);
		dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		optionPane.addPropertyChangeListener(e ->
		{
			final String prop = e.getPropertyName();

			if (dialog.isVisible() && e.getSource() == optionPane && prop.equals(JOptionPane.VALUE_PROPERTY))
			{
				// If you were going to check something
				// before closing the window, you'd do
				// it here.
				dialog.setVisible(false);
			}
		});

		dialog.pack();
		dialog.setVisible(true);

		return (LocalDate) optionPane.getValue();
	}

	public static void main(String[] p_args)
	{
		System.out.println(getDate(new JFrame()));
	}

	private final JDatePicker m_picker;

	public DatePickerDialog()
	{
		super(Messages.getString("DatePickerDialog.1"), JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, //$NON-NLS-1$
			null, null);

		setSize(500, 500);
		setLayout(new BorderLayout());
		final JPanel jPanel = new JPanel();
		m_picker = new JDatePicker();
		m_picker.setTextEditable(true);
		m_picker.setShowYearButtons(true);
		jPanel.add(m_picker);

		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(jPanel, BorderLayout.WEST);

		final JPanel  southPanel   = new JPanel();
		final JButton selectButton = new JButton(Messages.getString("DatePickerDialog.2")); //$NON-NLS-1$
		selectButton.addActionListener(p_arg0 -> setValue(PMMainController.getDateObject(m_picker.getModel())));

		southPanel.add(selectButton);

		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}
}
