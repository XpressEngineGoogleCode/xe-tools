<?php
/**
 * PHP_Beautifier filter for XpressEngine cms.
 * 
 * See XpressEngine coding convention on Google Code Wiki:
 * http://code.google.com/p/xe-core/wiki/PHPCodingConvension
 * 
 * @category   PHP
 * @package    PHP_Beautifier
 * @subpackage Filter
 * @author     Jesús Espino <jespinog@gmail.com>
 * @copyright  2010 Jesús Espino
 * @link       http://pear.php.net/package/PHP_Beautifier
 * @link       http://beautifyphp.sourceforge.net
 * @license    http://www.php.net/license/3_0.txt PHP License 3.0
 * @version    Release: 0.1.15
 */
class PHP_Beautifier_Filter_XpressEngine extends PHP_Beautifier_Filter
{
    protected $aFilterTokenFunctions = array(
        T_FOR => 't_for',
		T_FOREACH => 't_for'
    );

	/**
	* Handles IF / DO / WHILE / SWITCH statements
	*/
    public function t_control($sTag)
    {
		// Remove spaces between if and braket: << if(..) >> instead of << if (..) >>
		// phpcs error: Not permit spaces in Condition : if (
        $this->oBeaut->removeWhitespace();
		$this->oBeaut->addNewLine();
		$this->oBeaut->addIndent();
        $this->oBeaut->add($sTag);		
    }
	
	/**
	* Handles for and foreach statements
	*/
    public function t_for($sTag)
    {
		// Remove spaces between if and braket: << if(..) >> instead of << if (..) >>
		// phpcs error: Not permit spaces in Condition : if (
        $this->oBeaut->removeWhitespace();
		$this->oBeaut->addNewLine();
		$this->oBeaut->addIndent();
        $this->oBeaut->add($sTag);		
    }	
	
	/**
	* Handles plain text items
	*/ 
    public function t_string($sTag) {
		// Uppercase true, false and null
		// phpcs error:  Must use uppercase : null
        if($sTag=='true' or $sTag=='false' or $sTag=='null') {
            $this->oBeaut->aCurrentToken[1]=strtoupper($sTag);
        }
		// Class names should be uppercase
		// phpcs error: Must start uppercase on class name
		if($this->oBeaut->getPreviousTokenConstant() == T_CLASS){		
			$this->oBeaut->aCurrentToken[1]=ucfirst($sTag);
		}
		// If class extends another class, its name should be uppercase too
		if($this->oBeaut->getPreviousTokenConstant() == T_EXTENDS){		
			$this->oBeaut->aCurrentToken[1]=ucfirst($sTag);
		}		
		// Continue with default behaviour
        return PHP_Beautifier_Filter::BYPASS;        
    }		
	
	/**
	* Handles class and method comments
	*/ 
    function t_doc_comment($sTag) 
    {			
		// Fix doc comment ending line */ instead of **/
		// phpcs error: METHOD must must finish "*/" : wikiView::dispWikiMessage()
		$sTag = str_replace('**/', '*/', $sTag);
	
		// Add missing doxygen attributes
		//   phpcs error: METHOD Comment must have @return : 
		//                METHOD Comment must have @developer :
		// These are added with TODO so that developers can review comment content
		
		if($this->oBeaut->getNextTokenConstant() == T_FUNCTION){		
			// If @author is found, replace with @developer
			if(strpos($sTag, '@author') !== false){
				$sTag = str_replace('@author', '@developer', $sTag);
			}		
			
			// Split comment into lines
			$lines = preg_split("/\r\n|\r|\n/", $sTag);
			
			// Print first line
			$this->oBeaut->addNewLineIndent();
			$this->oBeaut->add($lines[0]);
			
			// Print missing attributes
			if(strpos($sTag, '@access') === false){
				$this->oBeaut->addNewLineIndent();
				$this->oBeaut->add(' * @access // TODO Add access for ' . $this->oBeaut->getNextTokenContent(2));
			}				
			if(strpos($sTag, '@developer') === false){
				$this->oBeaut->addNewLineIndent();
				$this->oBeaut->add(' * @developer // TODO Add developer name for ' . $this->oBeaut->getNextTokenContent(2));			
			}		
			if(strpos($sTag, '@return') === false){
				$this->oBeaut->addNewLineIndent();
				$this->oBeaut->add(' * @return // TODO Add return type for ' . $this->oBeaut->getNextTokenContent(2));						
			}
			
			// Add @ comments for function parameters, if any
			$indx = 1;
			while($this->oBeaut->getNextTokenContent($indx) != '(') $indx++;
			if(($param = $this->oBeaut->getNextTokenContent($indx+1)) != ')'){ // Means we have parameters, so lets add comments for them
				if(strpos($sTag, $param) === false){
					$this->oBeaut->addNewLineIndent();
					$this->oBeaut->add(' * @param ' . $param .' // TODO Add param info for ' . $param);											
				}
			}
			
			unset($lines[0]);
			foreach($lines as $line){
				$line = str_replace("\t",'',$line);
				$this->oBeaut->addNewLineIndent();				
				$this->oBeaut->add($line);							
			}
			$this->oBeaut->addNewLineIndent();
		}
		else if($this->oBeaut->getNextTokenConstant() == T_CLASS){		
			// If @author is found, replace with @developer
			// phpcs error: Class Comment must have @developer
			if(strpos($sTag, '@author') !== false){
				$sTag = str_replace('@author', '@developer', $sTag);
			}	
			
			// Print comment line by line
			$this->oBeaut->removeWhitespace();
			$this->oBeaut->addNewLineIndent();
			$lines = preg_split("/\r\n|\r|\n/", $sTag);
			foreach($lines as $line)
			{
				$this->oBeaut->add($line);
				$this->oBeaut->addNewLineIndent();	
			}
		}
		else // Skip this comment and fallback to default behaviour
			return PHP_Beautifier_Filter::BYPASS;        
    }		
	
	/**
	* Handles open braces - {
	*/
    function t_open_brace($sTag) 
    {
		// Move opening bracket to next line
	    //   phcps error: Must use a start bracket '{' on next line
        //   phpcs error: Not permit to use CLOSE BRACKET '{' on same line of if
		if($this->oBeaut->isPreviousTokenContent(']')){ // Bug fix - 'if($matches[1]{0} == "!")' should not be formatted
			$this->oBeaut->add($sTag);
		}
		else {
			$this->oBeaut->addNewLineIndent();
			$this->oBeaut->add($sTag);
			$this->oBeaut->incIndent();
			$this->oBeaut->addNewLineIndent();
		}		
    }		
	
	/**
	* Handles close braces - }
	*/	
    function t_close_brace($sTag) 
    {
		/* Fix indent for closing bracket
			phpcs error: Wrong Indent
		*/
		if($this->oBeaut->isPreviousTokenContent(']', 3) && is_numeric($this->oBeaut->getPreviousTokenContent(4))){ // Bug fix - 'if($matches[1]{0} == "!")' should not be formatted			
			$this->oBeaut->add($sTag);
		}
		else {
			if ($this->oBeaut->getMode('string_index') or $this->oBeaut->getMode('double_quote')) {
				$this->oBeaut->add($sTag);
			} else {
				$this->oBeaut->removeWhitespace();
				$this->oBeaut->decIndent();
				$this->oBeaut->addNewLineIndent();
				$this->oBeaut->add($sTag);
				$this->oBeaut->addNewLineIndent();
			}
		}		
    }		
	
	/**
	* Handles else statement
	*/
    function t_else($sTag) 
    {
		// Fix else indent - else should be on new line
		// phpcs error: Wrong Indent
        if (!$this->oBeaut->isPreviousTokenConstant(T_COMMENT)) {
            $this->oBeaut->removeWhitespace();
            $this->oBeaut->addNewLineIndent();
        }
        $this->oBeaut->add($sTag . ' ');
    }		
	
	/**
	* Handles php close tag: ?>
	*/
	function t_close_tag($sTag){
		// Output nothing -> closing tag is removed
		// phpcs error: Using PHP Close Tag 
	}

	/**
	* Handles post processing - do things after all tags have been processed
	*/
	function postProcess(){
		 $current_file = $this->oBeaut->sInputFile;
		 $parts = explode('/', $current_file);
		 if($parts[0] == $current_file) $parts = explode('\\', $current_file);
		 $file_name = $parts[count($parts) - 1];
		 
		 $this->oBeaut->removeWhitespace();
		 $this->oBeaut->addNewLineIndent();
		 $this->oBeaut->add("/* End of file $file_name */");
		 $this->oBeaut->addNewLineIndent();
		 // TODO Allow customization of file path - depending on wheter beautifier is called for a module or for an entire XE
		 $this->oBeaut->add("/* Location: $current_file */");		 
		 $this->oBeaut->addNewLineIndent();
	}
	
}
?>
