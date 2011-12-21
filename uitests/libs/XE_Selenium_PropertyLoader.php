<?php
abstract class XE_Selenium_PropertyLoader
{
	abstract public function load();
	abstract public function getXEPrefix();
	abstract public function getSeleniumHost();
	abstract public function getSeleniumPort();
	abstract public function getSeleniumWebdriver();
	abstract public function getSiteUrl();
	abstract public function getSiteAdmin();
	abstract public function hasInstall();
	abstract public function getInstallPath();
	abstract public function getInstallDatabase();
	abstract public function getRepositories();
}
