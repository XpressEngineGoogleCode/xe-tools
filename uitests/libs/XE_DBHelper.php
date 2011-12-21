<?php
abstract class XE_DBHelper
{
	abstract public function setPropertyLoader(XE_Selenium_PropertyLoader $oPropertyLoader);
	abstract public function connect();
	abstract public function close();
	abstract public function dropData();
}
