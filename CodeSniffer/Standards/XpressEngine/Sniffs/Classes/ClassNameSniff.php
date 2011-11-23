<?php

class XpressEngine_Sniffs_Classes_ClassNameSniff implements PHP_CodeSniffer_Sniff
{

    /**
     * Returns an array of tokens this test wants to listen for.
     *
     * @return array
     */
    public function register()
    {
        return array(T_CLASS);

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

	//	print_r($tokens); exit;


		if($tokens[$stackPtr+2]['code'] !== T_STRING) return;
		
		$first = substr($tokens[$stackPtr+2]['content'],0,1);
		if(!preg_match('/^[A-Z]/',$first))
		{
			$error = "Must start uppercase on class name";
			$phpcsFile->addError($error, $stackPtr+2, 'Class Name');
		}

    }//end process()

}//end class
