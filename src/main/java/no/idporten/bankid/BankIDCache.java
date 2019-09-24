package no.idporten.bankid;

import no.bbs.server.vos.BIDSessionData;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class BankIDCache {

    private final CacheManager cacheManager;

    @Autowired
    public BankIDCache(@Qualifier("ehcacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getSID(String uuid) {
        Cache<String, String> uuidSidCache = cacheManager.getCache("uuidSid", String.class, String.class);
        return uuidSidCache.get(uuid);
    }

    public void removeUuidSID(String uuid) {
        Cache<String, String> uuidSidCache = cacheManager.getCache("uuidSid", String.class, String.class);
        uuidSidCache.remove(uuid);
    }

    public void putSID(String uuid, String sid) {
        Cache<String, String> uuidSidCache = cacheManager.getCache("uuidSid", String.class, String.class);
        uuidSidCache.put(uuid, sid);
    }

    public String getSSN(String sid) {
        Cache<String, String> sidSSNCache = cacheManager.getCache("sidSSN", String.class, String.class);
        return sidSSNCache.get(sid);
    }

    public void putSSN(String sid, String ssn) {
        Cache<String, String> sidSSNCache = cacheManager.getCache("sidSSN", String.class, String.class);
        sidSSNCache.put(sid, ssn);
    }

    public BIDSessionData getBIDSessionData(String sid) {
        Cache<String, BIDSessionData> bidSessionCache = cacheManager.getCache("bidSession", String.class, BIDSessionData.class);
        return bidSessionCache.get(sid);
    }

    public void putBIDSessionData(String sid, BIDSessionData ssn) {
        Cache<String, BIDSessionData> bidSessionCache = cacheManager.getCache("bidSession", String.class, BIDSessionData.class);
        bidSessionCache.put(sid, ssn);
    }

    public void putOCSP(String sid, byte[] ocsp) {
        Cache<String, byte[]> ocspCache = cacheManager.getCache("ocsp", String.class, byte[].class);
        ocspCache.put(sid, ocsp);
    }

    public byte[] getOCSP(String sid) {
        Cache<String, byte[]> ocspCache = cacheManager.getCache("ocsp", String.class, byte[].class);
        return ocspCache.get(sid);
    }

    public void putTraceId(String sid, String traceId) {
        Cache<String, String> traceIdCache = cacheManager.getCache("traceId", String.class, String.class);
        traceIdCache.put(sid, traceId);
    }

    public String getTraceId(String sid) {
        Cache<String, String> traceIdCache = cacheManager.getCache("traceId", String.class, String.class);
        return traceIdCache.get(sid);
    }

    public void removeSession(String sid) {
        Cache<String, String> traceIdCache = cacheManager.getCache("traceId", String.class, String.class);
        traceIdCache.remove(sid);
        Cache<String, String> sidSSNCache = cacheManager.getCache("sidSSN", String.class, String.class);
        sidSSNCache.remove(sid);
        Cache<String, byte[]> ocspCache = cacheManager.getCache("ocsp", String.class, byte[].class);
        ocspCache.remove(sid);
        Cache<String, BIDSessionData> bidSessionCache = cacheManager.getCache("bidSession", String.class, BIDSessionData.class);
        bidSessionCache.remove(sid);
    }

}
