(ns forced.cache)

(defprotocol CacheStore
  (store! [cache-store d])
  (fetch [cache-store])
  (invalidate! [cache-store]))

(deftype AtomCacheStore [mutable]
  CacheStore
  (store! [_ d] (reset! mutable d))
  (fetch [_] (deref mutable))
  (invalidate! [_] (reset! mutable nil)))


(comment
  
  (def cache (AtomCacheStore. (atom nil)))
  (store! cache "foo")
  (fetch cache)
  (invalidate! cache)

  )
