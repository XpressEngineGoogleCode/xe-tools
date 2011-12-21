<?php
include_once '../libs/__init__.php';

class XEJoinTest extends PHPUnit_Framework_TestCase
{
	protected static $oXESelenium;
	protected static $property = './xe.property';
	protected static $errorScreenFile = './join.error.png';
	protected static $userInfo;

	public static function setUpBeforeClass()
	{
		$ran1 = str_shuffle(str_repeat(join('', range('a','z')), 10));
		$ran2 = str_shuffle(str_repeat(join('', range(0, 9)), 10));
		$userId = substr($ran1, 0, 6) . substr($ran2, 0, 3);

		self::$userInfo = array();
		self::$userInfo['userId'] = $userId;
		self::$userInfo['nickName'] = self::$userInfo['userId'];
		self::$userInfo['email'] = self::$userInfo['userId'] . '@admin.net';
		self::$userInfo['password'] = 'qwerty';
		self::$userInfo['question'] = 'answer';

		// load and copy xe file
		self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(self::$property));
		self::$oXESelenium->install(new XE_Selenium_InstallLocalHelper());
		self::$oXESelenium->connect();
		sleep(2);
		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div/div/div/form/fieldset/ul/li/a');
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

	public function testJoin()
	{
		$this->assertRegExp('/dispMemberSignUpForm/', self::$oXESelenium->getCurrentUrl());

		self::$oXESelenium->setElementText('name', 'email_address', self::$userInfo['email']);
		self::$oXESelenium->setElementText('xpath', '/html/body/div/div[2]/div[2]/div/form/ul/li[2]/p[2]/input', self::$userInfo['password']);
		self::$oXESelenium->setElementText('xpath', '/html/body/div/div[2]/div[2]/div/form/ul/li[3]/p[2]/input', self::$userInfo['password']);
		self::$oXESelenium->setElementText('name', 'nick_name', self::$userInfo['nickName']);
		self::$oXESelenium->setElementText('xpath', '/html/body/div/div[2]/div[2]/div/form/ul/li[6]/div/input', self::$userInfo['question']);

		self::$oXESelenium->clickElement('xpath', '/html/body/div/div[2]/div[2]/div/form/div/span/input');

		$src = self::$oXESelenium->getPageSource();
		$this->assertRegExp('/Registered successfully/', $src);
	}

	/**
	 * @depends testJoin
	 */
	public function testLogin()
	{
		self::$oXESelenium->setElementText('name', 'user_id', self::$userInfo['email']);
		self::$oXESelenium->setElementText('name', 'password', self::$userInfo['password']);
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
