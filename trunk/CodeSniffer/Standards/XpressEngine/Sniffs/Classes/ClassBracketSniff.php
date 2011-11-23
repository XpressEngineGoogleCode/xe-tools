<?php

class XpressEngine_Sniffs_Classes_ClassBracketSniff implements PHP_CodeSniffer_Sniff
{

    /**
     * Returns an array of tokens this test wants to listen for.
     *
     * @return array
     */
    public function register()
    {
        return array(T_CLASS, T_FUNCTION);

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

		$next = $phpcsFile->findNext(T_OPEN_CURLY_BRACKET,$stackPtr+1);
		if(!$next) return;

		if($tokens[$next]['line'] == $token['line'])
		{
			$error = "Must use start bracket '{' on next line";
			$phpcsFile->addError($error, $next, 'Using Bracket on Class and Method');
		}


		if($tokens[$next]['column'] != $token['column'])
		{
			$error = "Must use same Indent";
			$phpcsFile->addError($error, $next, 'Using Bracket on Class and Method');
		}

		$closer = $tokens[$next]['bracket_closer'];
		if($tokens[$closer]['column'] != $token['column'])
		{
			$error = "Must use same Indent";
			$phpcsFile->addError($error, $next, 'Using Bracket on Class and Method');
		}

    }//end process()

}//end class
