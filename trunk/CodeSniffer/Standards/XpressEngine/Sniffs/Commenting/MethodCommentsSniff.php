<?php
class XpressEngine_Sniffs_Commenting_MethodCommentsSniff implements PHP_CodeSniffer_Sniff
{

	/**
 	 * Returns the token types that this sniff is interested in.
	 *
 	 * @return array(int)
 	 */
	public function register()
	{
		return array(T_FUNCTION);
	}//end register()


	/**
	 * Processes the tokens that this sniff is interested in.
	 *
	 * @param PHP_CodeSniffer_File $phpcsFile The file where the token was found.
	 * @param int                  $stackPtr  The position in the stack where
	 *                                        the token was found.
	 *
	 * @return void
	 */
	public function process(PHP_CodeSniffer_File $phpcsFile, $stackPtr)
	{
		$tokens = $phpcsFile->getTokens();
		$token = $tokens[$stackPtr];
		$functionName = $tokens[$stackPtr + 2]['content'] . '()';

//		print_r($tokens); return;
		
		// get Class Name
		$classPtr = NULL;
		foreach($token['conditions'] as $key => $val)
		{
			$classPtr = $key;
		}

		// figure out METHOD or FUNCTION
		if($classPtr && $tokens[$classPtr]['code'] === T_CLASS) 
		{
			$type = 'METHOD';
			$className = $tokens[$classPtr + 2]['content'];
			$functionName = $className . '::' . $functionName;
		}
		else
		{
			$type = 'FUNCTION';
		}


		// count params
		$countParams = 0;
		for($i = $token['parenthesis_opener']+1; $i < $token['parenthesis_closer']; $i++)
		{
			if($tokens[$i]['code'] === T_VARIABLE) $countParams++;
		}

		// check comment
		$comments = array();
		$ptr = $phpcsFile->findPrevious(T_DOC_COMMENT, $stackPtr - 1);
		if($tokens[$ptr]['line'] + 1 !== $token['line'])
		{
			$error = $type . ' must have a comment : %s';
			$phpcsFile->addError($error, $stackPtr, 'Found', $functionName);
			return;
		}

		// get comment
		while($tokens[$ptr]['code'] === T_DOC_COMMENT)
		{
			$comments[] = trim($tokens[$ptr--]['content']);
		}
		$comments = array_reverse($comments);


		// check comment
		if($comments[0] != "/**")
		{
			$error = $type . ' Comment must start "/**"" : %s';
			$phpcsFile->addError($error, $stackPtr, 'Found', $functionName);
			return;
		}

		if($comments[count($comments)-1] != '*/')
		{
			$error = $type . ' must must finish "*/" : %s';
			$phpcsFile->addError($error, $stackPtr, 'Found', $functionName);
			return;
		}

		$comment = join("\n", $comments);
		$commentParams = array('@return', '@brief', '@developer');
		if($type == 'METHOD')
		{
			$commentParams[] = '@access';
		}
		if($countParams>0)
		{
			$commentParams[] = '@param';
		}

		foreach($commentParams as $val)
		{
			if(strpos($comment, $val) === false)
			{
				$error = $type .' Comment must have '. $val .' : %s';
				$phpcsFile->addError($error, $stackPtr, 'Found', $functionName);
			}
		}

	}//end process()

}//end class
