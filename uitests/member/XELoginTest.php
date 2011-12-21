<?php
include_once '../libs/__init__.php';

class XELoginTest extends PHPUnit_Framework_TestCase
{
	protected static $oXESelenium;
	protected static $property = './xe.property';
	protected static $errorScreenFile = './login.error.png';

	public static function setUpBeforeClass()
	{
		// load and copy file
		self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(self::$property));
		self::$oXESelenium->install(new XE_Selenium_InstallLocalHelper());
		self::$oXESelenium->connect();
	}

	protected function setUp()
	{
		sleep(2);
	}

	protected function tearDown()
	{
		if($this->hasFailed())
		{
			self::$oXESelenium->getScreenshotAndSaveToFile(self::$errorScreenFile);
		}
	}

	public function testLogin()
	{
		self::$oXESelenium->setElementText('name', 'user_id', self::$oXESelenium->getSiteAdmin('email'));
		self::$oXESelenium->setElementText('name', 'password', self::$oXESelenium->getSiteAdmin('password'));
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div/div/div/form/fieldset/div/input');
		sleep(2);

		$this->assertRegExp('/dispMemberLogout/', self::$oXESelenium->getPageSource());
	}

	/**
	 * @depends testLogin
	 */
	public function testLogout()
	{
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div/div/div/form/fieldset/div/a[2]');
		sleep(2);

		$this->assertRegExp('/keep_signed/', self::$oXESelenium->getPageSource());
	}
}
