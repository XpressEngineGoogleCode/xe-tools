<?php

/**
 * @brief DB Helper Class for XE MySQL
 * @developer sol
 * @date 2011-12-20
 */
class XE_DBMYSQLHelper extends XE_DBHelper
{
	private $conn;
	private $oPropertyLoader;

	public function setPropertyLoader(XE_Selenium_PropertyLoader $oPropertyLoader)
	{
		$this->oPropertyLoader = $oPropertyLoader;
	}

	public function connect()
	{
		$databaseInfo = $this->oPropertyLoader->getInstallDatabase();
		$this->conn = mysql_connect($databaseInfo['host'] . ':' . $databaseInfo['port'], $databaseInfo['user'], $databaseInfo['password']);
		mysql_query('set names utf8', $this->conn);
		mysql_select_db($databaseInfo['database'], $this->conn);

		register_shutdown_function(array($this, 'close'));
	}

	public function close()
	{
		if($this->conn)
		{
			mysql_close($this->conn);
			unset($this->conn);
		}
	}

	public function dropData()
	{
		$prefix = $this->oPropertyLoader->getXEPrefix();
		$result = mysql_query("show tables like '" . $prefix . "%'");
		$sql = 'drop tables `%s`';
		while($row = mysql_fetch_row($result))
		{
			mysql_query(sprintf($sql, $row[0]), $this->conn);
		}

		mysql_free_result($result);
	}
}

/* End of file XE_DBMYSQLHelper.php */
/* Location: ./libs/XE_DBMYSQLHelper.php */
