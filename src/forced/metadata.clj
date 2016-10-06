(ns forced.metadata
  (:require
    [forced.time :refer [rest->inst]]))

(defn- make-cache
  [cache-kw]
  {:store (ref nil)
   :last-update (ref nil)})

(defn- get-cache
  [system ck]
  (get-in system [:cache ck]))

(defn- fetch-sobject-list
  [system]
  (let
    [[lu-str lu-inst] @(:last-update (get-cache system :sobject-list))]
    (deref
      ((:rest-request system)
       {:headers (merge {} (when lu-str {"If-Modified-Since" lu-str}))
        :url ["services" "data" "v37.0" "sobjects"]}))))

(defn- fetch-sobject-description
  [system sobject-name]
  @((:rest-request system)
    {:url ["services" "data" @(:api-version system)
           "sobjects" sobject-name "describe"]}))

(defn list-sobjects
  [system]
  (let [{:keys [store last-update]} (get-cache system :sobject-list)
        response (fetch-sobject-list system)]
    (when (= (:status response) 200)
      (dosync
        (ref-set store (get-in response [:body :sobjects]))
        (ref-set
          last-update
          (let [lm-str (get-in response [:headers :last-modified])]
            [lm-str (rest->inst lm-str)]))))
    (deref store)))

(defn describe-sobject
  [system sobject-name]
  (let [{:keys [store last-update]} (get-cache system :sobject-metadata)
        response (fetch-sobject-description system sobject-name)]

    ))

