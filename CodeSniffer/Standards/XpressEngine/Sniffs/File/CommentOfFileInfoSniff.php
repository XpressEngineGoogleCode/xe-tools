<?php
class XpressEngine_Sniffs_File_CommentOfFileInfoSniff implements PHP_CodeSniffer_Sniff
{
	static $isStart = FALSE;

    /**
     * Returns the token types that this sniff is interested in.
     *
     * @return array(int)
     */
    public function register()
    {
        return array(T_OPEN_TAG);
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
		if(self::$isStart) return;
		self::$isStart = TRUE;

        $tokens = $phpcsFile->getTokens();
		$countTokens = count($tokens);

		if($tokens[$countTokens-1]['code'] == T_WHITESPACE 
				&& $tokens[$countTokens-2]['code'] == T_COMMENT 
				&& $tokens[$countTokens-3]['code'] == T_WHITESPACE 
				&& $tokens[$countTokens-4]['code'] == T_COMMENT)
		{
			$content = trim($tokens[$countTokens-4]['content']);
			if(strpos($content, '/* End of file ') === FALSE)
			{
				$error = 'Comment of File Info on End of Line : %s';
				$phpcsFile->addError($error, $countTokens-4, 'Found', $content);
			}

			$content = trim($tokens[$countTokens-2]['content']);
			if(strpos($content, '/* Location: ') === FALSE)
			{
				$error = 'Comment of File Info on End of Line : %s';
				$phpcsFile->addError($error, $countTokens-2, 'Found', $content);
			}
		}
		else 
		{
			$content = trim($tokens[$countTokens-1]['content']);
			$error = 'Comment of File Info on End of Line';
			$phpcsFile->addError($error, $countTokens-1, 'Found', $content);
		}

		return;

    }//end process()

}//end class
