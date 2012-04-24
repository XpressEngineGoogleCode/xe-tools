<?php
include_once dirname(__FILE__) . '../../../libs/__init__.php';

class XEInstallTest extends PHPUnit_Framework_TestCase
{
	protected static $oXESelenium;

	public static function setUpBeforeClass()
	{
		// load and copy file
		self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(dirname(__FILE__) . '/' .static::$property));
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
			self::$oXESelenium->getScreenshotAndSaveToFile(dirname(__FILE__) . '/' . static::$errorScreenFile);
		}
	}

	public function testCopied()
	{
		// go to the XE
		self::$oXESelenium->connect();

		// check html title
		$this->assertEquals('Welcome to Xpress Engine CMS Installation wizard', self::$oXESelenium->getTitle());
                
		// go next
		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
	}

	/**
	 * @depends testCopied
	 */
	public function testAcceptanceOfTerms()
	{
		$act = self::$oXESelenium->getElementValue('xpath', '//form[@id="agreement"]/input[@name="act"]');
		$this->assertEquals('procInstallAgreement', $act);

		// agreement
		self::$oXESelenium->clickElement('xpath', '//label[@for="lgpl_agree"]');

		// go next
		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
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
		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
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
		self::$oXESelenium->clickElement('xpath', '//label[@for=\'db_type_' . self::$oXESelenium->getInstallDatabase('type') . '\']');
		
                // go next
                self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
	}
	

	/**
	 * @depends testChooseDatabaseType
	 */
	public function testInputDatabaseinformation()
	{
		$act = self::$oXESelenium->getElementValue('xpath', '//form[@id=\'db_data\']/input[@name=\'act\']');
		$this->assertEquals('proc' . ucfirst(self::$oXESelenium->getInstallDatabase('type')) . 'DBSetting', $act);

		self::$oXESelenium->clearElementText('id', 'dbHostName');
		self::$oXESelenium->clearElementText('id', 'dbPort');
		self::$oXESelenium->clearElementText('id', 'dbPrefix');

		self::$oXESelenium->setElementText('id', 'dbHostName', self::$oXESelenium->getInstallDatabase('host'));
		self::$oXESelenium->setElementText('id', 'dbPort', self::$oXESelenium->getInstallDatabase('port'));
		self::$oXESelenium->setElementText('id', 'dbId', self::$oXESelenium->getInstallDatabase('user'));
		self::$oXESelenium->setElementText('id', 'dbPw', self::$oXESelenium->getInstallDatabase('password'));
		self::$oXESelenium->setElementText('id', 'dbName', self::$oXESelenium->getInstallDatabase('database'));
		self::$oXESelenium->setElementText('id', 'dbPrefix', self::$oXESelenium->getXEPreFix());
                
		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
	}
	
	
	/**
	 * @depends testInputDatabaseinformation
	 */
	public function testSettings()
	{
		$url = self::$oXESelenium->getCurrentUrl();
		$this->assertRegExp('/dispInstallConfigForm/', $url);

		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
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
                
		self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
	
	}

	/**
	 * @depends testEnterAdministrator
	 */
	public function testIndex()
	{
                self::$oXESelenium->clickElement('xpath', '//div[@id="installContent"]/div/div/p/a[1]');
		$this->assertEquals('welcome_page', self::$oXESelenium->getTitle());
	}
}
