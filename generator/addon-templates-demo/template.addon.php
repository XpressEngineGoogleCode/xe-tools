<?php

/**
 * @file	$PARAM_ADDON_NAME.addon.php
 * @author	$PARAM_ADDON_AUTHOR_NAME ($PARAM_ADDON_AUTHOR_EMAIL_ADDRESS)
 * @brief	$PARAM_ADDON_DESCRIPTION
 **/

if (!defined('__ZBXE__'))
    exit();

if( ($called_position != 'after_module_proc' && $called_position != 'before_display_content') || Context::getResponseMethod() !== 'HTML')
{
    return;
}

// switch by entry point
switch ($called_position)
{
	case 'before_module_init':
		break;
	case 'before_module_proc':
		break;    
	case 'after_module_proc':
		Context::addHtmlHeader('    <link rel="stylesheet" type="text/css" href="addons/$PARAM_ADDON_NAME/style/external_links.css" />');
		break;
	case 'before_display_content':
		require_once('XELinkScannerAddon.class.php');
		$link_scanner = new XELinkScannerAddon();
		$output = $link_scanner->ProcessPage($output);
		unset($link_scanner);
		break;
}
?>
