package common;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;

/**
 * @author Steve Riley
 */
public class TaskPullPanel extends JPanel
{
	/**
	 * WTF is this for? No idea
	 */
	private static final long serialVersionUID = -6261407565958923776L;

	private static boolean doTaskCompletion()
	{
		return PMMainController.markSelectedTaskAsComplete();
	}

	private final MainModel                m_model;
	private final JSlider                  m_prioritySensitivitySlider;
	private final ScheduledExecutorService m_snoozeThreadPool;

	private final MainView                 m_view;

	/**
	 *
	 */
	public TaskPullPanel(final MainModel p_model, final MainView p_view)
	{
		super(new GridLayout(2, 1));

		m_snoozeThreadPool = Executors.newScheduledThreadPool(1);

		m_model            = p_model;
		m_view             = p_view;

		final JPanel panel1    = new JPanel();
		final JPanel subPanel1 = new JPanel();
		m_prioritySensitivitySlider =
			new JSlider(0, Constants.MAX_PRIORITY_SENSITIVITY, Constants.DEFAULT_PRIORITY_SENSITIVITY);

		final JButton pullButton = new JButton(Messages.getString("TaskPullPanel.0")); //$NON-NLS-1$
		pullButton.addActionListener(p_arg0 ->
		{ pullTasks(); });
		subPanel1.add(pullButton);

		final JButton renormalizeButton = new JButton(Messages.getString("TaskPullPanel.1")); //$NON-NLS-1$
		renormalizeButton.addActionListener(p_arg0 ->
		{
			PMMainController.renormalizePriorities();
			JOptionPane.showMessageDialog(this,
				Messages.getString("TaskPullPanel.2") + Constants.TASK_TARGET_PRIORITY_MEAN //$NON-NLS-1$
					+ Messages.getString("TaskPullPanel.3") + Constants.TASK_TARGET_PRIORITY_SD); //$NON-NLS-1$
		});
		subPanel1.add(renormalizeButton);
		panel1.add(subPanel1);
		add(panel1);

		final JPanel panel2    = new JPanel();
		final JPanel subPanel2 = new JPanel(new FlowLayout());
		subPanel2.add(new JLabel(Messages.getString("TaskPullPanel.4"))); //$NON-NLS-1$

		final JTextField textField = new JTextField(3);
		textField.setText(String.valueOf(m_prioritySensitivitySlider.getValue()));
		textField.setEditable(false);
		m_prioritySensitivitySlider.addChangeListener(p_arg0 ->
		{ textField.setText(String.valueOf(m_prioritySensitivitySlider.getValue())); });
		subPanel2.add(m_prioritySensitivitySlider);
		subPanel2.add(textField);
		panel2.add(subPanel2);
		add(panel2);
	}

	private boolean displayTask(final Task p_task)
	{
		m_view.getTaskManagementPanel().getTaskTree().setSelectedTask(p_task);
		final ResponseOption[] options = ResponseOption.values();
		String                 desc    = p_task.getDescription();
		if (p_task.hasDueDate() && p_task.isRepeatingTask())
		{
			desc += Messages.getString("TaskPullPanel.5"); //$NON-NLS-1$
			if (p_task.getRepeatRestartType() == RepeatRestartType.ON_DUE_DATE)
			{
				desc += Messages.getString("TaskPullPanel.6") + p_task.getDueLocalDate() //$NON-NLS-1$
					+ Messages.getString("TaskPullPanel.7"); //$NON-NLS-1$
			}
			desc += Messages.getString("TaskPullPanel.8") + p_task.getRepeatPeriodCount() //$NON-NLS-1$
				+ Messages.getString("TaskPullPanel.9") + p_task.getRepeatPeriodType() //$NON-NLS-1$
				+ Messages.getString("TaskPullPanel.10"); //$NON-NLS-1$
		}
		final int response =
			JOptionPane.showOptionDialog(this, desc, Messages.getString("TaskPullPanel.11"), JOptionPane.DEFAULT_OPTION, //$NON-NLS-1$
				JOptionPane.QUESTION_MESSAGE, null, ResponseOption.values(), null);

		if (response == JOptionPane.CLOSED_OPTION)
		{
			return false;
		}

		boolean ret = false;
		switch (options[response])
		{
			case COMPLETE:
				ret = doTaskCompletion();
				break;
			case SNOOZE:
				ret = doSnoozing(p_task);
				break;
			default:
				throw new RuntimeException();
		}

		return ret;
	}

	private boolean doSnoozing(final Task p_task)
	{
		m_snoozeThreadPool.schedule(() ->
		{ p_task.setSnoozing(false); }, Constants.DEFAULT_SNOOZE_MINUTES * 60, TimeUnit.SECONDS);
		p_task.setSnoozing(true);
		return true;
	}

	private void pullTasks()
	{
		boolean breakLoop = false;
		while (!breakLoop)
		{
			final Collection<Task> tasks          = m_model.getTasks().values();
			final int              sensitivity    = m_prioritySensitivitySlider.getValue();
			final int              maxSensitivity = m_prioritySensitivitySlider.getMaximum();

			final Task             task           = PMMainController.pullTask(tasks, sensitivity, maxSensitivity);
			if (task == null)
			{
				JOptionPane.showMessageDialog(this, Messages.getString("TaskPullPanel.12")); //$NON-NLS-1$
				breakLoop = true;
			}
			else
			{
				breakLoop = !displayTask(task);
			}
		}
	}
}
