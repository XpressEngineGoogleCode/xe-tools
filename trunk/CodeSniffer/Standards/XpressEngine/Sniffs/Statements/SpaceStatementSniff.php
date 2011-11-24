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
        return array(
						T_EQUAL, T_DIVIDE, T_BITWISE_AND, T_MULTIPLY, T_MODULUS, T_PLUS, T_MINUS,
						T_BOOLEAN_AND, T_BOOLEAN_OR,
						T_SEMICOLON, T_COMMA,
						T_STRING_CONCAT
					);

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
		if($token['code'] === T_SEMICOLON || $token['code'] === T_COMMA)
		{
			if($tokens[$stackPtr + 1]['code'] !== T_WHITESPACE)
			{
				$error = "Must use space after " . substr($tokens[$stackPtr]['type'], 2) . " : %s";
				$phpcsFile->addError($error, $stackPtr, 'Space', $tokens[$stackPtr - 1]['content'] . $tokens[$stackPtr]['content']);
			}
		}
		else
		{
			if($tokens[$stackPtr - 1]['code'] !== T_WHITESPACE || $tokens[$stackPtr + 1]['code'] !== T_WHITESPACE)
			{
				$error = "Must use space before and after " . substr($tokens[$stackPtr]['type'], 2) . " : %s";
				$phpcsFile->addError($error, $stackPtr, 'Space', $tokens[$stackPtr - 1]['content'] . $tokens[$stackPtr]['content'] . $tokens[$stackPtr + 1]['content']);
			}
		}

    }//end process()

}//end class
