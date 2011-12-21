<?php
include_once '../libs/__init__.php';

class XELoginTest extends PHPUnit_Framework_TestCase
{
	protected static $oXESelenium;
	protected static $property = './xe.properties';
	protected static $errorScreenFile = './login.error.png';

	public static function setUpBeforeClass()
	{
		// load and copy file
		self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(self::$property));
		self::$oXESelenium->install(new XE_Selenium_InstallLocalHelper());
		self::$oXESelenium->connect('chrome');
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

		$src = self::$oXESelenium->getPageSource();
		$this->assertRegExp('/dispMemberLogout/', $src);
	}

	/**
	 * @depends testLogin
	 */
	public function testAdmin()
	{
		self::$oXESelenium->goAdminPage();
		sleep(2);
		
		$this->doInstall();

//		self::$oXESelenium->executeScript("alert(1)", array());
	}

	private function doInstall()
	{
		sleep(1);
		self::$oXESelenium->goAdminPage();
		$src = self::$oXESelenium->getPageSource();

		if(preg_match_all('/doInstallModule\(\'[a-zA-Z0-9_-]+\'\);/', $src, $matches))
		{
			self::$oXESelenium->executeScript($matches[0][0], array());
			$oWebDriver = self::$oXESelenium->getWebdriver();
			$a = $oWebDriver->getAlertText();
			$this->assertEquals('q', print_r($a,1));

			$oWebDriver->dismissAlert();

			sleep(3);

			if(count($matches[0]) > 1)
			{
				$this->doInstall();
			}
			else
			{
				return;
			}
		}

		return;
	}


	/**
	 * @depends testLogin
	 */
	public function testLogout()
	{
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div/div/ul/li[2]/a');
		sleep(2);

		$src = self::$oXESelenium->getPageSource();
		$this->assertRegExp('/loginAccess/', $src);
	}
}
