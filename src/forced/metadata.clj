(ns forced.metadata)

(defn- get-cache
  [system ck]
  (get-in system [:cache ck]))

(defn- fetch-sobject-list
  [system]
  (let
    [last-update @(:last-update (get-cache system :sobject-list))]
    (deref
      ((:rest-request system)
       {:headers (merge {} (when last-update {"If-Modified-Since" last-update}))
        :url ["services" "data" "v37.0" "sobjects"]}))))

(defn- fetch-sobject-description
  [system sobject-name]
  )

(defn list-sobjects
  [system]
  (let [{:keys [store last-update]} (get-cache system :sobject-list)
        response (fetch-sobject-list system)]
    (when (= (:status response) 200)
      (dosync
        (ref-set store (get-in response [:body :sobjects]))
        (ref-set last-update (get-in response [:headers :last-modified]))))
    (deref store)))

(defn describe-sobject
  [system sobject-name]
  (let [{:keys [store last-update]} (get-cache system :sobject-metadata)
        response (fetch-sobject-description system sobject-name)]
    
    ))

