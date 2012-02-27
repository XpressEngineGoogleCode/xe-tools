<?php
	/**
	 * @class  $PARAM_MODULE_NAMEController
	 * @author $PARAM_MODULE_AUTHOR_NAME ($PARAM_MODULE_AUTHOR_EMAIL_ADDRESS)
	 * @brief  Controller for the $PARAM_MODULE_NAME module
	 **/
	class $PARAM_MODULE_NAMEController extends $PARAM_MODULE_NAME {
		/**
		 * @brief Initialization
		 **/
		function init() {
		}
		
		/**
		 * @brief proc $PARAM_MODULE_NAME insert email
		 **/
		function proc$PARAM_MODULE_NAME_UCInsertEmail(){
			/**
             * Get request vars
             **/
			$args = Context::getRequestVars();
			/**
             * Insert values into table
             **/
			$output = executeQueryArray('$PARAM_MODULE_NAME.insert$PARAM_MODULE_NAME_UCEmail',$args);
			/**
             * Set redirect url and error message
             **/
			if ($output->toBool()) 
				$msg_code = 'success_registed';
			else
			{
				$this->setError(-1);
				$msg_code = 'msg_error_occured';
			}

            $this->setMessage($msg_code);
			if(!in_array(Context::getRequestMethod(),array('XMLRPC','JSON'))) {
				$returnUrl = Context::get('success_return_url') ? Context::get('success_return_url') : getNotEncodedUrl('', 'mid', Context::get('mid'), 'act', 'disp$PARAM_MODULE_NAME_UCContent');
				header('location:'.$returnUrl);
				return;
			}
			
		}
	}
?>
