java-aop-cache [![Build Status](https://api.travis-ci.org/repositories/delr3ves/java-aop-cache.svg)](https://travis-ci.org/delr3ves/java-aop-cache) [![Coverage Status](https://coveralls.io/repos/delr3ves/java-aop-cache/badge.png?branch=master)](https://coveralls.io/r/delr3ves/java-aop-cache?branch=master)
==============

Provides utilities to cache methods in an aspect oriented way.


## Getting started

First of all you'll need to add the dependency to your project:

Maven:

    <dependency>
        <groupId>com.emaginalabs</groupId>
        <artifactId>aop-cache</artifactId>
        <version>${aop-cache-version}</version>
    </dependency>


Gradle:

    compile 'com.emaginalabs:aop-cache:${aop-cache-version}'

SBT:

    libraryDependencies += "com.emaginalabs" % "aop-cache" % "${aop-cache-version}"

Once you have the dependency, you'll need to install the cache module:

##Usage
Once you have the library installed you can cache your methods adding the proper annotation:

    @Cached
    public List<User> executeMyVeryComplexQuery() {
        //your expensive query here
    }

    //you can even cache the exceptions...

    @Cached(namespace="myCacheNamespace", cachedExceptions={UserNotFoundException.class, ...})
    public User findUser(String userId) {
        //find your user here
    }

You'll also need to install the proper module in your GuiceModule configuration:

    public void configure() {
        ...
        install(new AOPCacheGuiceModule(cacheConfig, metricRegistry));
        ...
    }

##Configuration
      cacheConfig:
        Default:
          provider: ehcache
          resultCacheTtl: 30
          resultCacheTimeUnit: SECONDS
          resultCacheSize: 50000
          errorCachettl: 500
          errorCacheTimeUnit: MILLISECONDS
          errorCacheSize: 5000
          
    
        DAOLayer:
          provider: guava
          resultCacheTtl: 30
          resultCacheTimeUnit: SECONDS
    
        RequestLayer:
           ...
           
Default values are:

    public static final int DEFAULT_MAXIMUM_SIZE = 50000;
    public static final int DEFAULT_EXPIRATION = 600;
    public static final int DEFAULT_EXCEPTION_EXPIRATION = 300;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;
    public static final CacheProvider DEFAULT_PROVIDER = CacheProvider.GUAVA;


All values are optional so you can configure only the fields you want to change.

### Annotation Parameters
You can configure both the namespace and the allowed cached exceptions:

* **namespace:** The purpose of this parameter is being able to configure different caches for different purposes so we
can for example cache for a long time those methods which are very costly and changes a lot or cache for example a few 
millis for a "request time" cache

* **cachedExceptions:** Will contains the list of parent exceptions to be cached. We use hierarchy in order to be able to
cache more than one exception in a single way. So we can cache for example all Checked exceptions just adding *Exception* to the list.

## Considerations

As long as we use Google Guice as AOP framework, **you'll need to create your cacheable classes with Guice.**

For this first version, both Google's Guava and EhCache implementations are stored in memory. 
It makes the cache very fast but you should take into account that your process can consume a lot
of memory if you do not configure the cache properly.
