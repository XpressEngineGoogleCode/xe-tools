<?php
/**
 * PHP_Beautifier filter for XpressEngine CMS.
 * * See XpressEngine coding convention on Google Code Wiki:
 * @link http://code.google.com/p/xe-core/wiki/PHPCodingConvension
 * @link http://code.google.com/p/xe-tools/trunk/Beautifier
 *
 * @category   PHP
 * @package    PHP_Beautifier
 * @subpackage Filter
 * @author     Arnia Software <contact@arnia.ro>
 * @link       http://pear.php.net/package/PHP_Beautifier
 * @link       http://beautifyphp.sourceforge.net
 * @license    http://www.php.net/license/3_0.txt PHP License 3.0
 * @link       http://www.xpressengine.org/
 * @version    Release: 0.1.15
 */

final class NestedStatement
{
    const EXPRESSION = 0;
    const DO_WHILE_STMT = 1;
    const ELSE_STMT = 2;
}

class PHP_Beautifier_Filter_XpressEngine extends PHP_Beautifier_Filter
{
    protected $aSettings =
	array
	    (
		'project_root' => '.',
		'file_name' => NULL
	    );

    protected $sNewLine = "\n";	    // Use Unix line-endings ('\n' characters)

    protected $aFilterTokenFunctions = array(
	T_IF => 't_control_statement',
	T_ELSEIF => 't_control_statement',
	T_ELSE => 't_control_statement',
	T_SWITCH => 't_control_statement',
        T_FOR => 't_control_statement',
	T_FOREACH => 't_control_statement',
	T_WHILE => 't_control_statement',
	T_DO => 't_control_statement',

	T_PUBLIC => 't_check_doc_comment',
	T_PRIVATE => 't_check_doc_comment',
	T_PROTECTED => 't_check_doc_comment',
	T_STATIC => 't_check_doc_comment',
	T_FUNCTION => 't_check_doc_comment',
	T_CLASS => 't_check_doc_comment',

	T_COMMENT => 't_comment',
	T_CONCAT_EQUAL => 't_concat_equal',

	'{' => 't_brace',
	'}' => 't_brace',
	';' => 't_semicolon'
    );

    // A php closing tag at the end of the file will set this to the token
    // number just before the tag, where the end of file comment might be
    // found.
    protected $end_of_file_comment_token_index = -2;
    protected $input_file_name = NULL;

    // Indicates filter class is looking for the opening brace for
    // an if, for, while statement
    private $search_brace = FALSE;
    private $nested_level = 0;

    // Indicates filter class is currently looking for the semicolon to
    // append a closing brace after
    private $add_brace = FALSE;

    private $search_close_brace = FALSE;
    private $brace_nested_level = 0;
    private $do_while_body = FALSE;

    private $switch_stmt_block = FALSE;
    private $switch_brace_level = 0;

    public function handleToken($token)
    {
	// add a space after semicolons (";") in for loops
	$i = $this->oBeaut->iCount;

	if
	    (
		$this->search_brace
		    &&
		$i > 1	// Is iCount 0-based ?
		    &&
		in_array
		    (
			$this->oBeaut->aTokens[$i - 1][0],
			array(';' /*, ',' */)
		    )
	    )
	{
	    $this->oBeaut->add(' ');
	}

	// add a space before operator +=
	if
	    (
		// $i > 1 
		//     &&
		$this->oBeaut->aTokens[$i][0] == T_PLUS_EQUAL
		//     &&
		// $this->oBeaut->aTokens[$i-1][0] != T_WHITESPACE
	    )
	{
	    $this->oBeaut->add(' ');
	}

	// add documentation comment before functions and classes

	// handle all tokens normally
	$ret = parent::handleToken($token);

	// explicitly handle parenthesis, regular token handling does not
	// always work for them
	if ($token[0] == ')' || $token[0] == '(')
	    if (TRUE === $this->t_parenthezis($token[1]))
		$ret = TRUE;

	return $ret;
    }

    /**
     * set new-line type before processing.
     * convert single-line comments to multi-line comments
     */
    public function preProcess()
    {
	$this->oBeaut->sNewLine = $this->sNewLine;
	$this->input_file_name = $this->getSetting("file_name");
	if (!$this->input_file_name)
	    $this->input_file_name = $this->oBeaut->sInputFile;

	foreach ($this->oBeaut->aTokens as &$comment_token)
	    if ($comment_token[0] == T_COMMENT && $comment_token[1][1] == '/')
	    {
		// convert single-line comment to multi-line comment
		$comment_token[1] = preg_replace('#\*( *)/#', '*$1$1 /', $comment_token[1]);
		$comment_token[1] =
		    '/* single-line comment marker: d0dbea90-b3c8-11e1-afa6-0800200c9a66 '
			.
		    rtrim($comment_token[1], "\r\n")
			.
		    " */";
	    }
    }

	/**
	 * Handles IF / DO / WHILE / SWITCH statements
	 */
    // public function t_control($sTag)
    // {
    //     // Remove spaces between if and braket: << if(..) >> instead of << if (..) >>
    //     // phpcs error: Not permit spaces in Condition : if (
    //     $this->oBeaut->removeWhitespace();
    //     $this->oBeaut->addNewLine();
    //     $this->oBeaut->addIndent();
    //     $this->oBeaut->add($sTag);
    // }
    
    /* Disallow braces while looking for the delimiting ';' */
    public function t_brace($sTag)
    {
	if ($this->add_brace)
	{
	    fprintf(STDERR, "Line {$this->oBeaut->aTokens[$this->oBeaut->iCount -1][2]}:{$this->oBeaut->aTokens[$this->oBeaut->iCount +1][2]}: Compound statement found nested directly under an outer control statement.\n");
	    fprintf(STDERR, "Use braces around the nested statement.\n");
	    
	    exit(3);
	}

	if ($this->search_close_brace)
	    if ($sTag == '{')
	    {
		$this->brace_nested_level++;
	    }
	    else
	    {
		$this->brace_nested_level--;

		if ( ! $this->brace_nested_level )
		{
		    $this->search_close_brace = FALSE;
		    $this->do_while_body = TRUE;
		}
	    }

	if ($this->switch_stmt_block)
	    if ($sTag == '{')
		$this->switch_brace_level++;
	    else
		$this->switch_brace_level--;

	switch ($this->oBeaut->getPreviousTokenConstant())
	{
	case T_STRING:
	    switch ($this->oBeaut->getPreviousTokenConstant(2))
	    {
	    case T_EXTENDS:
	    case T_CLASS:
		break 2;
	    }

	case T_LNUMBER:
	case T_DNUMBER:
	case T_OBJECT_OPERATOR:
	case '}':
	case ']':
	case T_VARIABLE:
	    return PHP_Beautifier_Filter::BYPASS;
	}

	$this->oBeaut->removeWhitespace();

	if ($sTag == '{')
	{
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add($sTag);
	    $this->oBeaut->incIndent();

	    // indent switch statements twice
	    if ($this->switch_brace_level == 1)
		$this->oBeaut->incIndent();
	}
	else
	{
	    // dedent switch statements twice
	    if ($this->switch_stmt_block && $this->switch_brace_level == 0)
	    {
		$this->switch_stmt_block = FALSE;
		$this->switch_brace_level = 0;
		$this->oBeaut->decIndent();
	    }

	    $this->oBeaut->decIndent();
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add($sTag);
	}

	$this->oBeaut->addNewLineIndent();
    }

    // invoked after the if/for/while expression, and after do and else
    // keywords
    private function checkNextBraceToken($do_while_body = NestedStatement::EXPRESSION)
    {
	// look for the next non-comment, non-whitespace token
	$i = 1;

	while (in_array($this->oBeaut->getnextTokenConstant($i), array(T_WHITESPACE, T_COMMENT, T_DOC_COMMENT)))
	    $i++;

	if ($this->oBeaut->getNextTokenConstant($i) == '{')
	    // nested if/for/while/do statement starts with brace
	    if ($do_while_body)
	    {
		$this->search_close_brace = TRUE;
		$this->brace_nested_level = 0;

		if ($do_while_body == NestedStatement::ELSE_STMT)
		{
		    $this->oBeaut->removeWhiteSpace();
		    $this->oBeaut->addNewLineIndent();
		    $this->oBeaut->add('else');
		    
		    return TRUE;
		}
	    }
	    else
	    {
		$this->search_brace = FALSE;
		$this->nested_level = 0;
	    }
	else
	{
	    // nested if/for/while/do statement needs brace
	    $this->oBeaut->removeWhiteSpace();

	    switch ($do_while_body)
	    {
	    case NestedStatement::DO_WHILE_STMT:
		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add('do');
		break;

	    case NestedStatement::ELSE_STMT:
		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add('else');
		break;

	    default:
		$this->oBeaut->add(')');
		break;
	    }

	    $this->oBeaut->removeWhiteSpace();
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add('{');
	    $this->oBeaut->incIndent();
	    $this->oBeaut->addNewLineIndent();

	    // start searching for the end of the statement
	    $this->add_brace = TRUE;
	    $this->do_while_body = ($do_while_body == NestedStatement::DO_WHILE_STMT);

	    
	    return TRUE;
	}

	return PHP_Beautifier_Filter::BYPASS;
    }

    public function t_semicolon($sTag)
    {
	if ($this->search_brace)
	    return PHP_Beautifier_Filter::BYPASS;
	else
	{
	    $this->oBeaut->add($sTag);
	    $this->oBeaut->addNewLineIndent();

	    if ($this->add_brace)
	    {
		$this->oBeaut->removeWhitespace();
		$this->oBeaut->decIndent();
		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add("}");
		$this->oBeaut->addNewLineIndent();

		$this->add_brace = false;
		$this->search_brace = false;
	    }
	}
    }

    private function t_parenthezis($sTag)
    {
	if ($this->search_brace)
	    switch ($sTag)
	    {
	    case '(':
		$this->nested_level++;
		break;

	    case ')':
	    //case T_PARENTHESIS_CLOSE:
		$this->nested_level--;

		if ( ! $this->nested_level )
		{
		    $this->search_brace = FALSE;
		    return $this->checkNextBraceToken();
		}

		break;
	    }

	return PHP_Beautifier_Filter::BYPASS;
    }

    /**
     * Handles if, do, while, for and foreach statements
     */
    public function t_control_statement($sTag)
    {
	switch (strtolower($sTag))
	{
	case 'while':
	    if ($this->do_while_body && ! $this->add_brace)
	    {
		// while clause for a do { } while (); statement was found
		$this->do_while_body = FALSE;
		break;
	    }
	    // else
	    //	    fall-through

	case 'switch':
	    if (strlen($sTag) == 6)	// 'switch' has length 6, $sTag might also be 'while' (length 5) in here
	    {
		if ($this->switch_stmt_block)
		{
		    fprintf(STDERR, "Line {$this->oBeaut->aCurrentToken[2]}: Nested '{$sTag}' statements found.\n");

		    exit (6);
		}

		$this->switch_stmt_block = TRUE;
		$this->switch_brace_level = 0;
	    }
	    // no break, fall-through

	case 'if':
	case 'for':
	case 'foreach':
	case 'elseif':
	    if ($this->search_brace || $this->add_brace)
	    {
		fprintf(STDERR, "Line {$this->oBeaut->aCurrentToken[2]}: Nested control statement '$sTag' found, surround it with braces.\n");

		exit(2);
	    }
	    else
	    {
		// start looking for the opening brace
		$this->nested_level = 0;
		$this->add_brace = FALSE;
		$this->search_brace = TRUE;
	    }
	    break;

	case 'else':
	    return $this->checkNextBraceToken(NestedStatement::ELSE_STMT);

	case 'do':
	    if ($this->search_brace || $this->add_brace)
	    {
		fprintf(STDERR, "Line {$this->oBeaut->aCurrentToken[2]}: Nested control statement '$sTag' found, surround it with braces.\n");

		exit(5);
	    }
	    else
		if ($this->search_close_brace || $this->do_while_body)
		{
		    fprintf(STDERR, "Line {$this->oBeaut->aCurrentToken[2]}: Nested do-while statements found, exiting.\n");

		    exit(4);
		}
		else
		    return $this->checkNextBraceToken(NestedStatement::DO_WHILE_STMT);

	    break;
	}

        $this->oBeaut->removeWhitespace();
	$this->oBeaut->addNewLineIndent();
        $this->oBeaut->add($sTag);
    }
    
    /**
     * Handles plain text items
     * Converts php boolean and NULL literals (TRUE, FALSE, NULL)
     * to uppercase, converts class name to uppercase first letter
     */
    public function t_string($sTag)
    {
	// Uppercase true, false and null
	// phpcs error:  Must use uppercase : null
        if
	    (
		$sTag != 'TRUE' and !strcasecmp($sTag, 'true')
		    or
		$sTag != 'FALSE' and !strcasecmp($sTag, 'false')
		    or
		$sTag != 'NULL' and !strcasecmp($sTag, 'null')
	    )
	{
            $this->oBeaut->aCurrentToken[1]=strtoupper($sTag);
        }

	// Class names should be uppercase
	// phpcs error: Must start uppercase on class name
	if($this->oBeaut->getPreviousTokenConstant() == T_CLASS)
	{
	    $this->oBeaut->aCurrentToken[1] = ucfirst($sTag);
	}

	// If class extends another class, its name should be uppercase too
	if($this->oBeaut->getPreviousTokenConstant() == T_EXTENDS)
	{
	    $this->oBeaut->aCurrentToken[1] = ucfirst($sTag);
	}

	// Continue with default behaviour
        return PHP_Beautifier_Filter::BYPASS;
    }
    
	/**
	 * Handles class and method comments
	 */
    
    function t_doc_comment($sTag)
    {
	// Fix doc comment ending line */ instead of **/
	// phpcs error: METHOD must must finish "*/" : wikiView::dispWikiMessage()
	$sTag = str_replace('**/', '*/', $sTag);

	// Add missing doxygen attributes
	//   phpcs error: METHOD Comment must have @return :
	//                METHOD Comment must have @developer :
	// These are added with TODO so that developers can review comment content

	$function_def = FALSE;
	$class_def = FALSE;
	$i = 1;

	if (in_array($this->oBeaut->aCurrentToken[0], array(T_FUNCTION, T_CLASS)))
	{
	    $function_def = ($this->oBeaut->aCurrentToken[0] == T_FUNCTION);
	    $class_def = ! $function_def;
	}
	else
	{
	    while
		(
		    in_array
			(
			    $this->oBeaut->getNextTokenConstant($i),
			    array(T_PUBLIC, T_PRIVATE, T_PROTECTED, T_STATIC, T_COMMENT)
			)
		)
	    {
		// allow for some function definition like:
		//	    public static /* public interface method */ function f2($arg)
		$i++;
	    }

	    $function_def = ($this->oBeaut->getNextTokenConstant($i) == T_FUNCTION);
	    $class_def = ($this->oBeaut->getNextTokenConstant($i) == T_CLASS);
	}

	if($function_def)
	{
	    // If @author is found, replace with @developer
	    if (strpos($sTag, '@author') !== FALSE)
	    {
		    $sTag = str_replace('@author', '@developer', $sTag);
	    }
	    
	    // Split comment into lines
	    $lines = preg_split("/\r\n|\r|\n/", $sTag);

	    // Print first line ( the '/** ')
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add($lines[0]);

	    // Print missing attributes
	    foreach (array('@brief', '@access', '@developer', '@return') as $doc_entry)
		if (strpos($sTag, $doc_entry) === FALSE)
		{
		    $this->oBeaut->addNewLineIndent();
		    $this->oBeaut->add(' * ' . $doc_entry);
		}

	    // Add @ comments for function parameters, if any
	    $indx = $i;

	    while ($this->oBeaut->getNextTokenContent($indx) != '(')
		$indx++;

	    $next_param = NULL;

	    while (($param = $this->oBeaut->getNextTokenContent($indx)) != ')')
	    {
		// Means we have parameters, so lets add comments for them
		if ($next_param && $param[0] == '$')	// skip any explicit references and php5 type hints
		    // found parameter name in the function param list
		    if (preg_match('#@param[[:blank:]]+' . preg_quote($param, '#') . '#', $sTag))
			// param already documented, nothing to do
			$next_param = FALSE;
		    elseif (preg_match('#@param[[:blank:]]+' . preg_quote(substr($param, 1), '#') . '#', $sTag))
		    {
			// param is documented without the starting '$' char for param name
			// replace @param var_name with @param $var_name
			foreach ($lines as &$comment_line)
			    $comment_line = 
				preg_replace
				    (
					'#@param([[:blank:]]+)' . preg_quote(substr($param, 1), '#') . '#',
					'@param$1' . str_replace('$', "\\$", str_replace("\\", "\\\\", $param)),
					$comment_line
				    );

			unset($comment_line);

			$next_param = FALSE;
		    }
		    else
		    {
			// param not documented, add documentation
			$this->oBeaut->addNewLineIndent();
			$this->oBeaut->add(' * @param ' . $param);
			$next_param = FALSE;
		    }
		else
		    if ($param == ',' || $next_param === NULL && $param == '(')
			$next_param = TRUE;

		$indx++;
	    }

	    unset($lines[0]);
	    foreach ($lines as $line)
	    {
		// trim leading spaces from the comment line
		if (preg_match('#^[[:blank:]]*\*/#', $line))
		    $line = ' */';
		else
		    $line = preg_replace('#^[[:blank:]]*(\* ?)?#',' * ',$line);

		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add($line);

		// fputs(STDERR, $line . "\n");
	    }
	    $this->oBeaut->addNewLineIndent();
	}
	else
	    if ($class_def)
	    {
		// If @author is found, replace with @developer
		// phpcs error: Class comment must have @developer
		if (strpos($sTag, '@author') !== FALSE)
		    $sTag = str_replace('@author', '@developer', $sTag);

		// Print comment line by line
		$this->oBeaut->removeWhitespace();
		$this->oBeaut->addNewLineIndent();
		$lines = preg_split("/\r\n|\r|\n/", $sTag);

		$this->oBeaut->add($lines[0]);
		unset($lines[0]);

		foreach (array('@brief', '@developer') as $doc_entry)
		    if (strpos($sTag, $doc_entry) === FALSE)
		    {
			$this->oBeaut->addNewLineIndent();
			$this->oBeaut->add(' * ' . $doc_entry);
		    }

		foreach ($lines as $line)
		{
		    // trim leading spaces from the comment line
		    if (preg_match('#^[[:blank:]]*\*/#', $line))
			$line = ' */';
		    else
			$line = preg_replace('#^[[:blank:]]*(\* ?)?#',' * ',$line);

		    $this->oBeaut->addNewLineIndent();
		    $this->oBeaut->add($line);
		}

		$this->oBeaut->addNewLineIndent();
	    }
	    else
		// Skip this comment and fallback to default behaviour
		return PHP_Beautifier_Filter::BYPASS;
    }

	/**
	 * Handles open braces - {
	 */
    function t_open_brace($sTag)
    {
	// Move opening bracket to next line
	//   phcps error: Must use a start bracket '{' on next line
        //   phpcs error: Not permit to use CLOSE BRACKET '{' on same line of if
	if($this->oBeaut->isPreviousTokenContent(']')){ // Bug fix - 'if($matches[1]{0} == "!")' should not be formatted
	    $this->oBeaut->add($sTag);
	}
	else
	{
	    $this->oBeaut->removeWhitespace();
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add($sTag);
	    $this->oBeaut->incIndent();
	    $this->oBeaut->addNewLineIndent();
	}
    }
    
	/**
	 * Handles close braces - }
	 */
    function t_close_brace($sTag)
    {
	/* Fix indent for closing bracket
		phpcs error: Wrong Indent
	*/
	if($this->oBeaut->isPreviousTokenContent(']', 3) && is_numeric($this->oBeaut->getPreviousTokenContent(4)))
	{
	    // Bug fix - 'if($matches[1]{0} == "!")' should not be formatted
	    $this->oBeaut->add($sTag);
	}
	else
	{
	    if ($this->oBeaut->getMode('string_index') or $this->oBeaut->getMode('double_quote'))
	    {
		$this->oBeaut->add($sTag);
	    }
	    else
	    {
		$this->oBeaut->removeWhitespace();
		$this->oBeaut->decIndent();
		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add($sTag);
		$this->oBeaut->addNewLineIndent();
	    }
	}
    }
    
    /**
     * Handles php close tag: ?>
     */
    function t_close_tag($sTag)
    {
	// Output nothing -> closing tag is removed
	// phpcs error: Using PHP Close Tag
	switch (count($this->oBeaut->aTokens) - $this->oBeaut->iCount)
	{
	case 1:
	    // The closing tag is the last token in the .php source file
	    $this->end_of_file_comment_token_index = $this->oBeaut->iCount - 1;
	    return;
	case 2:
	    // One more token in the source file, after the closing tag
	    $token = $this->oBeaut->aTokens[$this->oBeaut->iCount+1];

	    if
		(
		    is_array($token)
			&&
		    $token[0] == T_INLINE_HTML
			&&
		    trim
			(
			    str_replace
				(
				    '/* blank line marker: 164e5c50-fbfb-4e16-a6fa-1d0640968b33 */',
				    '',
				    $token[1]
				),
			    "\t\r\n\v\f "
			) == ''
		)
	    {
		$this->end_of_file_comment_token_index = $this->oBeaut->iCount - 1;
		return;
	    }
	    // else fall through
	default:
	    return PHP_Beautifier_Filter::BYPASS;
	}

    }

    /**
     * Remove a blank line between the documentation comment and the class/function
     */
    function t_comment($sTag)
    {
	// Check for a blank line (a blank line comment marker) in between a
	// documentation comment and a function or class
	$i = $this->oBeaut->iCount;

	if
	    (
		$sTag == '/* blank line marker: 164e5c50-fbfb-4e16-a6fa-1d0640968b33 */'
		    &&
		(
		    $this->oBeaut->aTokens[$i-1][0] == T_DOC_COMMENT
			||
		    $this->oBeaut->aTokens[$i-1][0] == T_WHITESPACE
			&&
		    $this->oBeaut->aTokens[$i-2][0] == T_DOC_COMMENT
		)
	    )
	{
	    $i = 1;

	    while
		(
		    in_array
			(
			    $this->oBeaut->getNextTokenConstant($i),
			    array(T_PUBLIC, T_PROTECTED, T_PRIVATE, T_STATIC)
			)
		)
	    {
		$i++;
	    }

	    if (in_array($this->oBeaut->getNextTokenConstant($i), array(T_FUNCTION, T_CLASS)))
		// skip the comment from the output source
		// phpcs error: class must have a comment
		return TRUE;
	}

	return PHP_Beautifier_Filter::BYPASS;
    }

    /**
     * Check if function/class has a documentation comment and prepend one if needed.
     */
    function t_check_doc_comment($sTag)
    {
	$i = $this->oBeaut->iCount;

	// search token list backwards for an existing documentation comment
	$j = $i - 1;
	$blank_line_marker = FALSE;
	$token = $this->oBeaut->aTokens[$j];

	while ( $j && $j > $i - 5 && $token[0] != T_DOC_COMMENT)
	{
	    if ($token[0] == T_COMMENT)
		if ($token[1] == '/* blank line marker: 164e5c50-fbfb-4e16-a6fa-1d0640968b33 */')
		    if ($blank_line_marker)
		    {
			$j = -1;
			break;
		    }
		    else
			$blank_line_marker = TRUE;
		else
		{
		    // Non-doc comment found
		    $j = -1;
		    break;
		}
	    elseif ($token[0] == T_WHITESPACE)
		;
	    else
	    {
		$j = -1;
		break;
	    }

	    $j--;
	    $token = $this->oBeaut->aTokens[$j];
	}

	if ($j >= 0 && $j > $i - 5 && $token[0] == T_DOC_COMMENT)
	    // doc comment found within 4 tokens backwards from the current one
	    return PHP_Beautifier_Filter::BYPASS;
	else
	{
	    // no documentation comment
	    // check for a following class or function definition
	    // (member variables also begin with public/private)

	    $def_found =
		in_array
		    (
			$this->oBeaut->aCurrentToken[0],
			array(T_FUNCTION, T_CLASS)
		    );
	    $j =  1;

	    while
		(
		    !$def_found
			&&
		    in_array
			(
			    $this->oBeaut->getNextTokenConstant($j),
			    array
				(
				    T_FUNCTION, T_CLASS, T_PUBLIC,
				    T_PROTECTED, T_PRIVATE, T_STATIC
				)
			) 
		)
	    {
		$def_found =
		    in_array
			(
			    $this->oBeaut->getNextTokenConstant($j),
			    array(T_FUNCTION, T_CLASS)
			);

		$j++;
	    }

	    if ($def_found)
	    {
		$this->t_doc_comment("/**\n*/");
		$this->oBeaut->removeWhitespace();
		$this->oBeaut->addNewLineIndent();
		$this->oBeaut->add($sTag . ' ');    // ??

		return TRUE;
	    }
	    else
		// no class or function definition to be documented
		return PHP_Beautifier_Filter::BYPASS;
	}
    }

    /**
     * Handles post processing - do things after all tags have been processed
     */
    function postProcess()
    {
	// surround php operators with spaces if not already present
	$nTokens = count($this->oBeaut->aTokens);

	$php_operators =
	    array
		(
		    // this should be only a partial list of php operators
		    '+=', '-=', '*=', '/=', '.=', '%=', '&=', '|=', '^=', '^=',
		    '+= ', '-= ', '*= ', '/= ', '.= ', '%= ', '&= ', '|= ',
		    '^= ', '<<=', '>>=', '=>', 'and', 'or', 'xor', '.', '*', '/',
		    '%', '>>', '<<', '<', '<=', '==', '===', '!==',
		    '!=', '|', '^', '&&', '||', '?'
		);

	for ($i = 0; $i < $nTokens; $i++)
	{
	    $token_text = $this->oBeaut->getTokenAssocText($i);

	    if (in_array($token_text, $php_operators))
	    {
		$new_token_text = $token_text;

		if ($i && !preg_match('[[:space:]]$', $this->oBeaut->getTokenAssocText($i-1)))
		    $token_text = ' ' . $token_text;

		if
		    (
			$i < $nTokens - 1
			    &&
			!preg_match('^[[:space:]]', $this->oBeaut->getTokenAssocText($i+1))
		    )
		{
		    $token_text .= ' ';
		}

		if ($new_token_text != $token_text)
		    $this->oBeaut->replaceTokenAssoc($i, $token_text);
	    }
	}

	// append the end-of-file comment
	//
	// three comment lines that need to be added
	$add_vi_comment = true;
	$add_file_comment = true;
	$add_location_comment = true;

	if ($this->end_of_file_comment_token_index == -2)
	    $this->end_of_file_comment_token_index = count($this->oBeaut->aTokens) - 1;

	// Check for any existing end-of-file comment lines
	while
	    (
		$this->end_of_file_comment_token_index >= 0
		    &&
		in_array($this->oBeaut->aTokens[$this->end_of_file_comment_token_index][0], array(T_WHITESPACE, T_COMMENT))
	    )
	{
	    if ($this->oBeaut->aTokens[$this->end_of_file_comment_token_index][0] == T_COMMENT)
	    {
		$matches = array();

		if
		    (
			preg_match
			    (
				'+^/[*/] (Location: |End of file |vi:)+',
				$this->oBeaut->aTokens[$this->end_of_file_comment_token_index][1],
				$matches
			    )
		    )
		{
		    switch ($matches[1])	// first captured parenthesized expression
		    {
		    case 'Location: ':
			$add_location_comment = FALSE;
			break;
		    case 'End of file ':
			$add_file_comment = FALSE;
			break;
		    case 'vi:':
			$add_vi_comment = FALSE;
			break;
		    }
		}
	    }

	    $this->end_of_file_comment_token_index--;
	}

	// Add end of file comments
	$current_path_info = pathinfo(realpath($this->input_file_name));
	$file_name = $current_path_info['basename'];

	if ($add_vi_comment)
	{
	    $this->oBeaut->addNewLine();
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add("/* vi:set noet ts=4 sts=4 sw=4 ft=php ff=unix: */");
	    $this->oBeaut->addNewLine();
	}

	if ($add_file_comment)
	{
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add("/* End of file $file_name */");
	}

	if ($add_location_comment)
	{
	    // Get the current input file relative to the project root
	    // (to be included in the comment)
	    $project_root = realpath($this->getSetting('project_root'));
	    $path_components = array();

	    while
		(
		    array_key_exists('basename', $current_path_info)
			&&
		    $current_path_info['basename']
			&&
		    $current_path_info['dirname'] != $project_root
		)
	    {
		$path_components[] = $current_path_info['basename'];
		$current_path_info = pathinfo($current_path_info['dirname']);
	    }

	    if ($current_path_info['dirname'] == $project_root)
	    {
		$path_components[] = $current_path_info['basename'];
		$file_location = implode('/', array_reverse($path_components));
	    }
	    else
		$file_location = $this->input_file_name;

	    // write file location comment
	    $this->oBeaut->addNewLineIndent();
	    $this->oBeaut->add("/* Location: $file_location */");
	}

	$this->oBeaut->removeWhitespace();
	$this->oBeaut->addNewLine();
	
	// convert single-line comment markers back to single-line
	// comments
	$out_count = count($this->oBeaut->aOut);
	foreach ($this->oBeaut->aOut as $no => &$token_string)
	    if
		(
		    preg_match
			(
			    '#^/\* single-line comment marker: d0dbea90-b3c8-11e1-afa6-0800200c9a66 //.* \*/#',
			    $token_string
			)
		)
	    {
		if
		    (
			$no < $out_count -1
			    && 
			preg_match('#^[[:blank:]]*\r?\n#', $this->oBeaut->aOut[$no+1])
		    )
		{
		    // comment is followed by an end-of-line, no need to add another
		    $include_eol = false;
		}
		else
		    $include_eol = true;

		$token_string =
		    preg_replace
			(
			    '#^/\* single-line comment marker: d0dbea90-b3c8-11e1-afa6-0800200c9a66 //(.*) \*/#',
			    '//$1' . ($include_eol ? "\n" : ''),
			    $token_string
			);

		$token_string = preg_replace('#\*( *)\1 /#', '*$1/', $token_string);
	    }
    }
}
/* vi: set ft=php binary noendofline: */
?>