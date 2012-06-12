package setup;

import java.awt.Container;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * Handles the display of each option
 */
public class Option {
	String optionName;	//name of the option (without brackets)
	JLabel name;		//label that displays the option name
	JLabel description;	//description for the option
	JComboBox select;	//select box with options
	JTextField text;	//text field used to enter the option value
	boolean isSelectBox;//true if it is a select box
	
	/**
	 * Sets the option name and description and no default value
	 */
	public Option(String name, String description) {
		this.name = new JLabel(name);
		this.description = new JLabel(description);
		this.name.setFont(Fonts.lucida12);
		this.description.setFont(Fonts.lucida12);
		this.optionName = name.replace("*","");
	}
	
	/**
	 * Sets the option name and description and a default value
	 */
	public Option(String name, String description,String text) {
		this(name,description);
		this.text = new JTextField(20);
		this.text.setText(text);
		this.text.setFont(Fonts.lucida12);
	}
	
	/**
	 * Sets the option name and description and a default value
	 */
	public Option(String name, String description,Object[] values, Object defaultValue) {
		this(name,description);
		this.select = new JComboBox(values);
		this.select.setSelectedItem(defaultValue);
		this.select.setFont(Fonts.lucida12);
		isSelectBox = true;
	}

	/**
	 * Adds components to a container
	 * @param pane the container where the components will be added
	 * @param y	height at which the components will be added
	 */
	public void addToPane(Container pane, int y) {
		//set location and size for each component
		name.setBounds(10,y,160,30);
		description.setBounds(480,y,300,30);
		
		//add components to panel
		pane.add(name);
		if(isSelectBox) {
			select.setBounds(175,y,300,30);
			pane.add(select);
		} else {
			text.setBounds(175,y,300,30);
			pane.add(text);
		}
		pane.add(description);
	}
	
	/**
	 * Hides or displays the option
	 * @param visibility is true if the option is visible
	 */
	public void setVisible(boolean visibility) {
		name.setVisible(visibility);
		if(isSelectBox) {
			select.setVisible(visibility);
		} else {
			text.setVisible(visibility);
		}
		description.setVisible(visibility);
	}
}
