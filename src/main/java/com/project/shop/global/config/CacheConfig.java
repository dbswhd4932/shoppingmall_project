package com.project.shop.global.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
    private net.sf.ehcache.CacheManager createCacheManager() {
        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
        configuration.diskStore(new DiskStoreConfiguration().path("java.io.tmpdir"));
        return net.sf.ehcache.CacheManager.create(configuration);
    }

    @Bean
    public EhCacheCacheManager ehCacheCacheManager() {

        net.sf.ehcache.CacheManager manager = this.createCacheManager();

   /*
     name : 코드에서 사용할 캐시 name
     maxEntriesLocalHeap : 메모리에 생성 될 최대 캐시 갯수
     maxEntriesLocalDisk : 디스크에 생성 될 최대 캐시 갯수
     eternal : 영속성 캐시 설정(지워지는 캐시인지?) true 이면 timeToldleSecond, timeToLiveSeconds 설정이 무시
     diskSpoolBufferSizeMB :스풀버퍼에 대한 디스크(DiskStore) 크기 설정한다.
     timeToIdleSeconds : 해당 초 동안 캐시가 호출 되지 않으면 삭제
     timeToLiveSeconds : 해당 초가 지나면 캐시가 삭제
     memoryStoreEvictionPolicy : 캐시의 객체 수가 maxEntriesLocalHeap 에 도달하면 객체를 추가하고 제거하는 정책 설정
     LRU : 가장 오랫동안 호출 되지 않은 캐시를 삭제, LFU : 호출 빈도가 가장 적은 캐시를 삭제 FIFO : 캐시가 생성된 순서대로 가장 오래된 캐시를 삭제
     transactionalMode : 트랜잭션 모드 설정
   */

        Cache getMenuCache = new Cache(new CacheConfiguration()
                .maxEntriesLocalHeap(10000)
                .maxEntriesLocalDisk(1000)
                .eternal(false)
                .timeToIdleSeconds(10)
                .timeToLiveSeconds(600)
                .memoryStoreEvictionPolicy("LFU")
                .transactionalMode(CacheConfiguration.TransactionalMode.OFF)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP))
                .name("goodsFind")
        );
        manager.addCache(getMenuCache);

        return new EhCacheCacheManager(manager);
    }
}
