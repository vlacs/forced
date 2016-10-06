(ns forced.time
  (:require
    [clj-time.format :refer [parse unparse formatter]]
    [clj-time.coerce :refer [from-date to-timestamp]]))

(def rest-time-formatter (formatter "E, d MMM Y H:m:s z"))

(defn rest->inst [i] (to-timestamp (parse rest-time-formatter i)))
(defn inst->rest [i] (unparse rest-time-formatter (from-date i)))

