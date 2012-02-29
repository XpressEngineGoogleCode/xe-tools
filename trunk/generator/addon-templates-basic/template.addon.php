<?php
/**
 * @file	$PARAM_ADDON_NAME.addon.php
 * @author	$PARAM_ADDON_AUTHOR_NAME ($PARAM_ADDON_AUTHOR_EMAIL_ADDRESS)
 * @brief	$PARAM_ADDON_DESCRIPTION
 **/
 
if(!defined('__XE__')) exit();
 
// switch by entry point
 switch($called_position)
 {
	// Before creating a module object:  After finding a necessary module upon a user request and before creating the object of that module.
	case 'before_module_init':
		break;
	// Before executing a module:  After executing the object of a module and before executing the module.
	case 'before_module_proc':
		break;
	// After executing a module:  Immediately after executing a created module object and obtaining the result.
	case 'after_module_proc':
		break;
	// Before displaying result: Immediately before displaying the result of a module to which a layout has been applied.
	case 'before_display_content':
		break;
 }
?>
