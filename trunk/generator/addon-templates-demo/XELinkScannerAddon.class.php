<?php

/**
 * @file	$PARAM_ADDON_NAME.addon.php
 * @author	$PARAM_ADDON_AUTHOR_NAME ($PARAM_ADDON_AUTHOR_EMAIL_ADDRESS)
 * @brief	Scans HTML mark-up for <a> elements with external
 *			links and configures them to open in a new window.
 **/

    class XELinkScannerAddon
    {
		function ProcessPage($page_markup)
		{
			// they say loadHTML() throws some warnings all over for
            // non-validating markup, so use @ here
			$doc = @DOMDocument::loadHTML($page_markup);

			foreach ($doc->getElementsByTagName('a') as $anchor_element)
			if($anchor_element->hasAttribute('href') && $anchor_element->getAttribute('href') &&
				(! $anchor_element->hasAttribute('onclick') || preg_match(
				'/^[[:space:]]*window[[:space:]]*\.[[:space:]]*open[[:space:]]*\([[:space:]]*this[[:space:]]*\.[[:space:]]*href[[:space:]]*\)[[:space:]]*;[[:space:]]*return[[:space:]]+false[[:space:]]*;?[[:space:]]*$/',
				$anchor_element->getAttribute('onclick')) ) && (! $anchor_element->hasAttribute('target') ||
			   $anchor_element->getAttribute('target') == '_blank'))
			{
				$href = $anchor_element->getAttribute('href');
				$matches = array();

				if(strncasecmp($href, '//', strlen('//')) == 0 ||
					strncasecmp($href, 'http://', strlen('http://')) == 0 ||
					strncasecmp($href, 'https://', strlen('https://')) == 0 ||
					strncasecmp($href, 'ftp://', strlen('ftp://')) == 0)
				{
					if (strncmp($href, '//', strlen('//')) == 0)
						$href = 'http:' . $href;	    // parse_url() wants an absolute URL

					$remote_host = parse_url($href, PHP_URL_HOST);
					$external_link = true;

					if ($remote_host == $_SERVER['SERVER_NAME'])
						$external_link = false;
					else
						if(array_key_exists('HTTP_HOST', $_SERVER) && $remote_host == $_SERVER['HTTP_HOST'])
						{
							$external_link = false;
						}

					// Port number could also be checked here, in case a
					// different port on the current web server is to be
					// considered an external (remote) site
				
					if ($external_link)
					{
						if ($anchor_element->hasAttribute('class'))
							$anchor_element->setAttribute('class',$anchor_element->getAttribute('class') . ' external-link');
						else
							$anchor_element->setAttribute('class', 'external-link');

						$anchor_element->setAttribute('onclick','window.open(this.href); return false;');

						// deprecated by HTML 4.0 strict
						$anchor_element->removeAttribute('target');
					}
				}
			}

			$html_output = $doc->saveHTML();

			// remove the <!DOCTYPE ...> declaration if any
			if ($html_output)
			{
				if (strncasecmp($html_output, "<!DOCTYPE ", strlen("<!DOCTYPE ")) === 0)
				{
					$idx = strpos($html_output, '>');

					if ($idx !== FALSE)
					{
						$idx++;

						while($idx < strlen($html_output) &&
								($html_output[$idx] == "\n" || $html_output[$idx] == "\r"))
						{
							$idx++;
						}

						$html_output = substr($html_output, $idx);
					}
				}
			}
			else
				$html_output = $page_markup;

			return $html_output;
		}
    }
?>
