<?php
/**
 * PHP_Beautifier filter for 
 *	- adding braces for all if, else, for and foreach statements.
 * 	- adding comment stubs to methods and functions which don't have any
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
class PHP_Beautifier_Filter_AddMissingBracesAndComments extends PHP_Beautifier_Filter
{
    protected $aFilterTokenFunctions = array(
		T_IF => 't_check_braces',
		T_FOR => 't_check_braces',
		T_FOREACH => 't_check_braces',
		T_ELSE => 't_check_braces_without_paranthesis',
		T_FUNCTION => 't_check_comment',
		T_CLASS => 't_check_comment'
    );
	
	private $openBracketsToAdd = array();
	private $closeBracketsToAdd = array();
	private $commentsToAdd = array();

	function preProcess(){
		$this->openBracketsToAdd = array();
		$this->closeBracketsToAdd = array();
		$this->commentsToAdd = array();
	}
	
	function t_check_comment($sTag){
		if($this->oBeaut->getPreviousTokenConstant() != T_DOC_COMMENT)
			$this->commentsToAdd[] = $this->oBeaut->iCount;
	
		return PHP_Beautifier_Filter::BYPASS;   
	}
	
	/**
	* Handles if, for and foreach statements
	* which use paranthesis that need to be skipped in order
	* to find out where to put the open brace;
	*/
	function t_check_braces($sTag){	
		// Check for opening bracket
		$i = 2;
		$parCount = 1; // Parenthesis count
		while($i < 100){
			if($this->oBeaut->isNextTokenContent('(', $i))  { $parCount++; } 
			if($this->oBeaut->isNextTokenContent(')', $i)) { $parCount--; } 
					
			if($parCount === 0){
				if($this->oBeaut->isNextTokenContent('{', $i + 1)){ // already has curly braces, so nothing to do here
					break;
				}
				else { // Save positions where braces need to be inserted
					$this->openBracketsToAdd[] = $this->getNextTokenIndex($i); 
					$i++;
					while(!$this->oBeaut->isNextTokenContent(';', $i)){
						$i++;
					}
					$this->closeBracketsToAdd[] = $this->getNextTokenIndex($i); 
					break;
				}
			}
			
			$i++;
		}		
		
		return PHP_Beautifier_Filter::BYPASS;   
	}
	
	/**
	* Handles else statement
	* Else doesn't use paranthesis, so code is much simpler
	*/
	function t_check_braces_without_paranthesis($sTag){	
		if(!$this->oBeaut->isNextTokenContent('{')){
			$this->openBracketsToAdd[] = $this->oBeaut->iCount;
			$i = 1;
			while(!$this->oBeaut->isNextTokenContent(';', $i)){
				$i++;
			}
			$this->closeBracketsToAdd[] = $this->getNextTokenIndex($i); 
		}
		
		return PHP_Beautifier_Filter::BYPASS;   
	}	
	
    /**
     * Get the 'x' significant (non whitespace) next token
     * @param  int   current+x token
     * @return array
     */
    private function getNextTokenIndex($iNext = 1) 
    {
        for ($x = $this->oBeaut->iCount+1 ; $x < (count($this->oBeaut->aTokens) -1) ; $x++) {
            $aToken = &$this->oBeaut->getToken($x);
            if ($aToken[0] != T_WHITESPACE) {
                $iNext--;
                if (!$iNext) {
                    return $x;
                }
            }
        }
    }		
	
	/**
	* Handles post processing - do things after all tags have been processed
	*/
	function postProcess(){
		 $count = count($this->openBracketsToAdd);
		 for( $i = 0; $i < $count; $i++){
			$text = $this->oBeaut->getTokenAssocText($this->openBracketsToAdd[$i]);
			$text .= ' { ';
			$this->oBeaut->replaceTokenAssoc($this->openBracketsToAdd[$i], $text);
			$text = $this->oBeaut->getTokenAssocText($this->closeBracketsToAdd[$i]);
			$text .= ' } ';			
			$this->oBeaut->replaceTokenAssoc($this->closeBracketsToAdd[$i], $text);
		 }
		 
		 $comment_count = count($this->commentsToAdd);
		 for( $i = 0; $i < $comment_count; $i++){
			$text = $this->oBeaut->getTokenAssocText($this->commentsToAdd[$i]);
			$text = '/** ' . PHP_EOL . '  * // TODO Add comment for method/class ' . PHP_EOL .  ' */' . PHP_EOL . $text;			
			$this->oBeaut->replaceTokenAssoc($this->commentsToAdd[$i], $text);
		 }
	}
	
}
?>
