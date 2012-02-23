<?php
    /**
     * @class  $PARAM_MODULE_NAMEAdminView
     * @author $PARAM_MODULE_AUTHOR_NAME ($PARAM_MODULE_AUTHOR_EMAIL_ADDRESS)
     * @brief  $PARAM_MODULE_NAME admin view of the module class
     **/
    class $PARAM_MODULE_NAMEAdminView extends $PARAM_MODULE_NAME {
        /**
         * @brief Initialization
         **/
        function init() {
			// Pre-check if module_srl exists. Set module_info if exists
            $module_srl = Context::get('module_srl');
            // Create module model object
            $oModuleModel = &getModel('module');
            // module_srl two come over to save the module, putting the information in advance
            if($module_srl) {
                $module_info = $oModuleModel->getModuleInfoByModuleSrl($module_srl);
                if(!$module_info) {
                    Context::set('module_srl','');
                    $this->act = 'list';
                } else {
                    ModuleModel::syncModuleToSite($module_info);
                    $this->module_info = $module_info;
                    Context::set('module_info',$module_info);
                }
            }
            // Get a list of module categories
            $module_category = $oModuleModel->getModuleCategories();
            Context::set('module_category', $module_category);
			//Security
			$security = new Security();
			$security->encodeHTML('module_category..title');

			// Set template path for admin view pages
            $this->setTemplatePath($this->module_path.'tpl');
		}		
		
		/**
         * @brief Manage a list of $PARAM_MODULE_NAME instances
         **/
        function disp$PARAM_MODULE_NAME_UCAdminContent() {
            $args->sort_index = "module_srl";
            $args->page = Context::get('page');
            $args->list_count = 40;
            $args->page_count = 10;
            $args->s_module_category_srl = Context::get('module_category_srl');

			$s_mid = Context::get('s_mid');
			if($s_mid) $args->s_mid = $s_mid;

			$s_browser_title = Context::get('s_browser_title');
			if($s_browser_title) $args->s_browser_title = $s_browser_title;

            $output = executeQuery('$PARAM_MODULE_NAME.get$PARAM_MODULE_NAME_UCList', $args);
			$oModuleModel = &getModel('module');
			$$PARAM_MODULE_NAME_list = $oModuleModel->addModuleExtraVars($output->data);
            moduleModel::syncModuleToSite($$PARAM_MODULE_NAME_list);

            // To write to a template context:: set
            Context::set('total_count', $output->total_count);
            Context::set('total_page', $output->total_page);
            Context::set('page', $output->page);
            Context::set('$PARAM_MODULE_NAME_list', $output->data);
            Context::set('page_navigation', $output->page_navigation);
			//Security
			$security = new Security();
			$security->encodeHTML('$PARAM_MODULE_NAME_list..browser_title');
			$security->encodeHTML('$PARAM_MODULE_NAME_list..mid');
			$security->encodeHTML('module_info.');

			// Set a template file
            $this->setTemplateFile('index');
        }
		
		/**
         * @brief Display $PARAM_MODULE_NAME_UC admin fnsert form
         **/
        function disp$PARAM_MODULE_NAME_UCAdminInsert() {
            // Get module_srl by GET parameter
            $module_srl = Context::get('module_srl');
            // Get and set module information if module_srl exists
            if($module_srl) {
                $oModuleModel = &getModel('module');
				$columnList = array('module_srl', 'mid', 'module_category_srl', 'browser_title', 'layout_srl', 'use_mobile', 'mlayout_srl');
                $module_info = $oModuleModel->getModuleInfoByModuleSrl($module_srl, $columnList);
                if($module_info->module_srl == $module_srl) Context::set('module_info',$module_info);
                else {
                    unset($module_info);
                    unset($module_srl);
                }
            }
            // Get a layout list
            $oLayoutModel = &getModel('layout');
            $layout_list = $oLayoutModel->getLayoutList();
            Context::set('layout_list', $layout_list);

			$mobile_layout_list = $oLayoutModel->getLayoutList(0,"M");
			Context::set('mlayout_list', $mobile_layout_list);

            $oModuleModel = &getModel('module');
            $skin_list = $oModuleModel->getSkins($this->module_path);
            Context::set('skin_list',$skin_list);

			$mskin_list = $oModuleModel->getSkins($this->module_path, "m.skins");
			Context::set('mskin_list', $mskin_list);

			//Security
			$security = new Security();
			$security->encodeHTML('layout_list..layout');
			$security->encodeHTML('layout_list..title');
			$security->encodeHTML('mlayout_list..layout');
			$security->encodeHTML('mlayout_list..title');

            // Set a template file
            $this->setTemplateFile('$PARAM_MODULE_NAME_insert');
        }
		
		/**
         * @brief Display information about the selected $PARAM_MODULE_NAME instance
         **/
        function disp$PARAM_MODULE_NAME_UCAdminInfo() {
            // Get module_srl by GET parameter
            $module_srl = Context::get('module_srl');
            $module_info = Context::get('module_info');
            // If module_srl value not set just show the index page
            if(!$module_srl) return $this->disp$PARAM_MODULE_NAME_UCAdminContent();
            // If the layout is destined to add layout information haejum (layout_title, layout)
            if($module_info->layout_srl) {
                $oLayoutModel = &getModel('layout');
                $layout_info = $oLayoutModel->getLayout($module_info->layout_srl);
                $module_info->layout = $layout_info->layout;
                $module_info->layout_title = $layout_info->layout_title;
            }
            // Get a layout list
            $oLayoutModel = &getModel('layout');
            $layout_list = $oLayoutModel->getLayoutList();
            Context::set('layout_list', $layout_list);

			$mobile_layout_list = $oLayoutModel->getLayoutList(0,"M");
			Context::set('mlayout_list', $mobile_layout_list);
            // Set a template file

			
			$oModuleModel = &getModel('module');
			$skin_list = $oModuleModel->getSkins($this->module_path);
			Context::set('skin_list',$skin_list);

			//Security
			$security = new Security();
			$security->encodeHTML('layout_list..layout');
			$security->encodeHTML('layout_list..title');
			$security->encodeHTML('mlayout_list..layout');
			$security->encodeHTML('mlayout_list..title');
			$security->encodeHTML('module_info.');

            $this->setTemplateFile('$PARAM_MODULE_NAME_info');
        }
		
		/**
         * @brief Display $PARAM_MODULE_NAME_UC admin delete form
         **/
        function disp$PARAM_MODULE_NAME_UCAdminDelete() {
            $module_srl = Context::get('module_srl');
            if(!$module_srl) return $this->disp$PARAM_MODULE_NAME_UCAdminContent();

            $oModuleModel = &getModel('module');
			$columnList = array('module_srl', 'module', 'mid');
            $module_info = $oModuleModel->getModuleInfoByModuleSrl($module_srl, $columnList);
            Context::set('module_info',$module_info);
            // Set a template file
            $this->setTemplateFile('$PARAM_MODULE_NAME_delete');

			$security = new Security();
			$security->encodeHTML('module_info.');
        }
		
		/**
         * @brief Display rights settings
         **/
        function disp$PARAM_MODULE_NAME_UCAdminGrantInfo() {
            // Common module settings page, call rights
            $oModuleAdminModel = &getAdminModel('module');
            $grant_content = $oModuleAdminModel->getModuleGrantHTML($this->module_info->module_srl, $this->xml_info->grant);
            Context::set('grant_content', $grant_content);

            $this->setTemplateFile('grant_list');

			$security = new Security();
			$security->encodeHTML('module_info.');
        }
		
		/**
         * @brief Display additional settings page
         **/
        function disp$PARAM_MODULE_NAME_UCAdminPageAdditionSetup() {
            // Set your additional content here
			$content = '';
			
            Context::set('setup_content', $content);
            // Set a template file
            $this->setTemplateFile('addition_setup');

			$security = new Security();
			$security->encodeHTML('module_info.');
        }
	}
?>