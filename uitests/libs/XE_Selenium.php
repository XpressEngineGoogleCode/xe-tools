<?php
/**
 * @brief Helper Class for Selenium with XE
 * @developer sol
 * @date 2011-12-20
 */
class XE_Selenium
{
	private $oPropertyLoader;
	private $oWebdriver;
	private $oInstallHelper;

	/**
	 * @brief Constructor
	 * @access public
	 * @param $oPropertyLoader : XE_Selenium_PropertyLoader
	 * @return void
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function __construct(XE_Selenium_PropertyLoader $oPropertyLoader)
	{
		$this->oPropertyLoader = $oPropertyLoader;
		$this->oPropertyLoader->load();

		register_shutdown_function(array($this, 'close'));
	}

	/**
	 * @brief Selenium Connect
	 * @access public
	 * @return void
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function connect()
	{
		try
		{
			$this->oWebdriver = new WebDriver($this->oPropertyLoader->getSeleniumHost(), $this->oPropertyLoader->getSeleniumPort());
			$this->oWebdriver->connect($this->oPropertyLoader->getSeleniumWebdriver());
			$this->oWebdriver->get($this->oPropertyLoader->getSiteUrl());
		}
		catch(Exception $e)
		{
			throw new Exception('check property file', 0, $e);
		}
	}

	/**
	 * @brief Selenium Close, It will call self.
	 * @access public
	 * @return void
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function close()
	{
		if($this->oWebdriver)
		{
			$this->oWebdriver->close();
			unset($this->oWebdriver);
		}
	}

	/**
	 * @brief XE Pre install, copy files and change permission. 
	 * @access public
	 * @param $oInstallHelper : XE_Selenium_InstallHelper
	 * @return void
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function install(XE_Selenium_InstallHelper $oInstallHelper)
	{
		$this->oInstallHelper = $oInstallHelper;
		$this->oInstallHelper->setPropertyLoader($this->oPropertyLoader);
		$this->oInstallHelper->install();
	}

	/**
	 * @brief remove XE files and DB datas
	 * @access public
	 * @return BOOL
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function uninstall()
	{
		if(!$this->oInstallHelper)
		{
			return FALSE;
		}

		return $this->oInstallHelper->uninstall();
	}

	/**
	 * @brief return phpwebdriver object.
	 * @access public
	 * @return object 
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getWebdriver()
	{
		return $this->oWebdriver;
	}

	/**
	 * @brief return phpwebdriver object.
	 * @access public
	 * @return object 
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getSiteUrl()
	{
		return $this->oPropertyLoader->getSiteUrl();
	}

	/**
	 * @brief go to the url on Selenium Test 
	 * @access public
	 * @param $url : string url
	 * @return viod
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function goUrl($url)
	{
		$this->oWebdriver->get($url);
	}

	/**
	 * @brief go to the admin page
	 * @access public
	 * @return viod
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function goAdminPage()
	{
		$this->oWebdriver->get($this->oPropertyLoader->getSiteUrl() . '/admin');
	}

	/**
	 * @brief get database info
	 * @access public
	 * @param $field : string { user | password | database | host }
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getInstallDatabase($field = NULL)
	{
		$database = $this->oPropertyLoader->getInstallDatabase();
		if($field)
		{
			return $database[$field];
		}
		return $database;
	}

	/**
	 * @brief get xe admin info
	 * @access public
	 * @param $field : string { userid | nickname | password | email }
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getSiteAdmin($field = NULL)
	{
		$admin = $this->oPropertyLoader->getSiteAdmin();
		if($field)
		{
			return $admin[$field];
		}
		return $admin;
	}

	/**
	 * @brief get xe db prefix
	 * @access public
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getXEPrefix()
	{
		return $this->oPropertyLoader->getXEPrefix();
	}

	/**
	 * @brief set xe db prefix
	 * @access public
	 * @param $prefix 
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function setPrefix($prefix)
	{
		return $this->oPropertyLoader->setSiteUrl($prefix);
	}

	/**
	 * @brief get current test url
	 * @access public
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getCurrentUrl()
	{
		return $this->oWebdriver->getCurrentUrl();
	}

	/**
	 * @brief get html title of current test url
	 * @access public
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getTitle()
	{
		return $this->oWebdriver->getTitle();
	}

	/**
	 * @brief get html source of current test url
	 * @access public
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getPageSource()
	{
		return $this->oWebdriver->getPageSource();
	}

	/**
	 * @brief get html source of current test url
	 * @access public
	 * @param $script : javascript 
	 * @param $script_args : I don`t know.
	 * @return Bool
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function executeScript($script, $script_args = array())
	{
		return $this->oWebdriver->executeScript($script, $script_args);
	}
	
	/**
	 * @brief save screen shot.
	 * @access public
	 * @param $png_filename : image file name, include exetension
	 * @return void
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getScreenshotAndSaveToFile($png_filename)
	{
		$this->oWebdriver->getScreenshotAndSaveToFile($png_filename);
	}

	/**
	 * @brief get a type for finding html element
	 * @access private
	 * @param $type : { name | id | xpath | cssSelector }
	 * @return defined value
	 * @developer sol
	 * @date 2011-12-20
	 */
	private function _getType($type)
	{
		$types = array('name' => LocatorStrategy::name, 'id' => LocatorStrategy::id, 'xpath' => LocatorStrategy::xpath, 'cssSelector' => LocatorStrategy::cssSelector); 
		if(array_key_exists($type, $types))
		{
			return $types[$type];
		}
		return NULL;
	}

	/**
	 * @brief get Selenium Html Element
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @return object, phpwebdriver element
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getElement($type, $value)
	{
		$oElement = $this->oWebdriver->findElementBy($this->_getType($type), $value);
		return $oElement;
	}

	/**
	 * @brief get the text of Html Element
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getElementText($type, $value)
	{
		$oElement = $this->getElement($type, $value);
		return $oElement->getText();
	}

	/**
	 * @brief set the text of Html Input and so on
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @param $text : the text that you want
	 * @return object, phpwebdriver element
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function setElementText($type, $value, $text)
	{
		$oElement = $this->getElement($type, $value);
		$oElement->sendKeys(array($text));
		return $oElement;
	}
	
	/**
	 * @brief clear the text of Html Input and so on
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @return object, phpwebdriver element
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function clearElementText($type, $value)
	{
		$oElement = $this->getElement($type, $value);
		$oElement->clear();
		return $oElement;
	}

	/**
	 * @brief click the Html element
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @return object, phpwebdriver element
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function clickElement($type, $value)
	{
		$oElement = $this->getElement($type, $value);
		$oElement->click();
		return $oElement;
	}

	/**
	 * @brief get the value of element
	 * @access public
	 * @param $type : { name | id | xpath | cssSelector }
	 * @param $value : name, xpath or css selector
	 * @return string
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function getElementValue($type, $value)
	{
		$oElement = $this->getElement($type, $value);
		return $oElement->getValue();
	}
}

/* End of file XE_Selenium.php */
/* Location: ./libs/XE_Selenium.php */
