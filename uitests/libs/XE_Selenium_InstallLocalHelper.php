<?php

/**
 * @brief Helper Class for installing(copying) XE
 * @developer sol
 * @date 2011-12-20
 */
class XE_Selenium_InstallLocalHelper extends XE_Selenium_InstallHelper
{
	private $oPropertyLoader;

	public function setPropertyLoader(XE_Selenium_PropertyLoader $oPropertyLoader)
	{
		$this->oPropertyLoader = $oPropertyLoader;
		$this->oPropertyLoader->load();
	}

	public function install()
	{
		$this->_svn();
		$this->_chmod();
	}

	public function uninstall()
	{
		$this->_deletePath();

		$oDBFactory = new XE_DBFactory($this->oPropertyLoader);
		$oDBFactory->dropData();
	}

	private function _deletePath()
	{
		$path = $this->oPropertyLoader->getInstallPath();
		if(file_exists($path . '/LICENSE'))
		{
			$this->_exec(sprintf('rm -rf %s', $path));
		}
	}

	private function _svn()
	{
		$oRepositories = $this->oPropertyLoader->getRepositories();
		foreach($oRepositories as $path => $url)
		{
			$type = '';
			if(file_exists($path))
			{
				$type = 'update';
			}
			else
			{
				$type = 'checkout';
			}

			$this->_exec(sprintf('svn %s %s %s', $type, $this->_escapeshellarg($url), $this->_escapeshellarg($path)));
		}
	}

	private function _chmod()
	{
		$path = $this->oPropertyLoader->getInstallPath();
		$this->_exec(sprintf('chmod 707 %s', $this->_escapeshellarg($path)));
	}

	private function _exec($command)
	{
		exec($command);
	}

	private function _escapeshellarg($arg)
	{
		return escapeshellarg($arg);
	}
}

/* End of file XE_Selenium_InstallLocalHelper.php */
/* Location: ./libs/XE_Selenium_InstallLocalHelper */
