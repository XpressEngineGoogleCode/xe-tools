<?php
include_once dirname(__FILE__) . '../../../libs/__init__.php';

class XEAdminBaseTest extends PHPUnit_Framework_TestCase
{
    protected static $oXESelenium;
    protected static $property = 'install.mysql.properties';

    public static function setUpBeforeClass()
    {
        // load and copy file
        self::$oXESelenium = new XE_Selenium(new XE_Selenium_PropertyXMLLoader(dirname(__FILE__) . '/' .static::$property));
        self::$oXESelenium->install(new XE_Selenium_InstallLocalHelper());
        self::runXEInstallationScreens();
    }

    public static function tearDownAfterClass()
    {
        // remove file and DB
        self::$oXESelenium->uninstall();
    }

    private static function runXEInstallationScreens()
    {
        // -- Test copied
        // go to the XE
        self::$oXESelenium->connect();
        // go next
        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test acceptance of terms
        // agreement
        self::$oXESelenium->clickElement('xpath', '//label[@for="lgpl_agree"]');
        // go next
        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test check install condition
        // go next
        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test check database type
        // select db
        self::$oXESelenium->clickElement('xpath', '//label[@for=\'db_type_' . self::$oXESelenium->getInstallDatabase('type') . '\']');
        // go next
        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test input database information
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
        sleep(2);
        // -- Test settings
        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test enter administrator
        self::$oXESelenium->clearElementText('id', 'aId');

        self::$oXESelenium->setElementText('id', 'aMail', self::$oXESelenium->getSiteAdmin('email'));
        self::$oXESelenium->setElementText('id', 'aPw1', self::$oXESelenium->getSiteAdmin('password'));
        self::$oXESelenium->setElementText('id', 'aPw2', self::$oXESelenium->getSiteAdmin('password'));
        self::$oXESelenium->setElementText('id', 'aId', self::$oXESelenium->getSiteAdmin('userid'));

        self::$oXESelenium->clickElement('xpath', '//a[@id="goNext"]');
        sleep(2);
        // -- Test index
        self::$oXESelenium->clickElement('xpath', '//div[@id="installContent"]/div/div/p/a[1]');
    }
}