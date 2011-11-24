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

		if(!$token['scope_opener'])
		{
			$error = "Not permit to omit bracket '{}'";
			$phpcsFile->addError($error, $stackPtr, 'Bracket');
			return;
		}

		if($tokens[$token['parenthesis_closer']]['line'] + 1 != $tokens[$token['scope_opener']]['line'])
		{
			$error = "Not permit to use OPEN BRACKET '{' on same line of CLOSE PARENTHESIS ')'";
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
