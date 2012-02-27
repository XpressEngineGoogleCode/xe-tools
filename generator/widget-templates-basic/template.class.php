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
            return TemplateHandler::getInstance()->compile($this->widget_path . 'skins/' . $args->skin,'$PARAM_WIDGET_NAME');
        }
    }
?>
