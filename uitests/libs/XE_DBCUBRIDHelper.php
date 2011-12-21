<?php

/**
 * @brief DB Helper Class for XE CUBRID
 * @developer sol
 * @date 2011-12-20
 */
class XE_DBCUBRIDHelper extends XE_DBHelper
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
		$this->conn = cubrid_connect($databaseInfo['host'], $databaseInfo['port'], $databaseInfo['database'], $databaseInfo['user'], $databaseInfo['password']);

		register_shutdown_function(array($this, 'close'));
	}

	public function close()
	{
		if($this->conn)
		{
			cubrid_disconnect($this->conn);
			unset($this->conn);
		}
	}

	public function dropData()
	{
		$prefix = $this->oPropertyLoader->getXEPrefix();

		$result = cubrid_execute($this->conn, 'select class_name from "_db_class" where "class_name" like \'' . $prefix . '%\'');
		$sql = 'drop table "%s"';
		while($row = cubrid_fetch_row($result))
		{
			cubrid_execute($this->conn, sprintf($sql, $row[0]));
		}

		$result = cubrid_execute($this->conn, 'select name from "db_serial" where "name" like \'' . $prefix . '%\'');
		$sql = 'drop serial "%s"';
		while($row = cubrid_fetch_row($result))
		{
			cubrid_execute($this->conn, sprintf($sql, $row[0]));
		}

		cubrid_commit($this->conn);
	}
}

/* End of file XE_DBCUBRIDHelper.php */
/* Location: ./libs/XE_DBCUBRIDHelper.php */
