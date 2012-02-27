<?php
    /**
     * @class		$PARAM_WIDGET_NAME
     * @author		$PARAM_WIDGET_AUTHOR_NAME ($PARAM_WIDGET_AUTHOR_EMAIL_ADDRESS)
     * @version 	$PARAM_WIDGET_VERSION
     * @brief		$PARAM_WIDGET_DESCRIPTION
     **/

    class $PARAM_WIDGET_NAME extends WidgetHandler
    {
        /**
         * @brief	Widget execution
         * Get extra_vars declared in ./widgets/$PARAM_WIDGET_NAME/conf/info.xml as arguments
         * After generating the result, do not print but return it.
         **/
        function proc($args)
		{
			Context::set('caption', $args->caption);

			$firefox_visits = executeQuery('widgets.$PARAM_WIDGET_NAME.getFirefoxBrowser');
			if ($firefox_visits->toBool())
				foreach ($firefox_visits->data as $key => $val)
				{
					Context::set('firefox_visits', $val);
					break;
				}
			else
				Context::set('firefox_visits', '');

			$chrome_visits  = executeQuery('widgets.$PARAM_WIDGET_NAME.getChromeBrowser');
			if ($chrome_visits->toBool())
				foreach ($chrome_visits->data as $key => $val)
				{
					Context::set('chrome_visits', $val);
					break;
				}
			else
				Context::set('chrome_visits', '');

			$msie_visits    = executeQuery('widgets.$PARAM_WIDGET_NAME.getIEBrowser');
			if ($msie_visits->toBool())
				foreach ($msie_visits->data as $key => $val)
				{
					Context::set('msie_visits', $val);
					break;
				}
			else
				Context::set('msie_visits', '');

			$other_visits   = executeQuery('widgets.$PARAM_WIDGET_NAME.getOtherBrowsers');
			if ($other_visits->toBool())
				foreach ($other_visits->data as $key => $val)
				{
					Context::set('other_visits', $val);
					break;
				}
			else
				Context::set('other_visits', '');

			$total_visits = Context::get('firefox_visits')+Context::get('chrome_visits')+Context::get('msie_visits')+Context::get('other_visits');

			Context::set('total_visits', $total_visits);
            Context::set('colorset', $args->colorset);

            return TemplateHandler::getInstance()->compile($this->widget_path . 'skins/' . $args->skin,'$PARAM_WIDGET_NAME');
        }
    }
?>
