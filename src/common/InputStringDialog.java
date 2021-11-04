package common;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class InputStringDialog extends JOptionPane
{
	/**
	 *
	 */
	private static final long serialVersionUID = 689472856890095133L;

	public static double getInputDouble(final JFrame p_frame, final String p_prompt, final double p_default)
	{
		double ret = 0;

		while (true)
		{
			try
			{
				final String str = getInputString(p_prompt, String.valueOf(p_default));
				ret = Double.parseDouble(str);
				break;
			}
			catch (final NumberFormatException e)
			{
				JOptionPane.showMessageDialog(p_frame, Messages.getString("InputStringDialog.0")); //$NON-NLS-1$
			}
		}

		return ret;
	}

	public static String getInputString(final String p_prompt, final String p_defaultText)
	{
		final JOptionPane optionPane = new InputStringDialog(p_defaultText);

		final JDialog dialog = new JDialog((JFrame) null, p_prompt, true);
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

		return (String) optionPane.getValue();
	}

	public static void main(final String[] p_args)
	{
		System.out
			.println(
				getInputString(
					Messages.getString("InputStringDialog.1"), //$NON-NLS-1$
					Messages.getString("InputStringDialog.2"))); //$NON-NLS-1$
	}

	private final JTextField m_textField;

	private InputStringDialog(final String p_defaultText)
	{
		super(
			Messages.getString("InputStringDialog.3"), //$NON-NLS-1$
			JOptionPane.QUESTION_MESSAGE,
			JOptionPane.DEFAULT_OPTION,
			null,null,
			null);

		setSize(500, 500);
		setLayout(new BorderLayout());
		final JPanel jPanel = new JPanel();
		m_textField = new JTextField(p_defaultText, 30);
		jPanel.add(m_textField);

		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(jPanel, BorderLayout.WEST);

		final JPanel southPanel = new JPanel();
		final JButton selectButton = new JButton(Messages.getString("InputStringDialog.4")); //$NON-NLS-1$
		selectButton.addActionListener(p_arg0 -> setValue(m_textField.getText()));

		southPanel.add(selectButton);

		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);
	}
}
