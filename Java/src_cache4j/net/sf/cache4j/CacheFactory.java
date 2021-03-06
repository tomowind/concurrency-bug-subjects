/* =========================================================================
 * File: $Id: $CacheFactory.java,v$
 *
 * Copyright (c) 2006, Yuriy Stepovoy. All rights reserved.
 * email: stepovoy@gmail.com
 *
 * =========================================================================
 */

package net.sf.cache4j;

import net.sf.cache4j.Cache;
import net.sf.cache4j.CacheConfig;
import net.sf.cache4j.CacheException;
import net.sf.cache4j.impl.Configurator;

import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

/**
 * ����� CacheFactory ��������� ������������ �����.
 *
 * @version $Revision: 1.0 $ $Date:$
 * @author Yuriy Stepovoy. <a href="mailto:stepovoy@gmail.com">stepovoy@gmail.com</a>
 **/

public class CacheFactory {
// ----------------------------------------------------------------------------- ���������
// ----------------------------------------------------------------------------- �������� ������
// ----------------------------------------------------------------------------- ����������� ����������

    /**
     * ����� � ������������ �����
     */
    private Map _cacheMap;

    /**
     * ����� ������������ �������� ����
     */
    private CacheCleaner _cleaner;

    /**
     * Singleton
     */
//    private static CacheFactory _cacheFactory = new CacheFactory();
    private static CacheFactory _cacheFactory = null;

// ----------------------------------------------------------------------------- ������������

    /**
     * �����������
     */
    public CacheFactory() {
    	_cacheMap = null;
    	_cleaner = null;
        _cacheMap = new HashMap();
        _cleaner = new CacheCleaner(30000); //default 30sec
        _cleaner.start();
    }

// ----------------------------------------------------------------------------- Public ������

    /**
     * ���������� ��������� CacheFactory
     */
    public static CacheFactory getInstance(){
        return _cacheFactory;
    }
    
    /**
     * added for falcon testing - 2009/04/08
     */
    public static void newInstance() {
    	_cacheFactory = null;
    	_cacheFactory = new CacheFactory();
    }
    
    /**
     * added for falcon testing - 2009/04/20
     * 2009/05/09 -- it doesn't work. I dunno why. 
     * It will make some noise, I guess....
     */
    public static void join() {
    	try {
    		_cacheFactory._cleaner.falcon_stop_cleaner = true;
    		while (_cacheFactory._cleaner != null && _cacheFactory._cleaner.isAlive()) {
    			_cacheFactory._cleaner.falcon_stop_cleaner = true;
    			_cacheFactory._cleaner.join();
    		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

    /**
     * ��������� ������ ����� �� xml ������������, ��� ������� CacheFactory.
     * @param in ������� ����� � xml �������������
     * @throws CacheException
     */
    public void loadConfig(InputStream in) throws CacheException {
        Configurator.loadConfig(in);
    }

    /**
     * ��������� ���. ��� ����� ���������� Cache ������ ������������� ��������� ManagedCache.
     * @param cache ���
     * @throws NullPointerException ���� cache==null ��� cache.getCacheConfig()==null
     * ��� cache.getCacheConfig().getCacheId()==null
     * @throws CacheException ���� ��� ��� ���������� ��� ���� ����������� ��� ��
     * ��������� ��������� ManagedCache
     */
    public void addCache(Cache cache) throws CacheException {
        if(cache==null){
            throw new NullPointerException("cache is null");
        }
        CacheConfig cacheConfig = cache.getCacheConfig();
        if(cacheConfig==null) {
            throw new NullPointerException("cache config is null");
        }
        if(cacheConfig.getCacheId()==null) {
            throw new NullPointerException("config.getCacheId() is null");
        }
        if(!(cache instanceof Cache)) {
            throw new CacheException("cache not instance of "+ManagedCache.class.getName());
        }

        synchronized(_cacheMap){
            if(_cacheMap.containsKey(cacheConfig.getCacheId())) {
                throw new CacheException("Cache id:"+cacheConfig.getCacheId()+" exists");
            }

            _cacheMap.put(cacheConfig.getCacheId(), cache);
        }
    }

    /**
     * ���������� ���
     * @param cacheId ������������� ����
     * @throws NullPointerException ���� cacheId==null
     */
    public Cache getCache(Object cacheId) throws CacheException {
        if(cacheId==null) {
            throw new NullPointerException("cacheId is null");
        }

        synchronized(_cacheMap){
            return (Cache)_cacheMap.get(cacheId);
        }
    }

    /**
     * ������� ���
     * @param cacheId ������������� ����
     * @throws NullPointerException ���� cacheId==null
     */
    public void removeCache(Object cacheId) throws CacheException {
        if(cacheId==null) {
            throw new NullPointerException("cacheId is null");
        }

        synchronized(_cacheMap){
            _cacheMap.remove(cacheId);
        }
    }

    /**
     * ���������� ������ � ���������������� �����
     */
    public Object[] getCacheIds() {
        synchronized(_cacheMap) {
            return _cacheMap.keySet().toArray();
        }
    }

    /**
     * ������������� �������� ������� ����
     * @param time ���������� �����������
     */
    public void setCleanInterval(long time) {
        _cleaner.setCleanInterval(time);
    }

// ----------------------------------------------------------------------------- Package scope ������
// ----------------------------------------------------------------------------- Protected ������
// ----------------------------------------------------------------------------- Private ������
// ----------------------------------------------------------------------------- Inner ������

}

/*
$Log: CacheFactory.java,v $
*/
