<?php

class XpressEngine_Sniffs_Statements_SpaceStatementSniff implements PHP_CodeSniffer_Sniff
{

    /**
     * Returns an array of tokens this test wants to listen for.
     *
     * @return array
     */
    public function register()
    {
        return array(T_EQUAL, T_SEMICOLON, T_COMMA);

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

		// check if(
		if($token['code'] === T_EQUAL)
		{
			if($tokens[$stackPtr-1]['code'] !== T_WHITESPACE || $tokens[$stackPtr+1]['code'] !== T_WHITESPACE)
			{
				$error = "Must use spaces before and after EQUAL";
				$phpcsFile->addError($error, $stackPtr, 'Using Space');
			}
		}
		else if($token['code'] === T_SEMICOLON)
		{
			if($tokens[$stackPtr+1]['code'] !== T_WHITESPACE)
			{
				$error = "Must use spaces after SEMICOLON";
				$phpcsFile->addError($error, $stackPtr, 'Using Space');
			}
		}
		else if($token['code'] === T_COMMA)
		{
			if($tokens[$stackPtr+1]['code'] !== T_WHITESPACE)
			{
				$error = "Must use spaces after COMMA";
				$phpcsFile->addError($error, $stackPtr, 'Using Space');
			}
		}

    }//end process()

}//end class
