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
        return array(T_IF, T_ELSE, T_ELSEIF, T_FOR, T_FOREACH, T_WHILE, T_SWITCH);

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
		$checkBeforeCloseBracketLine = FALSE;
		$checkOpenBracketLine = FALSE;
		$checkBracketIndent = FALSE;


		$isElseIf = FALSE;

		// check 'else if' : else if, elseif 
		if($stackPtr > 3 && $token['code'] == T_IF && $tokens[$stackPtr - 2]['code'] == T_ELSE)
		{
			$isElseIf = T_IF;

			$prev = $phpcsFile->findPrevious(T_CLOSE_CURLY_BRACKET, $stackPtr-1);

			$checkOpenBracketLine = array($tokens[$token['parenthesis_closer']], $tokens[$token['scope_opener']]);
			$checkBeforeCloseBracketLine = array($token, $tokens[$prev]);

		}
		else if($token['code'] == T_ELSE && $tokens[$stackPtr + 2]['code'] == T_IF)
		{
			$isElseIf = T_ELSE;

			$prev = $phpcsFile->findPrevious(T_CLOSE_CURLY_BRACKET, $stackPtr-1);
			$checkBeforeCloseBracketLine = array($token, $tokens[$prev]);
		}
		else if($token['code'] == T_ELSEIF)
		{
			$isElseIf = T_ELSEIF;

			$prev = $phpcsFile->findPrevious(T_CLOSE_CURLY_BRACKET, $stackPtr-1);

			$checkOpenBracketLine = array($tokens[$token['parenthesis_closer']], $tokens[$token['scope_opener']]);
			$checkBeforeCloseBracketLine = array($token, $tokens[$prev]);
		}


		if(!$isElseIf)
		{
			$checkOpenBracketLine = array($tokens[$token['parenthesis_closer']], $tokens[$token['scope_opener']]);
		}

		if($isElseIf === T_IF)
		{
			$prev = $phpcsFile->findPrevious(T_ELSE, $stackPtr - 1);
			$checkBracketIndent = array($tokens[$prev]['column'],
										$token['scope_opener']['column'],
										$token['scope_closer']['column']);
		}
		else if($isElseIf === T_ELSE)
		{
			$next = $phpcsFile->findPrevious(T_IF, $stackPtr + 1);
			$nextToken = $tokens[$next];
			$checkBracketIndent = array($token, 
										$tokens[$nextToken['scope_opener']],
										$tokens[$nextToken['scope_closer']]);
		}
		else
		{
			$checkBracketIndent = array($token, 
										$tokens[$token['scope_opener']],
										$tokens[$token['scope_closer']]);
		}



		if($checkBeforeCloseBracketLine)
		{
			if($checkBeforeCloseBracketLine[0]['line'] - 1 != $checkBeforeCloseBracketLine[1]['line'])
			{
				$error = "Not permit to use CLOSE BRACKET '}' on same line of " . $token['content'];
				$phpcsFile->addError($error, $stackPtr, 'NewLine');

				return;
			}

		}


		if($checkOpenBracketLine)
		{
			if($checkOpenBracketLine[0]['line'] + 1 != $checkOpenBracketLine[1]['line'])
			{
				$error = "Not permit to use CLOSE BRACKET '{' on same line of " . $token['content'];
				$phpcsFile->addError($error, $stackPtr, 'NewLine');
				return;
			}
		}
		
		if($checkBracketIndent)
		{
			if($checkBracketIndent[0]['column'] !== $checkBracketIndent[1]['column'])
			{
				$error = "Wrong Indent";
				$phpcsFile->addError($error, $stackPtr, 'Indent');
			}

			if($checkBracketIndent[0]['column'] !== $checkBracketIndent[2]['column'])
			{
				$error = "Wrong Indent";
				$phpcsFile->addError($error, $stackPtr, 'Indent');
			}
		}


    }//end process()

}//end class
