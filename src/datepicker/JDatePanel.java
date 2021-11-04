/**
 * Copyright 2004 Juan Heyns. All rights reserved. Redistribution and use in
 * source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met: 1. Redistributions of source code must
 * retain the above copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution. THIS
 * SOFTWARE IS PROVIDED BY JUAN HEYNS ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL JUAN HEYNS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. The views and conclusions
 * contained in the software and documentation are those of the authors and
 * should not be interpreted as representing official policies, either expressed
 * or implied, of Juan Heyns.
 */
package datepicker;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import datepicker.constraints.DateSelectionConstraint;

/**
 * Created on 26 Mar 2004 Refactored on 21 Jun 2004 Refactored on 8 Jul 2004
 * Refactored 14 May 2009 Refactored 16 April 2010 Updated 18 April 2010 Updated
 * 26 April 2010 Updated 15 June 2012 Updated 10 August 2012 Updated 6 Jun 2015
 *
 * @author Juan Heyns
 * @author JC Oosthuizen
 * @author Yue Huang
 */
public class JDatePanel extends JComponent implements DatePanel
{

	/**
	 * This model represents the selected date. The model implements the
	 * TableModel interface for displaying days, and it implements the
	 * SpinnerModel for the year.
	 *
	 * @author Juan Heyns
	 */
	protected class InternalCalendarModel implements TableModel, SpinnerModel, ChangeListener
	{

		private final DateModel<?>				model;
		private final Set<ChangeListener>		spinnerChangeListeners;
		private final Set<TableModelListener>	tableModelListeners;

		private int[] lookup = null;

		public InternalCalendarModel(final DateModel<?> model)
		{
			spinnerChangeListeners = new HashSet<>();
			tableModelListeners = new HashSet<>();
			this.model = model;
			model.addChangeListener(this);
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public void addChangeListener(final ChangeListener arg0)
		{
			spinnerChangeListeners.add(arg0);
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public void addTableModelListener(final TableModelListener arg0)
		{
			tableModelListeners.add(arg0);
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public Class getColumnClass(final int arg0)
		{
			return Integer.class;
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public int getColumnCount()
		{
			return 7;
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public String getColumnName(final int columnIndex)
		{
			final int key = (firstDayOfWeek - 1 + columnIndex) % 7;
			return DAYS[key];
		}

		public DateModel<?> getModel()
		{
			return model;
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public Object getNextValue()
		{
			return Integer.toString(model.getYear() + 1);
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public Object getPreviousValue()
		{
			return Integer.toString(model.getYear() - 1);
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public int getRowCount()
		{
			return 6;
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public Object getValue()
		{
			return Integer.toString(model.getYear());
		}

		/**
		 * Part of TableModel, day previous month (... -1, 0) -> current month
		 * (1...DAYS_IN_MONTH) -> next month (DAYS_IN_MONTH + 1, DAYS_IN_MONTH +
		 * 2, ...)
		 */
		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex)
		{
			final int series = columnIndex + rowIndex * 7 + 1;

			final Calendar firstOfMonth = Calendar.getInstance();
			firstOfMonth.set(model.getYear(), model.getMonthButWrong(), 1);
			final int dowForFirst = firstOfMonth.get(Calendar.DAY_OF_WEEK);
			final int daysBefore = lookup()[dowForFirst - 1];

			return series - daysBefore;
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public boolean isCellEditable(final int arg0, final int arg1)
		{
			return false;
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public void removeChangeListener(final ChangeListener arg0)
		{
			spinnerChangeListeners.remove(arg0);
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public void removeTableModelListener(final TableModelListener arg0)
		{
			tableModelListeners.remove(arg0);
		}

		/**
		 * Part of SpinnerModel, year
		 */
		@Override
		public void setValue(final Object text)
		{
			final String year = (String) text;
			model.setYear(Integer.parseInt(year));
		}

		/**
		 * Part of TableModel, day
		 */
		@Override
		public void setValueAt(final Object arg0, final int arg1, final int arg2)
		{
			// should be blank
		}

		/**
		 * The model has changed and needs to notify the InternalModel.
		 */
		@Override
		public void stateChanged(final ChangeEvent e)
		{
			fireValueChanged();
		}

		/**
		 * Called whenever a change is made to the model value. Notify the
		 * internal listeners and update the simple controls. Also notifies the
		 * (external) ChangeListeners of the component, since the internal state
		 * has changed.
		 */
		private void fireValueChanged()
		{
			// Update year spinner
			for (final ChangeListener cl : spinnerChangeListeners)
			{
				cl.stateChanged(new ChangeEvent(this));
			}

			// Update month label
			internalView.updateMonthLabel();

			// Update day table
			for (final TableModelListener tl : tableModelListeners)
			{
				tl.tableChanged(new TableModelEvent(this));
			}
		}

		/**
		 * Results in a mapping which calculates the number of days before the
		 * first day of month DAY OF WEEK M T W T F S S 1 2 3 4 5 6 0 or S M T W
		 * T F S 0 1 2 3 4 5 6 DAYS BEFORE 0 1 2 3 4 5 6
		 *
		 * @return
		 */
		private int[] lookup()
		{
			if (lookup == null)
			{
				lookup = new int[8];
				lookup[(firstDayOfWeek - 1) % 7] = 0;
				lookup[(firstDayOfWeek + 0) % 7] = 1;
				lookup[(firstDayOfWeek + 1) % 7] = 2;
				lookup[(firstDayOfWeek + 2) % 7] = 3;
				lookup[(firstDayOfWeek + 3) % 7] = 4;
				lookup[(firstDayOfWeek + 4) % 7] = 5;
				lookup[(firstDayOfWeek + 5) % 7] = 6;
			}
			return lookup;
		}

	}

	/**
	 * This inner class hides the public view event handling methods from the
	 * outside. This class acts as an internal controller for this component. It
	 * receives events from the view components and updates the model.
	 *
	 * @author Juan Heyns
	 */
	private class InternalController implements ActionListener, MouseListener
	{

		/**
		 *
		 */
		public InternalController()
		{
			// should be blank
		}

		/**
		 * Next, Previous and Month buttons clicked, causes the model to be
		 * updated.
		 */
		@Override
		public void actionPerformed(final ActionEvent arg0)
		{
			if (!isEnabled())
			{
				return;
			}

			if (arg0.getSource() == internalView.getNextMonthButton())
			{
				internalModel.getModel().addMonth(1);
			}
			else
				if (arg0.getSource() == internalView.getPreviousMonthButton())
				{
					internalModel.getModel().addMonth(-1);
				}
				else
					if (arg0.getSource() == internalView.getNextYearButton())
					{
						internalModel.getModel().addYear(1);
					}
					else
						if (arg0.getSource() == internalView.getPreviousYearButton())
						{
							internalModel.getModel().addYear(-1);
						}
						else
						{
							for (int month = 0; month < internalView.getMonthPopupMenuItems().length; month++)
							{
								if (arg0.getSource() == internalView.getMonthPopupMenuItems()[month])
								{
									internalModel.getModel().setMonthButWrong(month);
								}
							}
						}
		}

		@Override
		public void mouseClicked(final MouseEvent arg0)
		{
			// should be blank
		}

		@Override
		public void mouseEntered(final MouseEvent arg0)
		{
			// should be blank
		}

		@Override
		public void mouseExited(final MouseEvent arg0)
		{
			// should be blank
		}

		/**
		 * Mouse down on monthLabel pops up a table. Mouse down on todayLabel
		 * sets the value of the internal model to today. Mouse down on day
		 * table will set the day to the value. Mouse down on none label will
		 * clear the date.
		 */
		@Override
		public void mousePressed(final MouseEvent arg0)
		{
			if (!isEnabled())
			{
				return;
			}

			if (arg0.getSource() == internalView.getMonthLabel())
			{
				internalView.getMonthPopupMenu().setLightWeightPopupEnabled(false);
				internalView.getMonthPopupMenu().show((Component) arg0.getSource(), arg0.getX(), arg0.getY());
			}
			else
				if (arg0.getSource() == internalView.getTodayLabel())
				{
					final Calendar today = Calendar.getInstance();
					internalModel
						.getModel()
						.setDateButWrong(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
				}
				else
					if (arg0.getSource() == internalView.getDayTable())
					{
						final int row = internalView.getDayTable().getSelectedRow();
						final int col = internalView.getDayTable().getSelectedColumn();
						if (row >= 0 && row <= 5)
						{
							final Integer date = (Integer) internalModel.getValueAt(row, col);

							// check constraints
							final int oldDay = internalModel.getModel().getDay();
							internalModel.getModel().setDay(date);
							if (!checkConstraints(internalModel.getModel()))
							{
								// rollback
								internalModel.getModel().setDay(oldDay);
								return;
							}

							internalModel.getModel().setSelected(true);

							if (doubleClickAction && arg0.getClickCount() == 2)
							{
								fireActionPerformed();
							}
							if (!doubleClickAction)
							{
								fireActionPerformed();
							}
						}
					}
					else
						if (arg0.getSource() == internalView.getNoneLabel())
						{
							internalModel.getModel().setSelected(false);

							if (doubleClickAction && arg0.getClickCount() == 2)
							{
								fireActionPerformed();
							}
							if (!doubleClickAction)
							{
								fireActionPerformed();
							}
						}
		}

		@Override
		public void mouseReleased(final MouseEvent arg0)
		{
			// should be blank
		}

	}

	/**
	 * This inner class renders the table of the days, setting colors based on
	 * whether it is in the month, if it is today, if it is selected etc.
	 *
	 * @author Juan Heyns
	 */
	private class InternalTableCellRenderer extends DefaultTableCellRenderer
	{

		private static final long serialVersionUID = -2341614459632756921L;

		/**
		 *
		 */
		public InternalTableCellRenderer()
		{
			// should be blank
		}

		@Override
		public Component getTableCellRendererComponent(
			final JTable table,
			final Object value,
			final boolean isSelected,
			final boolean hasFocus,
			final int row,
			final int column)
		{
			// Exit this method if the value is null, encountered from
			// JTable#AccessibleJTable
			if (value == null)
			{
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}

			final JLabel label =
				(JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setHorizontalAlignment(SwingConstants.CENTER);

			if (row == -1)
			{
				label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_HEADER));
				label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_HEADER));
				label.setHorizontalAlignment(SwingConstants.CENTER);
				return label;
			}

			final Calendar todayCal = Calendar.getInstance();
			final Calendar selectedCal = Calendar.getInstance();
			selectedCal
				.set(
					internalModel.getModel().getYear(),
					internalModel.getModel().getMonthButWrong(),
					internalModel.getModel().getDay());

			final int cellDayValue = (Integer) value;
			final int lastDayOfMonth = selectedCal.getActualMaximum(Calendar.DAY_OF_MONTH);

			// Other month
			if (cellDayValue < 1 || cellDayValue > lastDayOfMonth)
			{
				label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_OTHER_MONTH));

				final Calendar calForDay = Calendar.getInstance();
				calForDay
					.set(internalModel.getModel().getYear(), internalModel.getModel().getMonthButWrong(), cellDayValue);
				final DateModel<Calendar> modelForDay = new UtilCalendarModel(calForDay);
				label
					.setBackground(
						checkConstraints(modelForDay)
							? getColors().getColor(ComponentColorDefaults.Key.BG_GRID)
							: getColors().getColor(ComponentColorDefaults.Key.BG_GRID_NOT_SELECTABLE));

				// Past end of month
				if (cellDayValue > lastDayOfMonth)
				{
					label.setText(Integer.toString(cellDayValue - lastDayOfMonth));
				}
				// Before start of month
				else
				{
					final Calendar lastMonth = new GregorianCalendar();
					lastMonth.set(selectedCal.get(Calendar.YEAR), selectedCal.get(Calendar.MONTH) - 1, 1);
					final int lastDayLastMonth = lastMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
					label.setText(Integer.toString(lastDayLastMonth + cellDayValue));
				}
			}
			// This month
			else
			{
				label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_THIS_MONTH));

				final Calendar calForDay = Calendar.getInstance();
				calForDay
					.set(internalModel.getModel().getYear(), internalModel.getModel().getMonthButWrong(), cellDayValue);
				final DateModel<Calendar> modelForDay = new UtilCalendarModel(calForDay);
				label
					.setBackground(
						checkConstraints(modelForDay)
							? getColors().getColor(ComponentColorDefaults.Key.BG_GRID)
							: getColors().getColor(ComponentColorDefaults.Key.BG_GRID_NOT_SELECTABLE));

				// Today
				if (todayCal.get(Calendar.DATE) == cellDayValue
					&& todayCal.get(Calendar.MONTH) == internalModel.getModel().getMonthButWrong()
					&& todayCal.get(Calendar.YEAR) == internalModel.getModel().getYear())
				{
					label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_TODAY));
					// Selected
					if (internalModel.getModel().isSelected() && selectedCal.get(Calendar.DATE) == cellDayValue)
					{
						label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_TODAY_SELECTED));
						label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_TODAY_SELECTED));
					}
				}
				// Other day
				else
				{
					// Selected
					if (internalModel.getModel().isSelected() && selectedCal.get(Calendar.DATE) == cellDayValue)
					{
						label.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_GRID_SELECTED));
						label.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_GRID_SELECTED));
					}
				}
			}

			return label;
		}

	}

	/**
	 * Logically grouping the view controls under this internal class.
	 *
	 * @author Juan Heyns
	 */
	private class InternalView extends JPanel
	{

		private static final long serialVersionUID = -6844493839307157682L;

		private JPanel						centerPanel;
		private JPanel						northCenterPanel;
		private JPanel						northPanel;
		private JPanel						southPanel;
		private JPanel						previousButtonPanel;
		private JPanel						nextButtonPanel;
		JTable								dayTable;
		private JTableHeader				dayTableHeader;
		private InternalTableCellRenderer	dayTableCellRenderer;
		private JLabel						monthLabel;
		private JLabel						todayLabel;
		private JLabel						noneLabel;
		private JPopupMenu					monthPopupMenu;
		private JMenuItem[]					monthPopupMenuItems;
		private JButton						nextMonthButton;
		private JButton						previousMonthButton;
		private JButton						previousYearButton;
		private JButton						nextYearButton;
		private JSpinner					yearSpinner;

		public InternalView()
		{
			initialise();
		}

		@Override
		public void setEnabled(final boolean enabled)
		{
			dayTable.setEnabled(enabled);
			dayTableCellRenderer.setEnabled(enabled);
			nextMonthButton.setEnabled(enabled);
			if (nextYearButton != null)
			{
				nextYearButton.setEnabled(enabled);
			}
			previousMonthButton.setEnabled(enabled);
			if (previousYearButton != null)
			{
				previousYearButton.setEnabled(enabled);
			}
			yearSpinner.setEnabled(enabled);
			if (enabled)
			{
				todayLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED));
			}
			else
			{
				todayLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_DISABLED));
			}

			super.setEnabled(enabled);
		}

		/**
		 * This method initializes dayTable
		 *
		 * @return javax.swing.JTable
		 */
		JTable getDayTable()
		{
			if (dayTable == null)
			{
				dayTable = new javax.swing.JTable();
				dayTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
				dayTable.setModel(internalModel);
				dayTable.setShowGrid(true);
				dayTable.setGridColor(getColors().getColor(ComponentColorDefaults.Key.BG_GRID));
				dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				dayTable.setCellSelectionEnabled(true);
				dayTable.setRowSelectionAllowed(true);
				dayTable.setFocusable(false);
				dayTable.addMouseListener(internalController);
				for (int i = 0; i < 7; i++)
				{
					final TableColumn column = dayTable.getColumnModel().getColumn(i);
					column.setCellRenderer(getDayTableCellRenderer());
				}
				dayTable.addComponentListener(new ComponentListener()
				{

					@Override
					public void componentHidden(final ComponentEvent e)
					{
						// Do nothing
					}

					@Override
					public void componentMoved(final ComponentEvent e)
					{
						// Do nothing
					}

					@Override
					public void componentResized(final ComponentEvent e)
					{
						// The new size of the table
						final double w = e.getComponent().getSize().getWidth();
						final double h = e.getComponent().getSize().getHeight();

						// Set the size of the font as a fraction of the width
						// or the height, whichever
						// is smallest
						final float sw = (float) Math.floor(w / 16);
						final float sh = (float) Math.floor(h / 8);
						dayTable.setFont(dayTable.getFont().deriveFont(Math.min(sw, sh)));

						// Set the row height as a fraction of the height
						final int r = (int) Math.floor(h / 6);
						dayTable.setRowHeight(r);
					}

					@Override
					public void componentShown(final ComponentEvent e)
					{
						// Do nothing
					}

				});
			}
			return dayTable;
		}

		/**
		 * This method initializes monthLabel
		 *
		 * @return javax.swing.JLabel
		 */
		JLabel getMonthLabel()
		{
			if (monthLabel == null)
			{
				monthLabel = new javax.swing.JLabel();
				monthLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_MONTH_SELECTOR));
				monthLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				monthLabel.addMouseListener(internalController);
				updateMonthLabel();
			}
			return monthLabel;
		}

		/**
		 * This method initializes monthPopupMenu
		 *
		 * @return javax.swing.JPopupMenu
		 */
		JPopupMenu getMonthPopupMenu()
		{
			if (monthPopupMenu == null)
			{
				monthPopupMenu = new javax.swing.JPopupMenu();
				final JMenuItem[] menuItems = getMonthPopupMenuItems();
				for (final JMenuItem menuItem : menuItems)
				{
					monthPopupMenu.add(menuItem);
				}
			}
			return monthPopupMenu;
		}

		JMenuItem[] getMonthPopupMenuItems()
		{
			if (monthPopupMenuItems == null)
			{
				monthPopupMenuItems = new JMenuItem[12];
				for (int i = 0; i < 12; i++)
				{
					final JMenuItem mi = new JMenuItem(MONTHS[i]);
					mi.addActionListener(internalController);
					monthPopupMenuItems[i] = mi;
				}
			}
			return monthPopupMenuItems;
		}

		/**
		 * This method initializes nextMonthButton
		 *
		 * @return javax.swing.JButton
		 */
		JButton getNextMonthButton()
		{
			if (nextMonthButton == null)
			{
				nextMonthButton = new JButton();
				nextMonthButton.setIcon(getIcons().getNextMonthIconEnabled());
				nextMonthButton.setDisabledIcon(getIcons().getNextMonthIconDisabled());
				nextMonthButton.setText(""); //$NON-NLS-1$
				nextMonthButton.setPreferredSize(new java.awt.Dimension(20, 15));
				nextMonthButton
					.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				nextMonthButton.setFocusable(false);
				nextMonthButton.setOpaque(true);
				nextMonthButton.addActionListener(internalController);
				nextMonthButton.setToolTipText(Messages.getString("JDatePanel.0")); //$NON-NLS-1$
			}
			return nextMonthButton;
		}

		/**
		 * This method initializes nextYearButton
		 *
		 * @return javax.swing.JButton
		 */
		JButton getNextYearButton()
		{
			if (nextYearButton == null)
			{
				nextYearButton = new JButton();
				nextYearButton.setIcon(getIcons().getNextYearIconEnabled());
				nextYearButton.setDisabledIcon(getIcons().getNextYearIconDisabled());
				nextYearButton.setText(Messages.getString("JDatePanel.1")); //$NON-NLS-1$
				nextYearButton.setPreferredSize(new java.awt.Dimension(20, 15));
				nextYearButton
					.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				nextYearButton.setFocusable(false);
				nextYearButton.setOpaque(true);
				nextYearButton.addActionListener(internalController);
				nextYearButton.setToolTipText(Messages.getString("JDatePanel.2")); //$NON-NLS-1$
			}
			return nextYearButton;
		}

		/**
		 * This method initializes todayLabel
		 *
		 * @return javax.swing.JLabel
		 */
		JLabel getNoneLabel()
		{
			if (noneLabel == null)
			{
				noneLabel = new javax.swing.JLabel();
				noneLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED));
				noneLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				noneLabel.addMouseListener(internalController);
				// TODO get the translations for each language before adding
				// this in
				// noneLabel.setToolTipText(getText(ComponentTextDefaults.CLEAR));
			}
			return noneLabel;
		}

		/**
		 * This method initializes previousMonthButton
		 *
		 * @return javax.swing.JButton
		 */
		JButton getPreviousMonthButton()
		{
			if (previousMonthButton == null)
			{
				previousMonthButton = new JButton();
				previousMonthButton.setIcon(getIcons().getPreviousMonthIconEnabled());
				previousMonthButton.setDisabledIcon(getIcons().getPreviousMonthIconDisabled());
				previousMonthButton.setText(Messages.getString("JDatePanel.5")); //$NON-NLS-1$
				previousMonthButton.setPreferredSize(new java.awt.Dimension(20, 15));
				previousMonthButton
					.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				previousMonthButton.setFocusable(false);
				previousMonthButton.setOpaque(true);
				previousMonthButton.addActionListener(internalController);
				previousMonthButton.setToolTipText(Messages.getString("JDatePanel.6")); //$NON-NLS-1$
			}
			return previousMonthButton;
		}

		/**
		 * This method initializes previousMonthButton
		 *
		 * @return javax.swing.JButton
		 */
		JButton getPreviousYearButton()
		{
			if (previousYearButton == null)
			{
				previousYearButton = new JButton();
				previousYearButton.setIcon(getIcons().getPreviousYearIconEnabled());
				previousYearButton.setDisabledIcon(getIcons().getPreviousYearIconDisabled());
				previousYearButton.setText(Messages.getString("JDatePanel.7")); //$NON-NLS-1$
				previousYearButton.setPreferredSize(new java.awt.Dimension(20, 15));
				previousYearButton
					.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
				previousYearButton.setFocusable(false);
				previousYearButton.setOpaque(true);
				previousYearButton.addActionListener(internalController);
				previousYearButton.setToolTipText(Messages.getString("JDatePanel.8")); //$NON-NLS-1$
			}
			return previousYearButton;
		}

		/**
		 * This method initializes todayLabel
		 *
		 * @return javax.swing.JLabel
		 */
		JLabel getTodayLabel()
		{
			if (todayLabel == null)
			{
				todayLabel = new javax.swing.JLabel();
				todayLabel.setForeground(getColors().getColor(ComponentColorDefaults.Key.FG_TODAY_SELECTOR_ENABLED));
				todayLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				todayLabel.addMouseListener(internalController);
				updateTodayLabel();
			}
			return todayLabel;
		}

		/**
		 * Update the UI of the monthLabel
		 */
		void updateMonthLabel()
		{
			monthLabel.setText(MONTHS[internalModel.getModel().getMonthButWrong()]);
		}

		/**
		 * Update the scroll buttons UI.
		 */
		void updateShowYearButtons()
		{
			if (showYearButtons)
			{
				getNextButtonPanel().add(getNextYearButton());
				getPreviousButtonPanel().removeAll();
				getPreviousButtonPanel().add(getPreviousYearButton());
				getPreviousButtonPanel().add(getPreviousMonthButton());
			}
			else
			{
				getNextButtonPanel().remove(getNextYearButton());
				getPreviousButtonPanel().remove(getPreviousYearButton());
			}
		}

		void updateTodayLabel()
		{
			final Calendar now = Calendar.getInstance();
			final DateFormat df = getFormats().getFormat(ComponentFormatDefaults.Key.TODAY_SELECTOR);
			todayLabel.setText(Messages.getString("JDatePanel.9") + df.format(now.getTime())); //$NON-NLS-1$
		}

		/**
		 * This method initializes centerPanel
		 *
		 * @return javax.swing.JPanel
		 */
		private JPanel getCenterPanel()
		{
			if (centerPanel == null)
			{
				centerPanel = new javax.swing.JPanel();
				centerPanel.setLayout(new java.awt.BorderLayout());
				centerPanel.setOpaque(false);
				centerPanel.add(getDayTableHeader(), java.awt.BorderLayout.NORTH);
				centerPanel.add(getDayTable(), java.awt.BorderLayout.CENTER);
			}
			return centerPanel;
		}

		private InternalTableCellRenderer getDayTableCellRenderer()
		{
			if (dayTableCellRenderer == null)
			{
				dayTableCellRenderer = new InternalTableCellRenderer();
			}
			return dayTableCellRenderer;
		}

		private JTableHeader getDayTableHeader()
		{
			if (dayTableHeader == null)
			{
				dayTableHeader = getDayTable().getTableHeader();
				dayTableHeader.setResizingAllowed(false);
				dayTableHeader.setReorderingAllowed(false);
				dayTableHeader.setDefaultRenderer(getDayTableCellRenderer());
			}
			return dayTableHeader;
		}

		/**
		 * This method initializes nextButtonPanel
		 *
		 * @return javax.swing.JPanel
		 */
		private JPanel getNextButtonPanel()
		{
			if (nextButtonPanel == null)
			{
				nextButtonPanel = new javax.swing.JPanel();
				final java.awt.GridLayout layout = new java.awt.GridLayout(1, 2);
				layout.setHgap(3);
				nextButtonPanel.setLayout(layout);
				nextButtonPanel.setName(""); //$NON-NLS-1$
				nextButtonPanel.setOpaque(false);
				nextButtonPanel.add(getNextMonthButton());
				if (isShowYearButtons())
				{
					nextButtonPanel.add(getNextYearButton());
				}
			}
			return nextButtonPanel;
		}

		/**
		 * This method initializes northCenterPanel
		 *
		 * @return javax.swing.JPanel
		 */
		private JPanel getNorthCenterPanel()
		{
			if (northCenterPanel == null)
			{
				northCenterPanel = new javax.swing.JPanel();
				northCenterPanel.setLayout(new java.awt.BorderLayout());
				northCenterPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5));
				northCenterPanel.setOpaque(false);
				northCenterPanel.add(getMonthLabel(), java.awt.BorderLayout.CENTER);
				northCenterPanel.add(getYearSpinner(), java.awt.BorderLayout.EAST);
			}
			return northCenterPanel;
		}

		/**
		 * This method initializes northPanel
		 *
		 * @return javax.swing.JPanel The north panel
		 */
		private JPanel getNorthPanel()
		{
			if (northPanel == null)
			{
				northPanel = new javax.swing.JPanel();
				northPanel.setLayout(new java.awt.BorderLayout());
				northPanel.setName(Messages.getString("JDatePanel.3")); //$NON-NLS-1$
				northPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
				northPanel.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_MONTH_SELECTOR));
				northPanel.add(getPreviousButtonPanel(), java.awt.BorderLayout.WEST);
				northPanel.add(getNextButtonPanel(), java.awt.BorderLayout.EAST);
				northPanel.add(getNorthCenterPanel(), java.awt.BorderLayout.CENTER);
			}
			return northPanel;
		}

		/**
		 * This method initializes previousButtonPanel
		 *
		 * @return javax.swing.JPanel
		 */
		private JPanel getPreviousButtonPanel()
		{
			if (previousButtonPanel == null)
			{
				previousButtonPanel = new javax.swing.JPanel();
				final java.awt.GridLayout layout = new java.awt.GridLayout(1, 2);
				layout.setHgap(3);
				previousButtonPanel.setLayout(layout);
				previousButtonPanel.setName(Messages.getString("JDatePanel.4")); //$NON-NLS-1$
				previousButtonPanel.setOpaque(false);
				if (isShowYearButtons())
				{
					previousButtonPanel.add(getPreviousYearButton());
				}
				previousButtonPanel.add(getPreviousMonthButton());
			}
			return previousButtonPanel;
		}

		/**
		 * This method initializes southPanel
		 *
		 * @return javax.swing.JPanel
		 */
		private JPanel getSouthPanel()
		{
			if (southPanel == null)
			{
				southPanel = new javax.swing.JPanel();
				southPanel.setLayout(new java.awt.BorderLayout());
				southPanel.setBackground(getColors().getColor(ComponentColorDefaults.Key.BG_TODAY_SELECTOR));
				southPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
				southPanel.add(getTodayLabel(), java.awt.BorderLayout.WEST);
				southPanel.add(getNoneLabel(), java.awt.BorderLayout.EAST);
			}
			return southPanel;
		}

		/**
		 * This method initializes yearSpinner
		 *
		 * @return javax.swing.JSpinner
		 */
		private JSpinner getYearSpinner()
		{
			if (yearSpinner == null)
			{
				yearSpinner = new javax.swing.JSpinner();
				yearSpinner.setModel(internalModel);
			}
			return yearSpinner;
		}

		/**
		 * Initialise the control.
		 */
		private void initialise()
		{
			setLayout(new java.awt.BorderLayout());
			this.setSize(200, 180);
			setPreferredSize(new java.awt.Dimension(200, 180));
			setOpaque(false);
			this.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
			this.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
			this.add(getCenterPanel(), java.awt.BorderLayout.CENTER);
		}
	}

	static final String[]	MONTHS	= {
		Messages.getString("JDatePanel.10"),                                                                          //$NON-NLS-1$
		Messages.getString("JDatePanel.11"), //$NON-NLS-1$
		Messages.getString("JDatePanel.12"), //$NON-NLS-1$
		Messages.getString("JDatePanel.13"),                                            //$NON-NLS-1$
		Messages.getString("JDatePanel.14"), //$NON-NLS-1$
		Messages.getString("JDatePanel.15"), //$NON-NLS-1$
		Messages.getString("JDatePanel.16"),                                            //$NON-NLS-1$
		Messages.getString("JDatePanel.17"), //$NON-NLS-1$
		Messages.getString("JDatePanel.18"), //$NON-NLS-1$
		Messages.getString("JDatePanel.19"),                                            //$NON-NLS-1$
		Messages.getString("JDatePanel.20"), //$NON-NLS-1$
		Messages.getString("JDatePanel.21")};                                                                               //$NON-NLS-1$
	static final String[]	DAYS	= {
		Messages.getString("JDatePanel.22"),                                                                          //$NON-NLS-1$
		Messages.getString("JDatePanel.23"), //$NON-NLS-1$
		Messages.getString("JDatePanel.24"), //$NON-NLS-1$
		Messages.getString("JDatePanel.25"),                                            //$NON-NLS-1$
		Messages.getString("JDatePanel.26"), //$NON-NLS-1$
		Messages.getString("JDatePanel.27"), //$NON-NLS-1$
		Messages.getString("JDatePanel.28")};                                           //$NON-NLS-1$

	private static final long serialVersionUID = -2299249311312882915L;

	public static DateModel<Calendar> createModel()
	{
		return new UtilCalendarModel();
	}

	static ComponentColorDefaults getColors()
	{
		return ComponentColorDefaults.getInstance();
	}

	static ComponentFormatDefaults getFormats()
	{
		return ComponentFormatDefaults.getInstance();
	}

	static ComponentIconDefaults getIcons()
	{
		return ComponentIconDefaults.getInstance();
	}

	@SuppressWarnings("unused")
	private static DateModel<Calendar> createModel(final Calendar value)
	{
		return new UtilCalendarModel(value);
	}

	private static DateModel<?> createModelFromValue(final Object value)
	{
		if (value instanceof java.util.Calendar)
		{
			return new UtilCalendarModel((Calendar) value);
		}
		if (value instanceof java.util.Date)
		{
			return new UtilDateModel((java.util.Date) value);
		}
		if (value instanceof java.sql.Date)
		{
			return new SqlDateModel((java.sql.Date) value);
		}
		throw new IllegalArgumentException(Messages.getString("JDatePanel.29")); //$NON-NLS-1$
	}

	private final Set<ActionListener> actionListeners;

	private final Set<DateSelectionConstraint> dateConstraints;

	boolean showYearButtons;

	boolean doubleClickAction;

	final int firstDayOfWeek;

	final InternalCalendarModel internalModel;

	final InternalController internalController;

	final InternalView internalView;

	/**
	 * Creates a JDatePanel with a default calendar model.
	 */
	public JDatePanel()
	{
		this(createModel());
	}

	/**
	 * Create a JDatePanel with an initial value, with a UtilCalendarModel.
	 *
	 * @param value the initial value
	 */
	public JDatePanel(final Calendar value)
	{
		this(createModelFromValue(value));
	}

	/**
	 * Create a JDatePanel with a custom date model.
	 *
	 * @param model a custom date model
	 */
	public JDatePanel(final DateModel<?> model)
	{
		actionListeners = new HashSet<>();
		dateConstraints = new HashSet<>();

		showYearButtons = false;
		doubleClickAction = false;
		firstDayOfWeek = Calendar.getInstance().getFirstDayOfWeek();

		internalModel = new InternalCalendarModel(model);
		internalController = new InternalController();
		internalView = new InternalView();

		setLayout(new GridLayout(1, 1));
		add(internalView);
	}

	/**
	 * Create a JDatePanel with an initial value, with a SqlDateModel.
	 *
	 * @param value the initial value
	 */
	public JDatePanel(final java.sql.Date value)
	{
		this(createModelFromValue(value));
	}

	/**
	 * Create a JDatePanel with an initial value, with a UtilDateModel.
	 *
	 * @param value the initial value
	 */
	public JDatePanel(final java.util.Date value)
	{
		this(createModelFromValue(value));
	}

	@Override
	public void addActionListener(final ActionListener actionListener)
	{
		actionListeners.add(actionListener);
	}

	@Override
	public void addDateSelectionConstraint(final DateSelectionConstraint constraint)
	{
		dateConstraints.add(constraint);
	}

	@Override
	public Set<DateSelectionConstraint> getDateSelectionConstraints()
	{
		return Collections.unmodifiableSet(dateConstraints);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDateComponent#getModel()
	 */
	@Override
	public DateModel<?> getModel()
	{
		return internalModel.getModel();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePanel#isDoubleClickAction()
	 */
	@Override
	public boolean isDoubleClickAction()
	{
		return doubleClickAction;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePanel#isShowYearButtons()
	 */
	@Override
	public boolean isShowYearButtons()
	{
		return showYearButtons;
	}

	@Override
	public void removeActionListener(final ActionListener actionListener)
	{
		actionListeners.remove(actionListener);
	}

	@Override
	public void removeAllDateSelectionConstraints()
	{
		dateConstraints.clear();
	}

	@Override
	public void removeDateSelectionConstraint(final DateSelectionConstraint constraint)
	{
		dateConstraints.remove(constraint);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePanel#setDoubleClickAction(boolean)
	 */
	@Override
	public void setDoubleClickAction(final boolean doubleClickAction)
	{
		this.doubleClickAction = doubleClickAction;
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		internalView.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePanel#setShowYearButtons(boolean)
	 */
	@Override
	public void setShowYearButtons(final boolean showYearButtons)
	{
		this.showYearButtons = showYearButtons;
		internalView.updateShowYearButtons();
	}

	@Override
	public void setVisible(final boolean aFlag)
	{
		super.setVisible(aFlag);

		if (aFlag)
		{
			internalView.updateTodayLabel();
		}
	}

	/**
	 * Called internally when actionListeners should be notified.
	 */
	void fireActionPerformed()
	{
		for (final ActionListener actionListener : actionListeners)
		{
			actionListener
				.actionPerformed(
					new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Messages.getString("JDatePanel.30"))); //$NON-NLS-1$
		}
	}

	protected boolean checkConstraints(final DateModel<?> model)
	{
		for (final DateSelectionConstraint constraint : dateConstraints)
		{
			if (!constraint.isValidSelection(model))
			{
				return false;
			}
		}
		return true;
	}

}