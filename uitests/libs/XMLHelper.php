<?php
/**
 * @brief XML Helper for getting body or attributes of XML esaily.
 *        <xml>
 *			<parent>
 * 				<children>
 *					<child sex="male">sol</child>
 *				</children>
 *			</parent>
 *		</xml>
 * 		$oXMLHelper->xmlParantChildrenChild('body') == sol
 * 		$oXMLHelper->xmlParantChildrenChild('@sex') == male
 * @developer sol
 * @date 2011-12-20
 */
class XMLHelper
{
	protected $oXML;

	/**
	 * @brief load from xml file
	 * @access public
	 * @param $xmlFile : the path of xml file 
	 * @return void 
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function loadFromFiile($xmlFile)
	{
		$this->oXML = simplexml_load_file($xmlFile);
	}

	/**
	 * @brief load from xml string
	 * @access public
	 * @param $xml : the content of xml
	 * @return void 
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function loadFromString($xml)
	{
		$this->oXML = simplexml_load_string($xml);
	}

	/**
	 * @brief this is magic method for getting body or attribute of xml easily
	 * @access public
	 * @param $methodName
	 * @param $arguments : { body | @attribute name }
	 * @return String
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function __call($methodName, $arguments)
	{
		if(!$this->oXML)
		{
			return FALSE;
		}

		$nodes = explode('*', preg_replace('/([A-Z])/', '*\1', $methodName));
		for($i = 0, $c = count($nodes), $oNode = $this->oXML; $i < $c; $i++)
		{
			$node = strtolower($nodes[$i]);
			if($oNode->getName() != $node)
			{
				return FALSE;
			}

			if($i >= $c - 1)
			{
				if(count($arguments) ==  1)
				{
					if($arguments[0]{0} == '@')
					{
						return $oNode->attributes()->{substr($arguments[0], 1)};
					}
					
					if($arguments[0] == 'body')
					{
						return trim((string)$oNode);
					}
				}

				return $oNode;
			}

			$oNode = $oNode->{strtolower($nodes[$i + 1])};
		}
	}

	/**
	 * @brief get the array that include the body of children 
	 * @access public
	 * @param $oNode : SimpleXMLElement
	 * @return Array 
	 * @developer sol
	 * @date 2011-12-20
	 */
	public static function toArray(SimpleXMLElement $oNode)
	{
		$node = array();
		$oChildren = $oNode->children();
		if(count($oChildren) > 0)
		{
			foreach($oChildren as $child)
			{
				$node[$child->getName()] = (string)$child;
			}
		}

		return $node;
	}

	/**
	 * @brief get string from this object
	 * @access public
	 * @return String : XML
	 * @developer sol
	 * @date 2011-12-20
	 */
	public function toString()
	{
		return $this->oXML->asXML();
	}
}

/* End of file XMLHelper.php */
/* Location: ./XMLHelper.php */
