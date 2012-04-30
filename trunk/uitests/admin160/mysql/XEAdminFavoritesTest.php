<?php
include_once dirname(__FILE__) . '/XEAdminBaseTest.php';

class XEAdminFavoritesTest extends XEAdminBaseTest
{
    /**
     * Test module list page is loaded propely
     */
    public function testLoaded()
    {
        $site_url = self::$oXESelenium->getSiteUrl();

        // Go to the modules list page
        $modules_list = $site_url . '/index.php?module=admin&act=dispModuleAdminContent';
        self::$oXESelenium->goUrl($modules_list);

        // Check it was rendered properly
        $webdriver = self::$oXESelenium->getWebDriver();
        $element =  $webdriver->findElementBy(LocatorStrategy::xpath, "//td[text()[contains(.,'addon')]]");
        $this->assertNotNull($element);
    }


    /**
     * Test assumes user is logged in, since that's what the installation does
     * @depends testLoaded
     */
    public function testAddAdminFavorite()
    {
        $webdriver = self::$oXESelenium->getWebDriver();
        // Get the star for addons (should be unchecked, class favOff)
        $addon_favourite_star = $webdriver->findElementBy(LocatorStrategy::xpath, "//div[@id='content']/div/div/table/tbody/tr[td//text()[contains(.,'modules/addon')]]/td[1]/button[@class='fvOff']");
        $addon_favourite_star->click();

        // Page should refresh
        sleep(2);

        // Check that star is now selected (class favOn)
        $addon_favourite_star = $webdriver->findElementBy(LocatorStrategy::xpath, "//div[@id='content']/div/div/table/tbody/tr[td//text()[contains(.,'modules/addon')]]/td[1]/button[@class='fvOn']");
        $this->assertNotNull($addon_favourite_star);

        // Check that an item exists inside Favorites in the top menu
        $top_menu_favorites_list = $webdriver->findElementBy(LocatorStrategy::xpath, "//li[a/img[@src[contains(.,'menu_icon_favorites')]]]/ul/li[1]/form");
        $this->assertNotNull($top_menu_favorites_list);
    }
}