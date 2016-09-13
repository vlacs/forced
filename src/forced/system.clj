(ns forced.system)

(def ^:dynamic *auth-config* nil)
(def ^:dynamic *oauth-session* nil)
(def ^:dynamic *api-version* nil)

(def ^:dynamic *http-agent* "Forced 1.0 (clojure,http-kit)")
(def ^:dynamic *keepalive-duration* 30000)
(def ^:dynamic *timeout-duration* 30000)
