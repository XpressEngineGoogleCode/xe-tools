<?php

/**
 * @brief XML Property Loader Class
 * @developer sol
 * @date 2011-12-20
 */
class XE_Selenium_PropertyXMLLoader extends XE_Selenium_PropertyLoader
{
	private $propertyFile;
	private $oXMLHelper;
	protected $xePrefix = NULL;

	public function __construct($propertyFile = 'selenium.properties')
	{
		$this->propertyFile = $propertyFile;
		if(!file_exists($this->propertyFile))
		{
			$this->propertyFile = $_ENV['PWD'] . '/' . $this->propertyFile;
		}

		if(!file_exists($this->propertyFile))
		{
			throw new Exception('there is no property file');
		}
	}

	public function load()
	{	
		if(!$this->oXMLHelper)
		{
			$this->oXMLHelper = new XMLHelper();
			$this->oXMLHelper->loadFromFiile($this->propertyFile);
		}
	}

	public function getXEPrefix()
	{
		if($this->xePrefix === NULL)
		{
			$datbaseInfo = $this->getInstallDatabase();

			$prefix =  $this->oXMLHelper->configInstallDatabasePrefix('body');
			if($prefix)
			{
				$this->xePrefix = $prefix;
			}
			else
			{
				$this->xePrefix = substr(str_shuffle(str_repeat(join('', range('a', 'z')), 5)), 0, 6);
			}
		}
		
		return $this->xePrefix;
	}

	public function setXEPrefix($prefix)
	{
		$this->xePrefix = $prefix;
	}

	private function _replaceXEPrefix($str)
	{
		if(strpos($str, '%%PREFIX%%') !== FALSE && $this->getXEPrefix())
		{
			return str_replace('%%PREFIX%%', $this->getXEPrefix(), $str);
		}

		return $str;
	}

	public function getSeleniumHost()
	{
		return $this->oXMLHelper->configSeleniumHost('body');
	}

	public function getSeleniumPort()
	{
		return $this->oXMLHelper->configSeleniumPort('body');
	}

	public function getSeleniumWebdriver()
	{
		$webdriver = $this->oXMLHelper->configSeleniumWebdriver('body');
		if(!$webdriver)
		{
			return 'firefox';
		}

		return $webdriver;
	}

	public function getSiteUrl()
	{
		$url = $this->_replaceXEPrefix($this->oXMLHelper->configSiteUrl('body'));
		if(substr($url, -1) == '/')
		{
			$url = substr($url, 0, -1);
		}

		return $url;
	}

	public function hasInstall()
	{
		return $this->oXMLHelper->configInstall() ? TRUE : FALSE;
	}

	public function getSiteAdmin()
	{
		return XMLHelper::toArray($this->oXMLHelper->configSiteAdmin());
	}

	public function getInstallPath()
	{
		$path = $this->_replaceXEPrefix($this->oXMLHelper->configInstallPath('body'));
		if(substr($path, 0, 2) == './')
		{
			$path = dirname($this->propertyFile) . substr($path, 1);
		}

		if(substr($path, -1) == '/')
		{
			$path = substr($path, 0, -1);
		}

		return $path;
	}

	public function getInstallDatabase()
	{
		return XMLHelper::toArray($this->oXMLHelper->configInstallDatabase());
	}

	public function getRepositories()
	{
		$info = array();
		$oRepositories = $this->oXMLHelper->configInstallRepositories();
		if($oRepositories && count($oRepositories->children() > 0))
		{
			foreach($oRepositories->children() as $repositpory)
			{
				$info[$this->getInstallPath() . (string)$repositpory->path] = (string)$repositpory->url;
			}
		}

		return $info;
	}
}

/* End of file XE_Selenium_PropertyXMLLoader.php */
/* Location: ./libs/XE_Selenium_PropertyXMLLoaderr */
