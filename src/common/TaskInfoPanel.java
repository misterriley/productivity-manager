package common;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import datepicker.JDatePicker;

public class TaskInfoPanel extends JPanel
{
	/**
	 *
	 */
	private static final long				serialVersionUID	= -3322818822898952722L;
	private JSlider							m_prioritySlider;
	private JTextField						m_priorityField;
	private JTextField						m_descriptionField;
	private JCheckBox						m_repeatCheckbox;
	private JLabel							m_repeatPeriodLabel;
	private JTextField						m_repeatPeriodField;
	private JComboBox<RepeatPeriodType>		m_repeatPeriodComboBox;
	private JCheckBox						m_hasDueDateCheckbox;
	private JCheckBox						m_hasOpenDateCheckbox;
	private JComboBox<RepeatRestartType>	m_repeatRestartComboBox;
	private JDatePicker						m_openDatePicker;
	private JDatePicker						m_dueDatePicker;
	private JDatePicker						m_lastCompletionDatePicker;

	private JLabel m_timerResetsLabel;

	private final TaskManagementPanel	m_taskManagementPanel;
	private JLabel						m_lastCompletionDateLabel;
	private JCheckBox					m_childrenAreOrderedCheckbox;
	private JLabel						m_currentChildTaskLabel;
	private JTextField					m_currentChildTaskTextField;

	private Task m_currentlyDisplayedTask;

	public TaskInfoPanel(final TaskManagementPanel p_taskManagementPanel)
	{
		super(new GridLayout(5, 1));

		m_taskManagementPanel = p_taskManagementPanel;

		buildAndAddNamePanel();
		buildAndAddDatePanel();
		buildAndAddRepeatPanel();
		buildAndAddPriorityPanel();
		buildAndAddSavePanel();
	}

	public void refreshCurrentTask()
	{
		setCurrentTask(m_currentlyDisplayedTask);
	}

	public void saveUIToCurrentTask()
	{
		saveUIToTask(m_currentlyDisplayedTask);
	}

	public void saveUIToTask(final Task p_task)
	{
		if (p_task == null)
		{
			return;
		}

		p_task.setDescription(m_descriptionField.getText());
		p_task.setDueDate(m_dueDatePicker.getModel().getDay());
		p_task.setDueMonth(m_dueDatePicker.getModel().getMonthButWrong() + 1);
		p_task.setDueYear(m_dueDatePicker.getModel().getYear());
		p_task.setHasDueDate(m_hasDueDateCheckbox.isSelected());
		p_task.setHasOpeningDate(m_hasOpenDateCheckbox.isSelected());
		p_task.setOpeningDate(m_openDatePicker.getModel().getDay());
		p_task.setOpeningMonth(m_openDatePicker.getModel().getMonthButWrong() + 1);
		p_task.setOpeningYear(m_openDatePicker.getModel().getYear());
		p_task.setPriority(m_prioritySlider.getValue());
		p_task.setRepeatingTask(m_repeatCheckbox.isSelected());

		p_task.setRepeatPeriodType((RepeatPeriodType) m_repeatPeriodComboBox.getSelectedItem());
		p_task.setRepeatRestartType((RepeatRestartType) m_repeatRestartComboBox.getSelectedItem());
		p_task.setChildrenAreOrdered(m_childrenAreOrderedCheckbox.isSelected());

		// TODO - figure out what to do with "last completion date"

		try
		{
			final String text = m_repeatPeriodField.getText();
			if (!text.equals(Messages.getString("TaskInfoPanel.14"))) //$NON-NLS-1$
			{
				p_task.setRepeatPeriodCount(Integer.parseInt(text));
			}
		}
		catch (final NumberFormatException ioex)
		{
			JOptionPane.showMessageDialog(this, Messages.getString("TaskInfoPanel.15")); //$NON-NLS-1$
		}

		if (p_task.isRepeatingTask() && (!p_task.hasDueDate() || !p_task.hasOpeningDate()))
		{
			JOptionPane
				.showMessageDialog(
					this,
					Messages.getString("TaskInfoPanel.16"), //$NON-NLS-1$
					Messages.getString("TaskInfoPanel.17"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
		}

		PMMainController.saveModel();
		m_taskManagementPanel.getTaskTree().refresh(p_task.getNode());
	}

	public void setCurrentTask(final Task p_currentTask)
	{
		m_currentlyDisplayedTask = p_currentTask;

		if (p_currentTask == null)
		{
			m_repeatCheckbox.setSelected(false);
			m_hasDueDateCheckbox.setSelected(false);
			m_hasOpenDateCheckbox.setSelected(false);
			m_descriptionField.setText(Messages.getString("TaskInfoPanel.19")); //$NON-NLS-1$
			m_repeatPeriodField.setText(Messages.getString("TaskInfoPanel.20")); //$NON-NLS-1$
			m_prioritySlider.setValue(Constants.DEFAULT_TASK_PRIORITY);
			m_openDatePicker.clearDatePanel();
			m_dueDatePicker.clearDatePanel();
			m_childrenAreOrderedCheckbox.setSelected(false);
			m_childrenAreOrderedCheckbox.setEnabled(false);
			setChildTaskUIElementsEnabled(false);
		}
		else
		{
			m_repeatCheckbox.setSelected(p_currentTask.isRepeatingTask());

			if (p_currentTask.isRepeatingTask())
			{
				m_repeatPeriodComboBox.setSelectedItem(p_currentTask.getRepeatPeriodType());
				m_repeatRestartComboBox.setSelectedItem(p_currentTask.getRepeatRestartType());
				m_repeatPeriodField.setText(String.valueOf(p_currentTask.getRepeatPeriodCount()));
			}
			else
			{
				m_repeatPeriodField.setText(Messages.getString("TaskInfoPanel.21")); //$NON-NLS-1$
			}

			m_hasDueDateCheckbox.setSelected(p_currentTask.hasDueDate());

			if (p_currentTask.hasDueDate())
			{
				m_dueDatePicker
					.setDateExternallyButWrong(
						p_currentTask.getDueYear(),
						p_currentTask.getDueMonth() - 1,
						p_currentTask.getDueDate());
			}
			else
			{
				m_dueDatePicker.clearDatePanel();
			}

			m_hasOpenDateCheckbox.setSelected(p_currentTask.hasOpeningDate());

			if (p_currentTask.hasOpeningDate())
			{
				m_openDatePicker
					.setDateExternallyButWrong(
						p_currentTask.getOpeningYear(),
						p_currentTask.getOpeningMonth() - 1,
						p_currentTask.getOpeningDate());
			}
			else
			{
				m_openDatePicker.clearDatePanel();
			}

			m_descriptionField.setText(p_currentTask.getDescription());
			m_prioritySlider.setValue(p_currentTask.getPriority());

			if (p_currentTask.getNode().getChildCount() == 0)
			{
				m_childrenAreOrderedCheckbox.setEnabled(false);
			}
			else
			{
				m_childrenAreOrderedCheckbox.setEnabled(true);
			}

			m_childrenAreOrderedCheckbox.setSelected(p_currentTask.childrenAreOrdered());
			setChildTaskUIElementsEnabled(p_currentTask.childrenAreOrdered());
			fillLiveChildTaskField(p_currentTask);

			m_taskManagementPanel.getTaskTree().setSelectedTask(m_currentlyDisplayedTask);
		}
	}

	private void buildAndAddDatePanel()
	{
		final JPanel datePanel = new JPanel(new GridLayout(2, 1));
		m_hasOpenDateCheckbox = new JCheckBox(Messages.getString("TaskInfoPanel.0")); //$NON-NLS-1$
		m_openDatePicker = new JDatePicker();

		m_hasOpenDateCheckbox.addChangeListener(p_arg0 ->
		{
			final boolean checked = m_hasOpenDateCheckbox.isSelected();
			m_openDatePicker.setEnabled(checked);
		});

		final JPanel topPanel = new JPanel();
		topPanel.add(m_hasOpenDateCheckbox);
		topPanel.add(m_openDatePicker);
		datePanel.add(topPanel);

		m_hasDueDateCheckbox = new JCheckBox(Messages.getString("TaskInfoPanel.1")); //$NON-NLS-1$
		m_dueDatePicker = new JDatePicker();

		m_hasDueDateCheckbox.addChangeListener(p_arg0 ->
		{
			final boolean checked = m_hasDueDateCheckbox.isSelected();
			m_dueDatePicker.setEnabled(checked);
		});

		final JPanel bottomPanel = new JPanel();
		bottomPanel.add(m_hasDueDateCheckbox);
		bottomPanel.add(m_dueDatePicker);
		datePanel.add(bottomPanel);
		add(datePanel);

		m_openDatePicker.setEnabled(false);
		m_dueDatePicker.setEnabled(false);
	}

	private void buildAndAddNamePanel()
	{
		final JPanel temp = new JPanel();
		temp.setLayout(new BorderLayout());
		m_descriptionField = new JTextField(40);
		final JPanel northPanel = new JPanel();
		northPanel.add(new JLabel(Messages.getString("TaskInfoPanel.2"))); //$NON-NLS-1$
		northPanel.add(m_descriptionField);

		temp.add(northPanel, BorderLayout.NORTH);

		final JPanel southPanel = new JPanel();
		temp.add(southPanel, BorderLayout.SOUTH);
		m_childrenAreOrderedCheckbox = new JCheckBox(Messages.getString("TaskInfoPanel.3")); //$NON-NLS-1$
		m_childrenAreOrderedCheckbox.setEnabled(false);
		southPanel.add(m_childrenAreOrderedCheckbox);

		m_currentChildTaskLabel = new JLabel(Messages.getString("TaskInfoPanel.4")); //$NON-NLS-1$
		m_currentChildTaskTextField = new JTextField(20);
		m_currentChildTaskTextField.setEditable(false);
		setChildTaskUIElementsEnabled(false);

		southPanel.add(m_currentChildTaskLabel);
		southPanel.add(m_currentChildTaskTextField);

		m_childrenAreOrderedCheckbox.addActionListener(p_arg0 ->
		{
			final boolean selected = m_childrenAreOrderedCheckbox.isSelected();
			setChildTaskUIElementsEnabled(selected);
			if (selected)
			{
				final Task currentTask = m_taskManagementPanel.getTaskTree().getCurrentTask();
				PMMainController.convertToOrderedParent(currentTask);
				fillLiveChildTaskField(currentTask);
			}
		});

		add(temp);
	}

	private void buildAndAddPriorityPanel()
	{
		final JPanel temp = new JPanel();
		m_prioritySlider = new JSlider(0, Constants.MAX_TASK_PRIORITY, Constants.DEFAULT_TASK_PRIORITY);
		m_priorityField = new JTextField(3);
		m_priorityField.setEditable(false);
		m_priorityField.setText(Messages.getString("TaskInfoPanel.5")); //$NON-NLS-1$

		m_prioritySlider.addChangeListener(arg0 ->
		{
			m_priorityField.setText(String.valueOf(m_prioritySlider.getValue()));
		});

		temp.add(new JLabel(Messages.getString("TaskInfoPanel.6"))); //$NON-NLS-1$
		temp.add(m_prioritySlider);
		temp.add(m_priorityField);
		add(temp);
	}

	private void buildAndAddRepeatPanel()
	{
		final JPanel temp = new JPanel();
		m_repeatCheckbox = new JCheckBox(Messages.getString("TaskInfoPanel.7")); //$NON-NLS-1$
		m_repeatPeriodLabel = new JLabel(Messages.getString("TaskInfoPanel.8")); //$NON-NLS-1$
		m_timerResetsLabel = new JLabel(Messages.getString("TaskInfoPanel.9")); //$NON-NLS-1$
		m_repeatPeriodField = new JTextField(3);
		m_repeatPeriodComboBox = new JComboBox<>(RepeatPeriodType.values());
		m_repeatRestartComboBox = new JComboBox<>(RepeatRestartType.values());

		temp.add(m_repeatCheckbox);
		final JPanel repeatInfoPanel = new JPanel(new GridLayout(3, 1));
		temp.add(repeatInfoPanel);

		final JPanel repeatPeriodPanel = new JPanel();
		repeatPeriodPanel.add(m_repeatPeriodLabel);
		repeatPeriodPanel.add(m_repeatPeriodField);
		repeatPeriodPanel.add(m_repeatPeriodComboBox);
		repeatInfoPanel.add(repeatPeriodPanel);

		final JPanel repeatTypePanel = new JPanel();
		repeatInfoPanel.add(repeatTypePanel);
		repeatTypePanel.add(m_timerResetsLabel);
		repeatTypePanel.add(m_repeatRestartComboBox);

		final JPanel lastCompletionDatePanel = new JPanel();
		repeatInfoPanel.add(lastCompletionDatePanel);
		m_lastCompletionDateLabel = new JLabel(Messages.getString("TaskInfoPanel.10")); //$NON-NLS-1$
		m_lastCompletionDatePicker = new JDatePicker();
		lastCompletionDatePanel.add(m_lastCompletionDateLabel);
		lastCompletionDatePanel.add(m_lastCompletionDatePicker);

		m_repeatCheckbox.addChangeListener(arg0 ->
		{
			final boolean checked = m_repeatCheckbox.isSelected();
			setRepeatItemsEnabled(checked);
		});

		add(temp);
		setRepeatItemsEnabled(false);
	}

	private void buildAndAddSavePanel()
	{
		final JPanel temp = new JPanel();
		final JButton saveButton = new JButton(Messages.getString("TaskInfoPanel.11")); //$NON-NLS-1$
		saveButton.addActionListener(p_arg0 ->
		{
			saveUIToTask(m_currentlyDisplayedTask);
		});
		temp.add(saveButton);

		final JButton markCompletedButton = new JButton(Messages.getString("TaskInfoPanel.12")); //$NON-NLS-1$
		markCompletedButton.addActionListener(p_arg0 ->
		{
			PMMainController.markSelectedTaskAsComplete();
		});
		temp.add(markCompletedButton);

		add(temp);
	}

	private void fillLiveChildTaskField(final Task currentTask)
	{
		final Task liveChildTask = m_taskManagementPanel.getMainModel().getTasks().get(currentTask.getLiveChildID());
		if (liveChildTask != null)
		{
			m_currentChildTaskTextField.setText(liveChildTask.getDescription());
		}
		else
		{
			m_currentChildTaskTextField.setText(Messages.getString("TaskInfoPanel.13")); //$NON-NLS-1$
		}
	}

	private void setChildTaskUIElementsEnabled(final boolean p_selected)
	{
		m_currentChildTaskLabel.setEnabled(p_selected);
		m_currentChildTaskTextField.setEnabled(p_selected);
		if (p_selected == false)
		{
			m_currentChildTaskTextField.setText(Messages.getString("TaskInfoPanel.18")); //$NON-NLS-1$
		}
	}

	private void setRepeatItemsEnabled(final boolean p_enabled)
	{
		m_repeatPeriodLabel.setEnabled(p_enabled);
		m_repeatPeriodField.setEnabled(p_enabled);
		m_repeatPeriodComboBox.setEnabled(p_enabled);
		m_repeatRestartComboBox.setEnabled(p_enabled);
		m_timerResetsLabel.setEnabled(p_enabled);
		m_lastCompletionDateLabel.setEnabled(p_enabled);
		m_lastCompletionDatePicker.setEnabled(p_enabled);
	}
}
