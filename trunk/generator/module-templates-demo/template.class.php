<?php
    /**
     * @class  $PARAM_MODULE_NAME
     * @author $PARAM_MODULE_AUTHOR_NAME ($PARAM_MODULE_AUTHOR_EMAIL_ADDRESS)
     * @brief  base class for $PARAM_MODULE_NAME module 
     **/
    class $PARAM_MODULE_NAME extends ModuleObject {

        /**
         * @brief Actions to be performed on module installation
         **/
        function moduleInstall() {
			$oDB = &DB::getInstance();

            $oDB->addIndex("$PARAM_MODULE_NAME_emails","idx_module_$PARAM_MODULE_NAME_emails","module_srl",true);
            return new Object();
        }

        /**
         * @brief Checks if the module needs to be updated
         **/
        function checkUpdate() {
			$oDB = &DB::getInstance();
        	if(!$oDB->isColumnExists("$PARAM_MODULE_NAME_emails","email_srl")) return true;
            return false;
        }

        /**
         * @brief Updates module
         **/
        function moduleUpdate() {
            return new Object(0,'success_updated');
        }

        /**
         * @brief Re-generates the cache file
         **/
        function recompileCache() {
        }
    }
?>
