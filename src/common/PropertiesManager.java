package common;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager
{
	private static final String PROPERTIES_FILE       = Messages.getString("PropertiesManager.0"); //$NON-NLS-1$

	public static final String  SAVE_LOCATION_KEY     = Messages.getString("PropertiesManager.1"); //$NON-NLS-1$

	private static final String DEFAULT_SAVE_LOCATION = Messages.getString("PropertiesManager.2"); //$NON-NLS-1$

	private static String getDefaultProperty(String p_propertyName)
	{
		if (p_propertyName.equals(SAVE_LOCATION_KEY))
		{
			return DEFAULT_SAVE_LOCATION;
		}

		throw new RuntimeException();
	}

	private final Properties m_props;

	public PropertiesManager()
	{
		m_props = new Properties();
		try (FileReader reader = new FileReader(PROPERTIES_FILE))
		{
			m_props.load(reader);
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getProperty(String p_propertyName)
	{
		final String defaultProperty = getDefaultProperty(p_propertyName);
		String       ret             = m_props.getProperty(p_propertyName);
		if (ret == null)
		{
			ret = defaultProperty;
			m_props.put(p_propertyName, ret);
		}
		return ret;
	}

	public void save()
	{
		try (FileWriter writer = new FileWriter(PROPERTIES_FILE))
		{
			m_props.store(writer, Messages.getString("PropertiesManager.3")); //$NON-NLS-1$
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}
}