<?php
    /**
     * @class  $PARAM_MODULE_NAMEView
     * @author $PARAM_MODULE_AUTHOR_NAME ($PARAM_MODULE_AUTHOR_EMAIL_ADDRESS)
     * @brief  View class of the $PARAM_MODULE_NAME module
     **/
    class $PARAM_MODULE_NAMEView extends $PARAM_MODULE_NAME {
        /**
         * @brief Initialization
         **/
        function init() {
			/**
             * set the template path for module
             **/
        	$template_path = sprintf("%sskins/default/",$this->module_path);
        	$this->setTemplatePath($template_path);
        }
		
		/**
         * @brief display $PARAM_MODULE_NAME content
         **/
    	function disp$PARAM_MODULE_NAME_UCContent() {
    		/**
             * get and set module message to be displayed
             **/
    		$module_message = $this->module_info->module_message;
    		Context::set('module_message', $module_message);
    		/**
             * get and set module_srl to be displayed
             **/
    		$module_srl = $this->module_info->module_srl;
    		Context::set('module_srl', $module_srl);
    		
            /**
             * set template file
             **/
        	$this->setTemplateFile('$PARAM_MODULE_NAME_index');
        }
        
        function disp$PARAM_MODULE_NAME_UCEmailList(){
        	/**
             * set module_srl for the query arguments
             **/
        	$args->module_srl = $this->module_info->module_srl;
        	/**
             * execute query
             **/
        	$output = executeQueryArray("$PARAM_MODULE_NAME.getEmails",$args);
        	/**
             * set template and email_list
             **/
        	Context::set('email_list',$output->data);
        	$this->setTemplateFile('$PARAM_MODULE_NAME_email_list');
        }
    }
?>
