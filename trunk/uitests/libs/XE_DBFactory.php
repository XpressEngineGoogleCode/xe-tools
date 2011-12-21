<?php

/**
 * @brief DB Factory Class
 * @developer sol
 * @date 2011-12-20
 */
class XE_DBFactory
{
	private $oDBHelper;

	public function __construct(XE_Selenium_PropertyLoader $oPropertyLoader)
	{
		$databaseInfo = $oPropertyLoader->getInstallDatabase();
		$className = sprintf('XE_DB%sHelper', strtoupper($databaseInfo['type']));
		include_once dirname(__FILE__) . '/'  . $className . '.php';

		$this->oDBHelper = new $className();
		$this->oDBHelper->setPropertyLoader($oPropertyLoader);
	}

	public function dropData()
	{
		$this->oDBHelper->connect();
		return $this->oDBHelper->dropData();
	}
}

/* End of file XE_DBFactory.php */
/* Location: ./libs/XE_DBFactory.php */
