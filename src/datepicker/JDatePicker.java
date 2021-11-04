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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import datepicker.constraints.DateSelectionConstraint;

/**
 * Created on 25 Mar 2004 Refactored 21 Jun 2004 Refactored 14 May 2009
 * Refactored 16 April 2010 Updated 26 April 2010 Updated 10 August 2012 Updated
 * 6 Jun 2015
 *
 * @author Juan Heyns
 * @author JC Oosthuizen
 * @author Yue Huang
 */
public class JDatePicker extends JComponent implements DatePicker
{

	/**
	 * This internal class hides the public event methods from the outside
	 */
	private class InternalEventHandler
		implements
		ActionListener,
		HierarchyBoundsListener,
		ChangeListener,
		PropertyChangeListener,
		AWTEventListener
	{

		/**
		 *
		 */
		public InternalEventHandler()
		{
			// should be blank
		}

		@Override
		public void actionPerformed(final ActionEvent arg0)
		{
			if (arg0.getSource() == m_button)
			{
				if (m_popup == null)
				{
					showPopup();
				}
				else
				{
					hidePopup();
				}
			}
			else
				if (arg0.getSource() == m_datePanel)
				{
					hidePopup();
				}
		}

		@Override
		public void ancestorMoved(final HierarchyEvent arg0)
		{
			hidePopup();
		}

		@Override
		public void ancestorResized(final HierarchyEvent arg0)
		{
			hidePopup();
		}

		@Override
		public void eventDispatched(final AWTEvent event)
		{
			if (MouseEvent.MOUSE_CLICKED == event.getID() && event.getSource() != m_button)
			{
				final Set<Component> components = getAllComponents(m_datePanel);
				boolean clickInPopup = false;
				for (final Component component : components)
				{
					if (event.getSource() == component)
					{
						clickInPopup = true;
					}
				}
				if (!clickInPopup)
				{
					hidePopup();
				}
			}
		}

		@Override
		public void propertyChange(final PropertyChangeEvent evt)
		{
			// Short circuit if the following cases are found
			if (evt.getOldValue() == null && evt.getNewValue() == null)
			{
				return;
			}
			if (evt.getOldValue() != null && evt.getOldValue().equals(evt.getNewValue()))
			{
				return;
			}
			if (!m_formattedTextField.isEditable())
			{
				return;
			}

			// If the field is editable and we need to parse the date entered
			if (evt.getNewValue() != null)
			{
				final Calendar value = (Calendar) evt.getNewValue();
				final DateModel<?> model = new UtilCalendarModel(value);
				// check constraints
				if (!m_datePanel.checkConstraints(model))
				{
					// rollback
					m_formattedTextField.setValue(evt.getOldValue());
					return;
				}
				m_datePanel
					.getModel()
					.setDateButWrong(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));
				m_datePanel.getModel().setSelected(true);
			}

			// Clearing textfield will also fire change event
			if (evt.getNewValue() == null)
			{
				// Set model value unselected, this will fire an event
				getModel().setSelected(false);
			}
		}

		@Override
		public void stateChanged(final ChangeEvent arg0)
		{
			if (arg0.getSource() == m_datePanel.getModel())
			{
				final DateModel<?> model = m_datePanel.getModel();
				setTextFieldValue(
					m_formattedTextField,
					model.getYear(),
					model.getMonthButWrong(),
					model.getDay(),
					model.isSelected());
			}
		}
	}

	private static final long serialVersionUID = 2814777654384974503L;

	public static void main(final String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(Messages.getString("JDatePicker.0")); //$NON-NLS-1$
		}
		catch (final Exception e)
		{
			// what do I do here?
		}
		final JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		testFrame.setSize(500, 500);
		final JPanel jPanel = new JPanel();
		final DatePicker picker = new JDatePicker();
		picker.setTextEditable(true);
		picker.setShowYearButtons(true);
		jPanel.add((JComponent) picker);
		final JPanel DatePanel = new JPanel();
		DatePanel.setLayout(new BorderLayout());
		DatePanel.add(jPanel, BorderLayout.WEST);
		final BorderLayout fb = new BorderLayout();
		testFrame.setLayout(fb);
		testFrame.getContentPane().add(DatePanel, BorderLayout.WEST);
		testFrame.setVisible(true);
	}

	public static void setTextFieldValue(
		final JFormattedTextField textField,
		final int year,
		final int month,
		final int day,
		final boolean isSelected)
	{
		if (!isSelected)
		{
			textField.setValue(null);
		}
		else
		{
			final Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			textField.setValue(calendar);
		}
	}

	private static ComponentColorDefaults getColors()
	{
		return ComponentColorDefaults.getInstance();
	}

	Popup m_popup;

	final JFormattedTextField m_formattedTextField;

	final JButton m_button;

	final JDatePanel m_datePanel;

	private final InternalEventHandler m_internalEventHandler;

	/**
	 * Create a JDatePicker with a default calendar model.
	 */
	public JDatePicker()
	{
		this(new JDatePanel());
	}

	/**
	 * Create a JDatePicker with an initial value, with a UtilCalendarModel.
	 *
	 * @param value the initial value
	 */
	public JDatePicker(final Calendar value)
	{
		this(new JDatePanel(value));
	}

	/**
	 * Create a JDatePicker with a custom date model.
	 *
	 * @param model a custom date model
	 */
	public JDatePicker(final DateModel<?> model)
	{
		this(new JDatePanel(model));
	}

	/**
	 * Create a JDatePicker with an initial value, with a SqlDateModel.
	 *
	 * @param value the initial value
	 */
	public JDatePicker(final java.sql.Date value)
	{
		this(new JDatePanel(value));
	}

	/**
	 * Create a JDatePicker with an initial value, with a UtilDateModel.
	 *
	 * @param value the initial value
	 */
	public JDatePicker(final java.util.Date value)
	{
		this(new JDatePanel(value));
	}

	/**
	 * You are able to set the format of the date being displayed on the label.
	 * Formatting is described at:
	 *
	 * @param datePanel The DatePanel to use
	 */
	private JDatePicker(final JDatePanel datePanel)
	{
		m_datePanel = datePanel;

		// Initialise Variables
		m_popup = null;
		datePanel
			.setBorder(BorderFactory.createLineBorder(getColors().getColor(ComponentColorDefaults.Key.POPUP_BORDER)));
		m_internalEventHandler = new InternalEventHandler();

		// Create Layout
		final SpringLayout layout = new SpringLayout();
		setLayout(layout);

		// Create and Add Components
		// Add and Configure TextField
		m_formattedTextField = new JFormattedTextField(new DateComponentFormatter());
		final DateModel<?> model = datePanel.getModel();
		setTextFieldValue(
			m_formattedTextField,
			model.getYear(),
			model.getMonthButWrong(),
			model.getDay(),
			model.isSelected());
		m_formattedTextField.setEditable(false);
		add(m_formattedTextField);
		layout.putConstraint(SpringLayout.WEST, m_formattedTextField, 0, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, m_formattedTextField);

		// Add and Configure Button
		m_button = new JButton();
		m_button.setFocusable(true);
		final Icon icon = ComponentIconDefaults.getInstance().getPopupButtonIcon();
		m_button.setIcon(icon);
		if (icon == null)
		{
			// reset to caption
			m_button.setText(Messages.getString("JDatePicker.1")); //$NON-NLS-1$
		}
		else
		{
			// remove text
			m_button.setText(Messages.getString("JDatePicker.2")); //$NON-NLS-1$
		}
		add(m_button);
		layout.putConstraint(SpringLayout.WEST, m_button, 1, SpringLayout.EAST, m_formattedTextField);
		layout.putConstraint(SpringLayout.EAST, this, 0, SpringLayout.EAST, m_button);
		layout.putConstraint(SpringLayout.SOUTH, this, 0, SpringLayout.SOUTH, m_button);

		// Do layout formatting
		final int h = (int) m_button.getPreferredSize().getHeight();
		final int w = (int) datePanel.getPreferredSize().getWidth();
		m_button.setPreferredSize(new Dimension(h, h));
		m_formattedTextField.setPreferredSize(new Dimension(w - h - 1, h));

		// Add event listeners
		addHierarchyBoundsListener(m_internalEventHandler);
		// TODO addAncestorListener(listener)
		m_button.addActionListener(m_internalEventHandler);
		m_formattedTextField.addPropertyChangeListener(Messages.getString("JDatePicker.3"), m_internalEventHandler); //$NON-NLS-1$
		datePanel.addActionListener(m_internalEventHandler);
		datePanel.getModel().addChangeListener(m_internalEventHandler);
		final long eventMask = MouseEvent.MOUSE_PRESSED;
		Toolkit.getDefaultToolkit().addAWTEventListener(m_internalEventHandler, eventMask);
	}

	@Override
	public void addActionListener(final ActionListener actionListener)
	{
		m_datePanel.addActionListener(actionListener);
	}

	@Override
	public void addDateSelectionConstraint(final DateSelectionConstraint constraint)
	{
		m_datePanel.addDateSelectionConstraint(constraint);
	}

	public void clearDatePanel()
	{
		m_formattedTextField.setValue(null);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePicker#getButtonFocusable()
	 */
	@Override
	public boolean getButtonFocusable()
	{
		return m_button.isFocusable();
	}

	@Override
	public Set<DateSelectionConstraint> getDateSelectionConstraints()
	{
		return m_datePanel.getDateSelectionConstraints();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePicker#getJDateInstantPanel()
	 */
	public DatePanel getJDateInstantPanel()
	{
		return m_datePanel;
	}

	@Override
	public DateModel<?> getModel()
	{
		return m_datePanel.getModel();
	}

	@Override
	public int getTextfieldColumns()
	{
		return m_formattedTextField.getColumns();
	}

	@Override
	public boolean isDoubleClickAction()
	{
		return m_datePanel.isDoubleClickAction();
	}

	@Override
	public boolean isShowYearButtons()
	{
		return m_datePanel.isShowYearButtons();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePicker#isTextEditable()
	 */
	@Override
	public boolean isTextEditable()
	{
		return m_formattedTextField.isEditable();
	}

	@Override
	public void removeActionListener(final ActionListener actionListener)
	{
		m_datePanel.removeActionListener(actionListener);
	}

	@Override
	public void removeAllDateSelectionConstraints()
	{
		m_datePanel.removeAllDateSelectionConstraints();
	}

	@Override
	public void removeDateSelectionConstraint(final DateSelectionConstraint constraint)
	{
		m_datePanel.removeDateSelectionConstraint(constraint);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePicker#setButtonFocusable(boolean)
	 */
	@Override
	public void setButtonFocusable(final boolean focusable)
	{
		m_button.setFocusable(focusable);
	}

	public void setDateExternallyButWrong(final int p_year, final int p_month, final int p_date)
	{
		m_datePanel.getModel().setDateButWrong(p_year, p_month, p_date);
		m_datePanel.getModel().setSelected(true);
		setTextFieldValue(
			m_formattedTextField,
			m_datePanel.getModel().getYear(),
			m_datePanel.getModel().getMonthButWrong(),
			m_datePanel.getModel().getDay(),
			m_datePanel.getModel().isSelected());
	}

	@Override
	public void setDoubleClickAction(final boolean doubleClickAction)
	{
		m_datePanel.setDoubleClickAction(doubleClickAction);
	}

	@Override
	public void setEnabled(final boolean enabled)
	{
		m_button.setEnabled(enabled);
		m_datePanel.setEnabled(enabled);
		m_formattedTextField.setEnabled(enabled);

		super.setEnabled(enabled);
	}

	@Override
	public void setShowYearButtons(final boolean showYearButtons)
	{
		m_datePanel.setShowYearButtons(showYearButtons);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.jdatepicker.JDatePicker#setTextEditable(boolean)
	 */
	@Override
	public void setTextEditable(final boolean editable)
	{
		m_formattedTextField.setEditable(editable);
	}

	@Override
	public void setTextfieldColumns(final int columns)
	{
		m_formattedTextField.setColumns(columns);
	}

	@Override
	public void setVisible(final boolean aFlag)
	{
		if (!aFlag)
		{
			hidePopup();
		}
		super.setVisible(aFlag);
	}

	Set<Component> getAllComponents(final Component component)
	{
		final Set<Component> children = new HashSet<>();
		children.add(component);
		if (component instanceof Container)
		{
			final Container container = (Container) component;
			final Component[] components = container.getComponents();
			for (final Component component2 : components)
			{
				children.addAll(getAllComponents(component2));
			}
		}
		return children;
	}

	/**
	 * Called internally to hide the popup dates.
	 */
	void hidePopup()
	{
		if (m_popup != null)
		{
			m_popup.hide();
			m_popup = null;
		}
	}

	/**
	 * Called internally to popup the dates.
	 */
	void showPopup()
	{
		if (m_popup == null)
		{
			final PopupFactory fac = new PopupFactory();
			final Point xy = getLocationOnScreen();
			m_datePanel.setVisible(true);
			m_popup = fac.getPopup(this, m_datePanel, (int) xy.getX(), (int) (xy.getY() + getHeight()));
			m_popup.show();
		}
	}

}