<?php
$commands		= array('module', 'widget', 'addon');
$parameters		= array(
						'module' => array('basic', 'developer', 'demo'),
						'widget' => array('basic', 'demo'),
						'addon'  => array('basic', 'demo')
						);
$commands_help 	= array(
						'module' => 'Helps you generate a module',
						'widget' => 'Helps you generate a widget',
						'addon'  => 'Helps you generate an addon'
						);			   
$parameters_help = array(
						'module.basic'		=> 'Creates an empty module (only the structure)',
						'module.developer'	=> 'Creates a module with a standard BE',
						'module.demo'		=> 'Creates a demo module (recommended for getting started)',
						'widget.basic'		=> 'Creates an empty widget (only the structure)',
						'widget.demo'		=> 'Creates a demo widget (recommended for getting started)',
						'addon.basic'		=> 'Creates an empty addon (only the structure)',
						'addon.demo'		=> 'Creates a demo addon (recommended for getting started)'
						);

/**
 * @brief print_usage method that displays the commands options of the tool
 * @access public
 * @param $what : name of the command (optional - can be empty)
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function print_usage($what = '')
{	global $commands, $parameters, $commands_help, $parameters_help;
	$argc = $_SERVER['argc'];
	$argv = $_SERVER['argv'];
	
	if(strlen($what) == 0)
	{
		echo "\n\n";
		echo "You need to give some command\n";
		echo "Usage: php $argv[0] COMMAND [options]\n\n";
		echo "List of Commands:\n\n";
		
		$lengths = array_map('strlen', $commands);
		$max_length = max($lengths);
		
		foreach($commands as $cmd)
		{
			echo $cmd;
			for($counter = 0; $counter < ($max_length - strlen($cmd)); $counter++)
			{
				echo ' ';
			}
			echo "\t";
			echo $commands_help[$cmd] . "\n";
		}
	
		echo "\n\n";
		
		exit(0);
	}
	
	$lengths = array_map('strlen', $parameters[$what]);
	$max_length = max($lengths);
	
	echo "\n\n";	
	foreach($parameters[$what] as $param)
	{
		echo $param;
		for($counter = 0; $counter < ($max_length - strlen($param)); $counter++)
		{
			echo ' ';
		}
		echo "\t";
		echo $parameters_help[$what . '.' . $param] . "\n";
		
	}
	
	echo "\n\n";
	
	exit(0);
}

/**
 * @brief parse_arguments method that interprets the command provided by the user and call the corresponding handler
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function parse_arguments()
{	global $commands, $parameters;
	$argc = $_SERVER['argc'];
	$argv = $_SERVER['argv'];
	
	if($argc == 1)
	{
		print_usage();
		return;
	}
	
	if(!in_array($argv[1], $commands))
	{
		print_usage();
	}
		
	if(!empty($parameters[$argv[1]]) && $argc == 2)
	{
		print_usage($argv[1]);
	}
		
	$func_name = 'generate_' . $argv[1];
	if(!empty($parameters[$argv[1]]))
	{
		$func_name = $func_name . '_' . $argv[2];
	}
	
	if(!function_exists($func_name))
	{
		echo "Function not implemented\nApplication will exit\n\n";
		exit(0);
	}
	
	call_user_func($func_name);
}

/**
 * @brief print_additional_options method that displays the particular options for each commands
 * @access public
 * @param $parameters_offset : the position in the argv vector where to read the particular options for each command
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function parse_additional_options($parameters_offset)
{
	$argc = $_SERVER['argc'];
	$argv = $_SERVER['argv'];
	
	$retVal = array();
	for($counter = $parameters_offset; $counter < $argc; $counter += 2)
	{
		if(($counter + 1) < $argc)
		{
			$retVal[$argv[$counter]] = $argv[$counter + 1];
		}
	}
	return $retVal;
}

$global_templates_xml = NULL;
$global_variable = NULL;

/**
 * @brief rd_tpls_start_tag method is a callback called by XML parser in order parse the template xml file 
 * @access public
 * @param $parser	: the XML Parser object
 * @param $name		: the name of the tag
 * @param $attrs	: the attributes of the tag
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function rd_tpls_start_tag($parser, $name, $attrs)
{
	global $global_templates_xml;
	global $global_variable;
	switch($name)
	{
		case 'PARAMETERS':
			$global_templates_xml['parameters'] = array();
			break;
		case 'PARAMETER':
			if(isset($attrs['ALIAS']))
			{
				$global_templates_xml['parameters'][$attrs['ID']] = array(NULL, NULL, $attrs['VARIABLE_NAME'], $attrs['REQUIRED'], 1, $attrs['ALIAS'], $attrs['TRANSFORM']);
			}
			else
			{
				$global_templates_xml['parameters'][$attrs['ID']] = array($attrs['DESCRIPTION'], $attrs['OPTION_NAME'], $attrs['VARIABLE_NAME'],  $attrs['REQUIRED'], 0);
			}
			break;
		case 'FOLDERS':
			$global_templates_xml['folders'] = array();
			break;
		case 'FOLDER':
			$global_templates_xml['folders'][] = $attrs['RELATIVE_PATH'];
			break;
		case 'FILES':
			$global_templates_xml['files'] = array();
			break;
		case 'FILE':
			$global_templates_xml['files'][$attrs['RELATIVE_PATH']][0] = array();
			$global_variable = $attrs['RELATIVE_PATH'];
			if(isset($attrs['REPLACE_FILE_NAME_WITH_PARAM_ID']))
			{
				$global_templates_xml['files'][$attrs['RELATIVE_PATH']][1] = $attrs['REPLACE_FILE_NAME_WITH_PARAM_ID'];
			}
			break;
		case 'PARAM':
			$global_templates_xml['files'][$global_variable][0][] = $attrs['ID'];
			break;
	}
	
}
/**
 * @brief read_templates_xml_file method that reads and parse the template xml file 
 * @access public
 * @param $file_name	: the file name of the XML template file
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function read_templates_xml_file($file_name)
{
	global $global_templates_xml;
	global $global_variable;
	$xml_parser = xml_parser_create();
	$global_variable = array();
	xml_set_element_handler($xml_parser, "rd_tpls_start_tag", FALSE);
	
	if(!($fp = fopen($file_name, "r"))) 
	{
		die("Error: Could not open XML input file - $file_name");
	}
	while($data = fread($fp, 4096)) 
	{
		if(!xml_parse($xml_parser, $data, feof($fp))) 
		{
			die(sprintf("XML error: %s at line %d",
						xml_error_string(xml_get_error_code($xml_parser)),
						xml_get_current_line_number($xml_parser)));
		}
	}
	fclose($fp);
	xml_parser_free($xml_parser);
}

/**
 * @brief get_templates_parameters method that extracts from the template file info the list of options
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function get_templates_parameters()
{
	global $global_templates_xml;
	$return_value = array();

	foreach(array_keys($global_templates_xml['parameters']) as $param)
	{
		$return_value[$global_templates_xml['parameters'][$param][1]] = array(($global_templates_xml['parameters'][$param][3] == 'true'),  $global_templates_xml['parameters'][$param][0], $global_templates_xml['parameters'][$param][4]);
	}
	
	return $return_value;
}

/**
 * @brief print_usage_options method that displays to the user the list of possible options
 * @access public
 * @param $options	: the list of options
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function print_usage_options($options)
{
	echo "\n\n";
	echo "Invalid options. Please see below the list of possible options\n\n";
		
	$lengths = array_map('strlen', array_keys($options));
	$max_length = max($lengths);
		
	foreach(array_keys($options) as $opt)
	{
		if($options[$opt][2])// if it's an alias parameter just skip it
		{
			continue;
		}
		echo ($options[$opt][0])?' ':'[';
		echo $opt;
		echo ($options[$opt][0])?' ':']';
		
		for($counter = 0; $counter < ($max_length - strlen($opt)); $counter++)
		{
			echo ' ';
		}
		echo "\t";
		echo $options[$opt][1] . "\n";
	}
	echo "\n\n";
	exit(0);
}

/**
 * @brief map_parameters_values method that matches command options with the values provided by the user
 * @access public
 * @param $options	: the list of command options
 * @return array
 * @developer Catalin
 * @date 2012-02-10
 */
function map_parameters_values($options)
{
	global $global_templates_xml;
	$return_value = array();

	foreach(array_keys($global_templates_xml['parameters']) as $param)
	{
		if($global_templates_xml['parameters'][$param][4])// if it's an alias param
		{
			$alias_value = $return_value[$global_templates_xml['parameters'][$param][5]][1];
			switch($global_templates_xml['parameters'][$param][6])// transform
			{
				case 'ucword':
					$alias_value = ucwords($alias_value);
					break;
				default:
					break;
			}
			$return_value[$param] = array(($global_templates_xml['parameters'][$param][3] == 'true'), $alias_value);
		}
		else
		{
			$option_name = $global_templates_xml['parameters'][$param][1];
			$return_value[$param] = array(($global_templates_xml['parameters'][$param][3] == 'true'), isset($options[$option_name])?$options[$option_name]:'');
		}
	}
	
	return $return_value;
}

/**
 * @brief generate_item_from_templates method that can generate a specific item (module, widget, addon)
 * @access public
 * @param $templates_xml_file_name	: the name of the XML template file
 * @param $templates_base_dir		: the base dir of the XML template file
 * @param $item_type				: the type of the item to be generated
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_item_from_templates($templates_xml_file_name, $templates_base_dir, $item_type)
{
	global $global_templates_xml;
	$command_line_options = parse_additional_options(3);
	read_templates_xml_file($templates_xml_file_name);
	$parameters = get_templates_parameters();
	
	$required_parameters = array();
	foreach(array_keys($parameters) as $param)
	{
		if($parameters[$param][0])
		{
			$required_parameters[] = $param;
		}
	}
	if(count($required_parameters) > count($command_line_options) || count(array_diff($required_parameters, array_keys($command_line_options))) > 0)
	{
		print_usage_options($parameters);
	}
		
	$parameters_values = map_parameters_values($command_line_options);
	
	if(@mkdir('./' . $parameters_values['name'][1] . '/', 0777) == FALSE)
	{
		die("Error: Could not create new module's folder\n");
	}
	
	foreach($global_templates_xml['folders'] as $folder)
	{
		if(@mkdir('./' . $parameters_values['name'][1] . '/' . $folder, 0777, TRUE) == FALSE)
		{
			die("Error: Could not create new module's sub-folders structure\n");
		}
	}
	
	foreach(array_keys($global_templates_xml['files']) as $file)
	{
		$source_name = $file;
		if(isset($global_templates_xml['files'][$file][1]))
		{
			$destination_name = str_replace('template', $parameters_values[$global_templates_xml['files'][$file][1]][1], $source_name);
		}
		else
		{
			$destination_name = $file;
		}
		
		if(($data = file_get_contents($templates_base_dir . $source_name)) === FALSE)
		{
			die("Error: could not open template file for read\n");
		}
		
		foreach($global_templates_xml['files'][$file][0] as $param)
		{
			$data = str_replace($global_templates_xml['parameters'][$param][2], $parameters_values[$param][1], $data);
		}
		
		if((file_put_contents('./' . $parameters_values['name'][1] . '/' . $destination_name, $data)) === FALSE)
		{
			die("Error: could not open module file for write\n");
		}
	}
	
	echo "\n\n{$item_type} called {$parameters_values['name'][1]} generated successfully in " . './' . $parameters_values['name'][1] . '/' . "\n\n";
}

define("MODULE_TEMPLATES_XML_FILE_NAME_BASIC", "./module-templates-basic.xml");

/**
 * @brief generate_module_basic method that generates a basic module
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_module_basic()
{
	generate_item_from_templates(MODULE_TEMPLATES_XML_FILE_NAME_BASIC, './module-templates-basic/', 'Basic module');
}

define("MODULE_TEMPLATES_XML_FILE_NAME_DEVELOPER", "./module-templates-developer.xml");

/**
 * @brief generate_module_developer method that generates a developer module
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_module_developer()
{
	generate_item_from_templates(MODULE_TEMPLATES_XML_FILE_NAME_DEVELOPER, './module-templates-developer/', 'Developer module');
}

define("MODULE_TEMPLATES_XML_FILE_NAME_DEMO", "./module-templates-demo.xml");

/**
 * @brief generate_module_demo method that generates a demo module
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_module_demo()
{
	generate_item_from_templates(MODULE_TEMPLATES_XML_FILE_NAME_DEMO, './module-templates-demo/', 'Demo module');
}

define("WIDGET_TEMPLATES_XML_FILE_NAME_BASIC", "./widget-templates-basic.xml");

/**
 * @brief generate_widget_basic method that generates a basic widget
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_widget_basic()
{
	generate_item_from_templates(WIDGET_TEMPLATES_XML_FILE_NAME_BASIC, './widget-templates-basic/', 'Basic widget');
}

define("WIDGET_TEMPLATES_XML_FILE_NAME_DEMO", "./widget-templates-demo.xml");

/**
 * @brief generate_widget_demo method that generates a demo widget
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_widget_demo()
{
	generate_item_from_templates(WIDGET_TEMPLATES_XML_FILE_NAME_DEMO, './widget-templates-demo/', 'Demo widget');
}

define("ADDON_TEMPLATES_XML_FILE_NAME_BASIC", "./addon-templates-basic.xml");

/**
 * @brief generate_addon_basic method that generates a basic addon
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_addon_basic()
{
	generate_item_from_templates(ADDON_TEMPLATES_XML_FILE_NAME_BASIC, './addon-templates-basic/', 'Basic addon');
}

define("ADDON_TEMPLATES_XML_FILE_NAME_DEMO", "./addon-templates-demo.xml");

/**
 * @brief generate_addon_demo method that generates a demo addon
 * @access public
 * @return void
 * @developer Catalin
 * @date 2012-02-10
 */
function generate_addon_demo()
{
	generate_item_from_templates(ADDON_TEMPLATES_XML_FILE_NAME_DEMO, './addon-templates-demo/', 'Demo addon');
}

parse_arguments();

/* End of file xe-generator.php */
/* Location: ./generator/xe-generator.php */
