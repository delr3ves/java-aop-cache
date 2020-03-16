package com.emaginalabs.cache;

import com.emaginalabs.cache.fixture.CacheConfigBuilder;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertNotNull;

/**
 * @author Sergio Arroyo - @delr3ves
 */
public class CacheConfigTest {

    public static final String IRRELEVANT_NAMESPACE = "irrelevantNamespace";

    @Test
    public void whenCreateEmptyConfigShouldCreateDefaultNamespace() {
        CacheConfig config = new CacheConfig();
        assertNotNull(config.get(Cached.DEFAULT_NAMESPACE));
    }

    @DataProvider(name = "populatedConfigProvider")
    public Object[][] populatedConfigProvider() {
        return new Object[][]{
                {CacheConfigBuilder.createConfigForNamespace(IRRELEVANT_NAMESPACE)}
        };
    }

    @Test(dataProvider = "populatedConfigProvider")
    public void whenCreateConfigWithAnyWorkspaceItShouldContainsTheConfiguration(CacheConfig cacheConfig) {
        assertNotNull(cacheConfig.get(IRRELEVANT_NAMESPACE));
    }

    @Test(dataProvider = "populatedConfigProvider")
    public void whenCreateConfigWithDifferentNamespaceShouldAddDefaultNamespace(CacheConfig cacheConfig) {
        assertNotNull(cacheConfig.get(Cached.DEFAULT_NAMESPACE));
    }

    @Test(dataProvider = "populatedConfigProvider")
    public void whenCreateConfigWithDifferentNamespaceShouldContainsTwoConfigs(CacheConfig cacheConfig) {
        assertThat(cacheConfig.size(), equalTo(2));
    }

    @DataProvider(name = "populatedDefaultConfigProvider")
    public Object[][] populatedDefaultConfigProvider() {
        return new Object[][]{
                {CacheConfigBuilder.createConfigForNamespace(Cached.DEFAULT_NAMESPACE)}
        };
    }

    @Test(dataProvider = "populatedDefaultConfigProvider")
    public void whenCreateConfigWithDefaultNamespaceItShouldOnlyContainsOneConfig(CacheConfig cacheConfig) {
        assertThat(cacheConfig.size(), equalTo(1));
    }

    @Test(dataProvider = "populatedDefaultConfigProvider")
    public void whenCreateConfigWithDefaultNamespaceShouldNotAddItAgain(CacheConfig cacheConfig) {
        CacheNamespaceConfig cacheNamespaceConfig = cacheConfig.get(Cached.DEFAULT_NAMESPACE);
        assertThat(cacheNamespaceConfig.getProvider(), equalTo(CacheNamespaceConfig.CacheProvider.EHCACHE));
    }

}
