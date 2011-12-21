<?php
include_once dirname(__FILE__) . '/../libs/__init__.php';

class XEInstallMySQLTest extends PHPUnit_Framework_TestCase
{
	protected static $oXESelenium;
	protected static $errorScreenFile = './install.mysql.error.png';
	protected static $property = './install.mysql.properties';

	public static function setUpBeforeClass()
	{
		// load and copy file
		self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(self::$property));
		self::$oXESelenium->install(new XE_Selenium_InstallLocalHelper());
	}

	public static function tearDownAfterClass()
	{
		// remove file and DB
		self::$oXESelenium->uninstall();
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

	public function testCopyed()
	{
		// go to the XE
		self::$oXESelenium->connect();

		// check html title
		$this->assertEquals('XE Installation', self::$oXESelenium->getTitle());
	}

	/**
	 * @depends testCopyed
	 */
	public function testAcceptanceOfTerms()
	{
		$act = self::$oXESelenium->getElementValue('xpath', '/html/body/div/div[2]/div[2]/form/input[5]');
		$this->assertEquals('procInstallAgreement', $act);

		// agreement
		self::$oXESelenium->clickElement('xpath', '//*[@id="lgpl"]');
		self::$oXESelenium->clickElement('xpath', '//*[@id="env"]');

		// go next
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/form/div[3]/div[2]/span/input');
	}

	/**
	 * @depends testAcceptanceOfTerms
	 */
	public function testCheckTheInstallationCondition()
	{
		// check the url
		$url = self::$oXESelenium->getCurrentUrl();
		$this->assertRegExp('/dispInstallCheckEnv/', $url);

		// go next
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/div[2]/div[2]/span/a');
	}

	/**
	 * @depends testCheckTheInstallationCondition
	 */
	public function testChooseDatabaseType()
	{
		// check the url
		$url = self::$oXESelenium->getCurrentUrl();
		$this->assertRegExp('/dispInstallSelectDB/', $url);

		// select db
		self::$oXESelenium->clickElement('id', 'db_type_' . self::$oXESelenium->getInstallDatabase('type'));
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/form/div/div[2]/span/input');
	}

	/**
	 * @depends testChooseDatabaseType
	 */
	public function testInputDatabaseinformationMySQL()
	{
		$act = self::$oXESelenium->getElementValue('xpath', '/html/body/div/div[2]/div[2]/form/input[5]');
		$this->assertEquals('procMysqlDBSetting', $act);

		self::$oXESelenium->clearElementText('id', 'dbHostName');
		self::$oXESelenium->clearElementText('id', 'dbPort');
		self::$oXESelenium->clearElementText('id', 'dbPrefix');

		self::$oXESelenium->setElementText('id', 'dbHostName', self::$oXESelenium->getInstallDatabase('host'));
		self::$oXESelenium->setElementText('id', 'dbPort', self::$oXESelenium->getInstallDatabase('port'));
		self::$oXESelenium->setElementText('id', 'dbId', self::$oXESelenium->getInstallDatabase('user'));
		self::$oXESelenium->setElementText('id', 'dbPw', self::$oXESelenium->getInstallDatabase('password'));
		self::$oXESelenium->setElementText('id', 'dbName', self::$oXESelenium->getInstallDatabase('database'));
		self::$oXESelenium->setElementText('id', 'dbPrefix', self::$oXESelenium->getXEPreFix());
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/form/div[2]/div[2]/span/input');
	}
	
	/**
	 * @depends testInputDatabaseinformationMySQL
	 */
	public function testSettings()
	{
		$url = self::$oXESelenium->getCurrentUrl();
		$this->assertRegExp('/dispInstallConfigForm/', $url);

		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/form/div/div[2]/span/input');
	}

	/**
	 * @depends testSettings
	 */
	public function testEnterAdministrator()
	{
		$url = self::$oXESelenium->getCurrentUrl();
		$this->assertRegExp('/dispInstallManagerForm/', $url);

		self::$oXESelenium->clearElementText('id', 'aId');

		self::$oXESelenium->setElementText('id', 'aMail', self::$oXESelenium->getSiteAdmin('email'));
		self::$oXESelenium->setElementText('id', 'aPw1', self::$oXESelenium->getSiteAdmin('password'));
		self::$oXESelenium->setElementText('id', 'aPw2', self::$oXESelenium->getSiteAdmin('password'));
		self::$oXESelenium->setElementText('id', 'aId', self::$oXESelenium->getSiteAdmin('userid'));
		self::$oXESelenium->setElementText('id', 'aNick', self::$oXESelenium->getSiteAdmin('nickname'));
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/form/div[2]/div[2]/span/input');
	}

	/**
	 * @depends testEnterAdministrator
	 */
	public function testIndex()
	{
		$this->assertEquals('welcome_page', self::$oXESelenium->getTitle());
	}
}
