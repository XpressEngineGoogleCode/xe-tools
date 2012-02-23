<?php
	/**
     * @class  $PARAM_MODULE_NAMEAdminController
     * @author $PARAM_MODULE_AUTHOR_NAME ($PARAM_MODULE_AUTHOR_EMAIL_ADDRESS)
     * @brief  $PARAM_MODULE_NAME module of the admin controller class
     **/
	class $PARAM_MODULE_NAMEAdminController extends $PARAM_MODULE_NAME {
		/**
         * @brief Initialization
         **/
        function init() {
        }
		
		/**
         * @brief Add a $PARAM_MODULE_NAME_UC
         **/
        function proc$PARAM_MODULE_NAME_UCAdminInsert() {
            // Create model/controller object of the module module
            $oModuleController = &getController('module');
            $oModuleModel = &getModel('module');
            // Set the module information parameters
            $args = Context::getRequestVars();
            $args->module = '$PARAM_MODULE_NAME';
            $args->mid = $args->$PARAM_MODULE_NAME_name;	//because if mid is empty in context, set start page mid
            unset($args->$PARAM_MODULE_NAME_name);

			if($args->use_mobile != 'Y') $args->use_mobile = '';
            // Check if an original module exists by using module_srl
            if($args->module_srl) {
				$columnList = array('module_srl');
                $module_info = $oModuleModel->getModuleInfoByModuleSrl($args->module_srl, $columnList);
                if($module_info->module_srl != $args->module_srl) {
					unset($args->module_srl);
				}
				else
				{
					foreach($args as $key=>$val)
					{
						$module_info->{$key} = $val;
					}
					$args = $module_info;
				}
            }

            // Insert/update depending on module_srl
            if(!$args->module_srl) {
                $output = $oModuleController->insertModule($args);
                $msg_code = 'success_registed';
            } else {
                $output = $oModuleController->updateModule($args);
                $msg_code = 'success_updated';
            }

            if(!$output->toBool()) return $output;

            $this->add('module_srl',$output->get('module_srl'));
            $this->setMessage($msg_code);
			if(!in_array(Context::getRequestMethod(),array('XMLRPC','JSON'))) {
				$returnUrl = Context::get('success_return_url') ? Context::get('success_return_url') : getNotEncodedUrl('', 'module', 'admin', 'module_srl', $output->get('module_srl'), 'act', 'disp$PARAM_MODULE_NAME_UCAdminInfo');
				header('location:'.$returnUrl);
				return;
			}
        }
		
		 /**
         * @brief Modify a $PARAM_MODULE_NAME_UC
         **/
		function proc$PARAM_MODULE_NAME_UCAdminUpdate()
		{
			$this->proc$PARAM_MODULE_NAME_UCAdminInsert();
		}
		
		/**
         * @brief Delete a $PARAM_MODULE_NAME_UC
         **/
        function proc$PARAM_MODULE_NAME_UCAdminDelete() {
            $module_srl = Context::get('module_srl');
            // Get an original
            $oModuleController = &getController('module');
            $output = $oModuleController->deleteModule($module_srl);
            if(!$output->toBool()) return $output;

            $this->add('module','$PARAM_MODULE_NAME');
            $this->setMessage('success_deleted');
			if(!in_array(Context::getRequestMethod(),array('XMLRPC','JSON'))) {
				$returnUrl = Context::get('success_return_url') ? Context::get('success_return_url') : getNotEncodedUrl('', 'module', 'admin', 'module_srl', $output->get('module_srl'), 'act', 'disp$PARAM_MODULE_NAME_UCAdminInfo');
				header('location:'.$returnUrl);
				return;
			}
        }
	}
?>