<?php


class XpressEngine_Sniffs_ControlStructures_ControlSignatureSniff implements PHP_CodeSniffer_Sniff
{

    /**
     * Returns an array of tokens this test wants to listen for.
     *
     * @return array
     */
    public function register()
    {
        return array(T_IF, T_ELSEIF, T_FOR, T_FOREACH, T_WHILE, T_SWITCH);

    }//end register()


    /**
     * Processes this test, when one of its tokens is encountered.
     *
     * @param PHP_CodeSniffer_File $phpcsFile The file being scanned.
     * @param int                  $stackPtr  The position of the current token
     *                                        in the stack passed in $tokens.
     *
     * @return void
     */
    public function process(PHP_CodeSniffer_File $phpcsFile, $stackPtr)
    {
        $tokens = $phpcsFile->getTokens();
        $token = $tokens[$stackPtr];
//print_r($tokens); exit;
		$closeBracket = $token['parenthesis_closer'];
		$next = $phpcsFile->findNext(T_OPEN_CURLY_BRACKET, $closeBracket+1);
		if(!$next) return;

		$closeBracketLine = $tokens[$closeBracket]['line'];
		if($closeBracketLine != $tokens[$next]['line'] && $closeBracketLine != $tokens[$next]['line']-1)
		{
			return;
		}

		$count = 0;
		$newlineCount = 0;
		for($i = $closeBracket+1; $i < $next; $i++)
		{
			if($tokens[$i]['code'] === T_WHITESPACE)
			{
				if($tokens[$i]['content'] == "\n")
				{
					$newlineCount++;
				}
			}
			else
			{
				$count++;
			}
		}

		if($count > 0)
		{
			return;
		}

		if($newlineCount == 0)
		{
			$error = "New Line(\\n) must be used bewteen CLOSE PARENTHESIS ')' and OPEN BRACKET '{'";
			$phpcsFile->addError($error, $closeBracket+1, 'NewLine');
			return;
		}

		$opener = $token['scope_opener'];
		$closer = $token['scope_closer'];

		if(($token['column'] !== $tokens[$opener]['column']) || ($token['column'] !== $tokens[$closer]['column']))
		{
			$error = "Wrong Indent";
			$phpcsFile->addError($error, $closeBracket+1, 'Indent');
		}

    }//end process()

}//end class
