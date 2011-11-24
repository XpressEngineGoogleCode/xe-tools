<?php

class XpressEngine_Sniffs_Classes_PropertyNameSniff implements PHP_CodeSniffer_Sniff
{

    /**
     * Returns an array of tokens this test wants to listen for.
     *
     * @return array
     */
    public function register()
    {
        return array(T_VAR, T_PUBLIC, T_PRIVATE, T_PROTECTED);

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
		$next = $phpcsFile->findNext(T_VARIABLE, $stackPtr + 1);
			
		// remove $
		$propertyName = substr($tokens[$next]['content'], 1);

		if(!preg_match('/^_*[a-z]/', $propertyName))
		{
			$error = "Must start a lowercase on property name : %s";
			$phpcsFile->addError($error, $next, 'Property Name', $tokens[$next]['content']);
		}

    }//end process()

}//end class
