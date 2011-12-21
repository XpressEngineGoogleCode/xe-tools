<?php

abstract class XE_Selenium_InstallHelper
{
	abstract public function setPropertyLoader(XE_Selenium_PropertyLoader $oPropertyLoader);
	abstract public function install();
	abstract public function uninstall();
}
